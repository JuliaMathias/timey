# Execution plan: #11 phased reps and offline speech

Status: active. Owner: primary agent. Base revision: 73e2101. Branch: `codex/issue-11-phased-reps-speech`.

## Scope and approach

R06–R09/R11/R23 P0 diagnostic slice only. Fixture A: 10 × 2200ms. Fixture B: 10 × (Down 2000ms + selected Up 4000ms). Short fixture: ten reps with two 200ms long-label phases. English default; pt-BR option. Immutable schedule and injected monotonic clock; grouped cues, cancellation and stale-callback fencing. No portable schema/editor/storage or background reliability claim: #12 owns service/notification/overlay integration.

## Progress and validation

Instructions, requirements, plan, architecture, workflow, Android guidance and testing strategy read. GitHub #9 closed with Phone 2 acceptance; #10 complete. Current main verified against origin. Untracked Studio `android/gradle/gradle-daemon-jvm.properties` is unrelated and excluded. No connected devices at initial inspection.

First increment adds the pure engine and fake-clock tests. Local `testDebugUnitTest lintDebug assembleDebug` passed: 10 JVM behavioral cases, no failures. CI awaits PR. Physical timing/offline speech and measured tolerance remain not run until Phone 2 evidence exists. Phone 1 is compatibility-only, hardware behavior unverified.

## Recovery and next steps

Use README's command-scoped Studio Java/SDK build. Never clear phone data. Add the foreground-only diagnostic UI and offline TTS adapter, then update issue #11 with actual screen labels, exact fixture procedures and result recording. PR #21 is open as a draft with its Development association verified; verify fresh CI for each revision. Leave PR/issue open for missing physical evidence and user merge.

## Android adapter increment

Foreground diagnostic route, StateFlow/ViewModel ownership across rotation, pause on leaving foreground, offline-only English/pt-BR selection, queue-flush speech and callback fencing are implemented. Two-rep preview and long-label 200ms phases expose overlap warnings without pace changes. Bounded diagnostics show cue due/dispatch offsets, onset callback delay, completed speech duration and actual finish-dispatch lateness (separate from clamped elapsed display). Missing voice offers setup guidance and silent timing; no voice download or sound library is added.

Local `testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest` passes: 11 JVM cases, no failures/errors/skips. Seven result-guard cases pass. Three new Compose cases compile; hosted execution results are recorded below (five total instrumentation cases). Lint led to the official stable Lifecycle 2.11.0 pin and removal of an empty super call. No Phone 2 connected; physical cases and measured tolerance remain blocked/not run.

## Forward-plan review

Reread P0–P7 and outstanding dependencies. #12 must move session/polling ownership from this foreground ViewModel into a foreground service, retain the pure engine/cue cancellation seam, and verify resource release/notification/overlay on Phone 2. P1 can reuse elapsed-time and callback tests but must implement full routine contracts/manual wait/step navigation separately: the P0 fixture is not a portable schema. P3 audio focus, sound assets, recovery and actual voice/tolerance decisions still need physical evidence. P4–P7 scope/order remain unchanged: no adjustments needed, because this slice adds no persistence, sync or backend assumptions. No measured tolerance or pre-synthesis decision can be accepted before Phone 2 tests. Issue #11 stays open.

## Hosted evidence and physical-test handoff

[PR #21](https://github.com/JuliaMathias/timey/pull/21) targets main and is draft pending physical evidence. Development links #11 to PR #21, verified in the actual sidebar and through GraphQL closingIssuesReferences. [CI run 37053031243](https://github.com/JuliaMathias/timey/actions/runs/37053031243) passes android-build, android-device-tests and ci-required at implementation revision 7c6d9fa. Independently downloaded reports verify 11 JVM cases and 5 instrumented cases, zero failures/skips. Seven result-guard cases pass in that build job. This documentation increment must receive its own fresh required CI.

[Issue #11](https://github.com/JuliaMathias/timey/issues/11#issue-5672297851) contains the full runnable installation/fixture/measurement/cleanup procedures and result template, verified against implemented labels. User elected to run Phone 2 tests later. English/pt-BR audible output, overlap/preemption, physical lifecycle behavior and measured tolerance remain Not run; do not close the issue or infer Phone 1 acceptance. The final APK is available locally at `android/app/build/outputs/apk/debug/app-debug.apk` or from the passing CI's android-build artifact. Build/install without clearing phone data.

Commits: 86acdaf (pure engine/tests), 7c6d9fa (diagnostic UI/TTS/tests/docs). Updated dependent issues #12 and #2 with service ownership and contract reuse boundaries before any merge. Unrelated Studio daemon-JVM file remains untracked. User reviews/merges manually; no merge/auto-merge is performed.
