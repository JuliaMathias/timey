package com.juliamathias.timey.platform.audio

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.juliamathias.timey.domain.playback.CueGeneration
import java.util.Locale

/** Main-thread offline-only TTS owner. Callbacks report measurements, never schedule playback. */
class OfflineSpeech(context: Context, private val report: (String) -> Unit) {
    private val handler = Handler(Looper.getMainLooper())
    private val fence = CueGeneration()
    private var closed = false
    private var initialized = false
    private var language = "en-US"
    private var ready = false
    private var requestMs = 0L
    private var onsetMs = 0L
    private var engine: TextToSpeech? = null

    init {
        engine = TextToSpeech(context.applicationContext) { result ->
            handler.post {
                if (!closed) {
                    initialized = result == TextToSpeech.SUCCESS
                    if (initialized) {
                        engine?.setOnUtteranceProgressListener(listener())
                        select(language)
                    } else report("Speech unavailable. Open Android Text-to-speech settings, install offline English/pt-BR voice data, then reopen Timey. Timer works silently.")
                }
            }
        }
    }

    /** Selects installed non-network voice data; Portuguese must be Brazilian Portuguese. */
    fun select(tag: String) {
        cancel()
        language = tag
        ready = false
        if (!initialized || closed) return
        val locale = Locale.forLanguageTag(tag)
        val voices = engine?.voices.orEmpty().filter {
            !it.isNetworkConnectionRequired &&
                !it.features.orEmpty().contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED) &&
                it.locale.language == locale.language &&
                (locale.language != "pt" || it.locale.country == "BR")
        }.sortedWith(compareBy({ it.locale.country != locale.country }, { it.name }))
        val voice = voices.firstOrNull()
        if (voice != null && engine?.setVoice(voice) == TextToSpeech.SUCCESS) {
            ready = true
            report("Offline voice: ${engine?.defaultEngine} / ${voice.name} / ${voice.locale.toLanguageTag()}")
        } else {
            report("No installed offline $tag voice. Open Android Text-to-speech settings and download voice data while online; reopen Timey. Timer works silently.")
        }
    }

    /** Flushes old speech; initialization/errors never hold up or queue timer events. */
    fun speak(text: String) {
        cancel()
        if (!ready || closed) return
        val id = fence.next()
        requestMs = SystemClock.elapsedRealtime()
        onsetMs = 0
        if (engine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, id) != TextToSpeech.SUCCESS) {
            fence.cancel()
            report("Speech request failed; timer continues silently. Check offline voice setup.")
        }
    }

    /** Invalidates callbacks before stopping the engine, including callbacks already posted. */
    fun cancel() { fence.cancel(); engine?.stop() }

    /** Releases native TTS resources; a late init callback cannot reopen a cleared owner. */
    fun close() { closed = true; cancel(); engine?.shutdown(); engine = null }

    /** Marshals engine callbacks onto the owner thread, fencing stale results after dispatch. */
    private fun listener(): UtteranceProgressListener = object : UtteranceProgressListener() {
        /** Measures engine-reported onset delay; this is not acoustic/Bluetooth latency. */
        override fun onStart(utteranceId: String?) {
            val observed = SystemClock.elapsedRealtime()
            handler.post {
                if (fence.accepts(utteranceId)) {
                    onsetMs = observed
                    report("Speech onset callback: ${observed - requestMs}ms after request")
                }
            }
        }
        /** Reports completed duration without advancing the rep or replaying a cue. */
        override fun onDone(utteranceId: String?) {
            val observed = SystemClock.elapsedRealtime()
            handler.post {
                if (fence.accepts(utteranceId)) {
                    if (onsetMs != 0L) report("Speech completed: ${observed - onsetMs}ms after onset callback")
                    fence.cancel()
                }
            }
        }
        /** Handles the required legacy callback as a current-request failure. */
        @Deprecated("Required by the Android listener contract")
        override fun onError(utteranceId: String?) { error(utteranceId) }
        /** Handles engine-specific errors without attempting stale retries or network voices. */
        override fun onError(utteranceId: String?, errorCode: Int) { error(utteranceId) }
        /** Records only errors belonging to the current generation. */
        private fun error(id: String?) {
            handler.post {
                if (fence.accepts(id)) {
                    ready = false
                    fence.cancel()
                    report("Offline speech failed. Check downloaded voice data; timer continues silently.")
                }
            }
        }
    }
}
