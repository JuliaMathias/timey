package com.juliamathias.timey.domain.playback

/** Elapsed-time source; Android uses elapsedRealtime, tests advance a fake without sleeping. */
fun interface MonotonicClock {
    /** Returns integer milliseconds from a monotonic origin. */
    fun nowMs(): Long
}

/** Immutable phase of the diagnostic fixture; selected names are spoken at their boundaries. */
data class RepPhase(val name: String, val durationMs: Long, val speak: Boolean = false)

/** Bounded P0 fixture, deliberately separate from the future portable routine schema. */
data class RepFixture(val name: String, val reps: Int, val phases: List<RepPhase>) {
    init {
        require(reps in 1..100 && phases.size in 1..16)
        require(phases.all { it.durationMs in 1..60_000 && it.name.isNotBlank() })
    }
    val repMs: Long = phases.sumOf { it.durationMs }
    val totalMs: Long = repMs * reps
}

/** Fixed disposable examples; editing/persistence and full step navigation belong to P1/P2. */
object PrototypeFixtures {
    val a = RepFixture("A · 10 × 2.2 seconds", 10, listOf(RepPhase("Pace", 2_200)))
    val b = RepFixture("B · Down 2s / Up 4s", 10,
        listOf(RepPhase("Down", 2_000), RepPhase("Up", 4_000, true)))
    val short = RepFixture("Short · Down / Up 0.2s", 10,
        listOf(RepPhase("Lower slowly with control", 200, true),
            RepPhase("Raise slowly with control", 200, true)))
}

/** Playback intent, independent of UI attachment or speech completion. */
enum class RepStatus { IDLE, RUNNING, PAUSED, FINISHED, STOPPED }

/** Renderable immutable state; rep N stays visible for its entire final duration. */
data class RepState(val status: RepStatus, val elapsedMs: Long, val rep: Int, val phase: Int)

/** One grouped utterance; its due time permits dispatch-lateness measurements. */
data class RepCue(val rep: Int, val phaseName: String?, val dueMs: Long, val includeNumber: Boolean)

/** Platform boundary. Replace must interrupt old speech; cancel invalidates asynchronous callbacks. */
interface RepCueAdapter {
    /** Replaces the current utterance without waiting for it to finish. */
    fun replace(cue: RepCue)
    /** Cancels queued/current audio and invalidates its callbacks. */
    fun cancel()
}

/** Single-threaded monotonic engine; delayed polls reconcile position and discard missed speech. */
class RepPrototype(private val clock: MonotonicClock, private val cues: RepCueAdapter) {
    private var fixture = PrototypeFixtures.a
    private var anchorMs = 0L
    private var offsetMs = 0L
    private var lastBoundary = -1L
    var state = RepState(RepStatus.IDLE, 0, 1, 0)
        private set

    /** Copies fixture phases to prevent caller mutation of a running schedule. */
    fun start(value: RepFixture) {
        cues.cancel()
        fixture = value.copy(phases = value.phases.toList())
        offsetMs = 0
        anchorMs = clock.nowMs()
        lastBoundary = -1
        state = RepState(RepStatus.RUNNING, 0, 1, 0)
        poll()
    }

    /** Reconciles every elapsed boundary, emitting only the cue for the current boundary. */
    fun poll(): RepState {
        if (state.status != RepStatus.RUNNING) return state
        val elapsed = (offsetMs + clock.nowMs() - anchorMs).coerceIn(0, fixture.totalMs)
        if (elapsed == fixture.totalMs) {
            state = RepState(RepStatus.FINISHED, elapsed, fixture.reps, fixture.phases.lastIndex)
            cues.cancel()
            return state
        }
        val rep = (elapsed / fixture.repMs).toInt() + 1
        val within = elapsed % fixture.repMs
        var phase = 0
        var phaseStart = 0L
        while (phase < fixture.phases.lastIndex && within >= phaseStart + fixture.phases[phase].durationMs) {
            phaseStart += fixture.phases[phase].durationMs
            phase++
        }
        state = RepState(RepStatus.RUNNING, elapsed, rep, phase)
        val boundary = (rep - 1) * fixture.repMs + phaseStart
        if (boundary != lastBoundary) {
            lastBoundary = boundary
            // Never carry an unfinished phrase into a newer, even silent, phase.
            cues.cancel()
            val selected = fixture.phases[phase]
            if (phase == 0 || selected.speak) {
                cues.replace(RepCue(rep, selected.name.takeIf { selected.speak }, boundary, phase == 0))
            }
        }
        return state
    }

