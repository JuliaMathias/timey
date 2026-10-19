package com.juliamathias.timey.ui.prototype

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juliamathias.timey.domain.playback.*
import com.juliamathias.timey.platform.audio.OfflineSpeech
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Snapshot rendered by Compose; diagnostic events are bounded and contain only fixture data. */
data class PrototypeUiState(
    val open: Boolean = false,
    val fixture: RepFixture = PrototypeFixtures.a,
    val language: String = "en-US",
    val playback: RepState = RepState(RepStatus.IDLE, 0, 1, 0),
    val voice: String = "Initializing offline speech…",
    val events: List<String> = emptyList(),
    val preview: Boolean = false,
)

/** Foreground P0 owner survives rotation; #12 replaces its polling owner with a playback service. */
class PrototypeViewModel(application: Application) : AndroidViewModel(application) {
    private val mutable = MutableStateFlow(PrototypeUiState())
    val state = mutable.asStateFlow()
    private var job: Job? = null
    private val speech = OfflineSpeech(application) { message ->
        if (message.startsWith("Speech onset") || message.startsWith("Speech completed")) record(message)
        else mutable.value = mutable.value.copy(voice = message)
    }
    private val engine = RepPrototype(MonotonicClock { SystemClock.elapsedRealtime() }, object : RepCueAdapter {
        /** Groups same-boundary number/name into one phrase and records dispatch lateness. */
        override fun replace(cue: RepCue) {
            val name = translated(cue.phaseName, mutable.value.language)
            val phrase = listOfNotNull(cue.rep.toString().takeIf { cue.includeNumber }, name).joinToString(", ")
            record("Due ${cue.dueMs}ms · dispatch +${engineElapsed() - cue.dueMs}ms · $phrase")
            speech.speak(phrase)
        }
        /** Cancellation invalidates asynchronous speech status, never timing. */
        override fun cancel() { speech.cancel() }
    })

    /** Opens the diagnostic route without starting a workout. */
    fun open() { mutable.value = mutable.value.copy(open = true) }

    /** Stops audio and returns to the foundation route. */
    fun home() { stop(); mutable.value = mutable.value.copy(open = false) }

    /** Configures only while idle/finished/stopped so running snapshots stay immutable. */
    fun configure(fixture: RepFixture = state.value.fixture, language: String = state.value.language) {
        if (engine.state.status == RepStatus.RUNNING || engine.state.status == RepStatus.PAUSED) return
        engine.stop()
        mutable.value = mutable.value.copy(fixture = fixture, language = language, preview = false, playback = engine.state)
        speech.select(language)
    }

    /** Starts the exact fixture or a two-rep preview; warnings never change configured pacing. */
    fun start(preview: Boolean = false) {
        job?.cancel()
        mutable.value = mutable.value.copy(events = emptyList(), preview = preview)
        val fixture = state.value.fixture
        engine.start(if (preview) fixture.copy(reps = 2) else fixture)
        publish()
        launchPolling()
    }

    /** Captures position and stops speech; no duration passes while paused. */
    fun pause() { engine.pause(); job?.cancel(); publish() }

    /** Continues from captured elapsed time without repeating a cancelled cue. */
    fun resume() { engine.resume(); publish(); launchPolling() }

    /** Resets this fixture while preserving paused/running intent. */
    fun restart() { engine.restart(); publish(); launchPolling() }

    /** Skips the sole diagnostic step and cancels its unfinished utterance. */
    fun next() { engine.next(); job?.cancel(); publish() }

    /** Stops all polling/audio without changing selected fixture or language. */
    fun stop() { engine.stop(); job?.cancel(); publish() }

    /** Pauses when leaving the foreground; no background reliability is claimed in #11. */
    fun background() { if (engine.state.status == RepStatus.RUNNING) pause() }

    /** Polls for presentation/cue dispatch; elapsed time, not delay count, determines progression. */
    private fun launchPolling() {
        job?.cancel()
        if (engine.state.status != RepStatus.RUNNING) return
        job = viewModelScope.launch {
            while (engine.state.status == RepStatus.RUNNING) {
                delay(20)
                engine.poll()
                publish()
            }
            record("${engine.state.status} at ${engine.state.elapsedMs}ms active elapsed · finish dispatch +${engine.finishLatenessMs}ms")
        }
    }

    /** Reads current engine state during a cue; initialization happens before any cue is emitted. */
    private fun engineElapsed(): Long = engine.state.elapsedMs

    /** Publishes immutable playback state for UI observers. */
    private fun publish() { mutable.value = mutable.value.copy(playback = engine.state) }

    /** Keeps the latest forty events so short-phase diagnostics cannot grow indefinitely. */
    private fun record(message: String) {
        mutable.value = mutable.value.copy(events = (mutable.value.events + message).takeLast(40))
    }

    /** Maps fixed fixture labels to pt-BR while preserving their timing and selection. */
    private fun translated(name: String?, language: String): String? {
        if (language != "pt-BR") return name
        return when (name) {
            "Down" -> "Descer"
            "Up" -> "Subir"
            "Lower slowly with control" -> "Desça devagar com controle"
            "Raise slowly with control" -> "Suba devagar com controle"
            else -> name
        }
    }

    /** Releases TTS and the polling job when the Activity owner is permanently removed. */
    override fun onCleared() { job?.cancel(); engine.stop(); speech.close() }
}
