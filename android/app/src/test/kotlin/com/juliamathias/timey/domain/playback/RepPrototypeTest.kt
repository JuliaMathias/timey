package com.juliamathias.timey.domain.playback

import org.junit.Assert.*
import org.junit.Test

/** Exercises outcomes with controllable elapsed time and a fake cue adapter, never Android/TTS. */
class RepPrototypeTest {
    /** Clock advances only when a case explicitly changes its time. */
    private class Clock(var time: Long = 0) : MonotonicClock {
        /** Returns the controlled monotonic timestamp. */
        override fun nowMs() = time
    }
    /** Records replacement/cancellation for observable cue behavior. */
    private class Cues : RepCueAdapter {
        val emitted = mutableListOf<RepCue>()
        var cancellations = 0
        /** Records each utterance selected by the engine. */
        override fun replace(cue: RepCue) { emitted.add(cue) }
        /** Records cancellation, including silent boundaries and terminal transitions. */
        override fun cancel() { cancellations++ }
    }
    private val clock = Clock()
    private val cues = Cues()
    private val engine = RepPrototype(clock, cues)

    /** Decimal pace starts at rep one and cannot finish at the start of rep ten. */
    @Test fun fractionalPaceAndFinalRepDuration() {
        engine.start(PrototypeFixtures.a)
        assertEquals(1, engine.state.rep)
        assertEquals(0L, cues.emitted.single().dueMs)
        clock.time = 2_199; assertEquals(1, engine.poll().rep)
        clock.time = 2_200; assertEquals(2, engine.poll().rep)
        clock.time = 19_800; assertEquals(10, engine.poll().rep)
        assertEquals(RepStatus.RUNNING, engine.state.status)
        clock.time = 21_999; assertEquals(RepStatus.RUNNING, engine.poll().status)
        clock.time = 22_000; assertEquals(RepStatus.FINISHED, engine.poll().status)
    }

    /** Selected Up begins exactly at 2s; number begins at rep start and repeats at 6s. */
    @Test fun phaseBoundariesAndSelectedCue() {
        engine.start(PrototypeFixtures.b)
        assertNull(cues.emitted.last().phaseName)
        clock.time = 1_999; assertEquals(0, engine.poll().phase)
        clock.time = 2_000; assertEquals(1, engine.poll().phase)
        assertEquals(RepCue(1, "Up", 2_000, false), cues.emitted.last())
        clock.time = 6_000; assertEquals(2, engine.poll().rep)
        assertEquals(RepCue(2, null, 6_000, true), cues.emitted.last())
        clock.time = 60_000; assertEquals(RepStatus.FINISHED, engine.poll().status)
    }

    /** Delayed callbacks reconcile multiple reps but never create a missed-cue backlog. */
    @Test fun delayedPollDropsObsoleteSpeechAndDoesNotDuplicate() {
        engine.start(PrototypeFixtures.b)
        clock.time = 20_100; engine.poll()
        assertEquals(4, engine.state.rep)
        assertEquals(1, engine.state.phase)
        assertEquals(2, cues.emitted.size)
        assertEquals(20_000L, cues.emitted.last().dueMs)
        engine.poll(); assertEquals(2, cues.emitted.size)
    }

    /** Pause captures time even between polls; a five-second pause does not consume duration. */
    @Test fun pauseResumePreservesPositionWithoutReplay() {
        engine.start(PrototypeFixtures.b)
        clock.time = 1_500; engine.pause()
        assertEquals(1_500L, engine.state.elapsedMs)
        clock.time = 6_500; engine.resume(); engine.poll()
        assertEquals(1_500L, engine.state.elapsedMs)
        assertEquals(1, cues.emitted.size)
        clock.time = 7_000; engine.poll()
        assertEquals(1, engine.state.phase)
        assertEquals(2, cues.emitted.size)
    }

    /** Pausing after a missed boundary must cancel rather than briefly emit that cue. */
    @Test fun pauseAcrossBoundaryAndResumeDoesNotReplayIt() {
        engine.start(PrototypeFixtures.b)
        clock.time = 2_100; engine.pause()
        assertEquals(1, engine.state.phase)
        assertEquals(1, cues.emitted.size)
        clock.time = 8_000; engine.resume(); engine.poll()
        assertEquals(1, cues.emitted.size)
    }

    /** Navigation cancels speech and paused restart preserves user intent. */
    @Test fun navigationRestartStopCancel() {
        engine.start(PrototypeFixtures.a)
        clock.time = 500; engine.pause()
        engine.restart(); assertEquals(RepStatus.PAUSED, engine.state.status)
        assertEquals(0L, engine.state.elapsedMs)
        engine.resume(); clock.time = 2_700; engine.poll()
        assertEquals(2, engine.state.rep)
        val cancels = cues.cancellations
        engine.next(); assertTrue(cues.cancellations > cancels)
        assertEquals(RepStatus.FINISHED, engine.state.status)
        engine.restart(); assertEquals(1, engine.state.rep)
        engine.stop(); assertEquals(RepStatus.STOPPED, engine.state.status)
        val count = cues.emitted.size
        clock.time += 10_000; engine.poll(); assertEquals(count, cues.emitted.size)
    }

    /** Same-boundary number and phase are grouped; short labels warn without rejecting pace. */
    @Test fun simultaneousCueGroupingAndPreviewWarning() {
        engine.start(PrototypeFixtures.short)
        assertEquals(RepCue(1, "Lower slowly with control", 0, true), cues.emitted.single())
        assertEquals(2, previewWarnings(PrototypeFixtures.short).size)
        clock.time = 200; engine.poll()
        assertEquals(2, cues.emitted.size)
        clock.time = 4_000; assertEquals(RepStatus.FINISHED, engine.poll().status)
    }

    /** Cancellation and replacement fence out obsolete asynchronous callbacks. */
    @Test fun staleCallbackRejection() {
        val fence = CueGeneration()
        val old = fence.next(); assertTrue(fence.accepts(old))
        val newest = fence.next(); assertFalse(fence.accepts(old)); assertTrue(fence.accepts(newest))
        fence.cancel(); assertFalse(fence.accepts(newest)); assertFalse(fence.accepts(null))
    }

    /** Running execution is detached from later mutation of a caller's phase list. */
    @Test fun immutableSnapshot() {
        val phases = mutableListOf(RepPhase("Pace", 2_200))
        engine.start(RepFixture("Copy", 10, phases))
        phases[0] = RepPhase("Changed", 1)
        clock.time = 2_199; assertEquals(1, engine.poll().rep)
    }

    /** Invalid and unbounded diagnostic inputs cannot create playable schedules. */
    @Test fun invalidFixturesRejected() {
        for (phases in listOf(emptyList(), listOf(RepPhase("Pace", 0)), listOf(RepPhase("", 1)))) {
            assertThrows(IllegalArgumentException::class.java) { RepFixture("Invalid", 10, phases) }
        }
        assertThrows(IllegalArgumentException::class.java) { RepFixture("Invalid", 0, PrototypeFixtures.a.phases) }
    }
}