    /** Captures actual elapsed time on pause and cancels speech without producing a late cue. */
    fun pause() {
        if (state.status != RepStatus.RUNNING) return
        // Reconcile silently: pausing must not briefly enqueue a new utterance.
        val elapsed = (offsetMs + clock.nowMs() - anchorMs).coerceIn(0, fixture.totalMs)
        offsetMs = elapsed
        cues.cancel()
        state = position(elapsed, if (elapsed == fixture.totalMs) RepStatus.FINISHED else RepStatus.PAUSED)
    }

    /** Reanchors time after a pause; paused duration and already-spoken boundaries are excluded. */
    fun resume() {
        if (state.status != RepStatus.PAUSED) return
        anchorMs = clock.nowMs()
        lastBoundary = boundaryAt(offsetMs)
        state = state.copy(status = RepStatus.RUNNING)
    }

    /** Restarts this one-step fixture, preserving running/paused intent. */
    fun restart() {
        val paused = state.status == RepStatus.PAUSED
        if (paused) {
            cues.cancel()
            offsetMs = 0
            lastBoundary = 0
            state = RepState(RepStatus.PAUSED, 0, 1, 0)
        } else start(fixture)
    }

    /** Diagnostic next-step navigation reaches the end of this single-step fixture. */
    fun next() {
        cues.cancel()
        state = RepState(RepStatus.FINISHED, fixture.totalMs, fixture.reps, fixture.phases.lastIndex)
    }

    /** Ends this session and leaves no asynchronous cue eligible for acceptance. */
    fun stop() {
        cues.cancel()
        state = RepState(RepStatus.STOPPED, 0, 1, 0)
    }

    /** Projects paused position without scheduling speech. */
    private fun position(elapsed: Long, status: RepStatus): RepState {
        if (elapsed == fixture.totalMs) return RepState(status, elapsed, fixture.reps, fixture.phases.lastIndex)
        val within = elapsed % fixture.repMs
        var phase = 0
        var start = 0L
        while (phase < fixture.phases.lastIndex && within >= start + fixture.phases[phase].durationMs) {
            start += fixture.phases[phase].durationMs
            phase++
        }
        return RepState(status, elapsed, (elapsed / fixture.repMs).toInt() + 1, phase)
    }

    /** Locates the current boundary so resume cannot replay already elapsed cues. */
    private fun boundaryAt(elapsed: Long): Long {
        val phase = position(elapsed, RepStatus.PAUSED).phase
        return elapsed / fixture.repMs * fixture.repMs + fixture.phases.take(phase).sumOf { it.durationMs }
    }
}

/** Main-thread callback fence; an old utterance cannot update diagnostics after replacement. */
class CueGeneration {
    private var generation = 0L
    private var current: String? = null
    /** Invalidates all previously issued identifiers. */
    fun cancel() { generation++; current = null }
    /** Starts a new identity, invalidating an unfinished utterance. */
    fun next(): String { cancel(); return "cue-$generation".also { current = it } }
    /** Accepts callbacks only for the latest active identity. */
    fun accepts(id: String?): Boolean = id != null && id == current
}

/** Conservative preview estimate, not an audio-duration measurement or a minimum pace rule. */
fun previewWarnings(fixture: RepFixture): List<String> = fixture.phases.mapIndexedNotNull { index, phase ->
    val words = phase.name.split(' ').size + if (index == 0) 1 else 0
    val estimateMs = words * 400L + 200L
    if ((index == 0 || phase.speak) && estimateMs > phase.durationMs)
        "Speech may not fit ${phase.durationMs}ms: ${phase.name}. New cues interrupt; pace stays unchanged."
    else null
}
