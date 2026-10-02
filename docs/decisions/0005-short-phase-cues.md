# 0005: Prefer current cues over unfinished speech without slowing the timer

Date: 2026-10-01. Status: accepted user policy; implementation pending. Related requirements: R06-R07, R09, R11 in [PRODUCT.md](../PRODUCT.md). Complements [0003: voice timing](0003-voice-timing.md); does not supersede its semantics.

## Context and decision

An announcement can last longer than a configured rep phase. The user accepted allowing short durations, warning in preview when speech will not fit, and interrupting unfinished speech in favor of the newest cue. Timer pace and configured phase boundaries remain unchanged.

## Reason and alternatives

The accepted proposal keeps exercise timing independent of voice length and prioritizes information for the current phase. Waiting for speech to finish would alter the configured pace; queuing every announcement would produce obsolete instructions. Rejecting short phases solely because a phrase is long would restrict otherwise valid routines. These alternatives were not chosen.

## Consequences and validation

The engine emits timestamped cue events independently of speech completion. The audio adapter cancels/replaces obsolete utterances; asynchronous completion/cache callbacks must not replay an old event. Group number and phase information due at one boundary into a single utterance so simultaneous cues do not preempt one another.

Preview identifies speech-fit issues without silently extending durations. Positive-duration validation still applies. P0 measures real offline TTS latency and cancellation in English and Brazilian Portuguese on both phones; P3 implements preview warnings and cue replacement. Test an announcement spanning a later cue, simultaneous number/phase events, pause/skip/stop, stale callbacks and unchanged engine progression. Minimum representable duration and measurable timing tolerances are implementation details to validate, not promises of sample-accurate speech.

## Validation scope update — 2026-10-02

The user subsequently limited required physical testing to Phone 2 (Galaxy S22 Ultra) because Phone 1 has a damaged USB port. Earlier two-phone validation statements above are superseded by the current device policy in [PRODUCT.md](../PRODUCT.md) and [TESTING.md](../TESTING.md). The architectural decision and Phone 1 compatibility requirement remain unchanged; no Phone 1 hardware result is inferred.
