# Execution plan: #11 phased reps and offline speech

Status: active. Owner: primary agent. Base revision: 73e2101. Branch: `codex/issue-11-phased-reps-speech`.

## Scope and approach

R06–R09/R11/R23 P0 diagnostic slice only. Fixture A: 10 × 2200ms. Fixture B: 10 × (Down 2000ms + selected Up 4000ms). Short fixture: ten reps with two 200ms long-label phases. English default; pt-BR option. Immutable schedule and injected monotonic clock; grouped cues, cancellation and stale-callback fencing. No portable schema/editor/storage or background reliability claim: #12 owns service/notification/overlay integration.

## Progress and validation

Instructions, requirements, plan, architecture, workflow, Android guidance and testing strategy read. GitHub #9 closed with Phone 2 acceptance; #10 complete. Current main verified against origin. Untracked Studio `android/gradle/gradle-daemon-jvm.properties` is unrelated and excluded. No connected devices at initial inspection.

First increment adds the pure engine and fake-clock tests. Local `testDebugUnitTest lintDebug assembleDebug` passed: 10 JVM behavioral cases, no failures. CI awaits PR. Physical timing/offline speech and measured tolerance remain not run until Phone 2 evidence exists. Phone 1 is compatibility-only, hardware behavior unverified.

## Recovery and next steps

Use README's command-scoped Studio Java/SDK build. Never clear phone data. Add the foreground-only diagnostic UI and offline TTS adapter, then update issue #11 with actual screen labels, exact fixture procedures and result recording. Open a linked PR and verify Development association and latest CI. Leave PR/issue open for missing physical evidence and user merge.

## Android adapter increment

Foreground diagnostic route, StateFlow/ViewModel ownership across rotation, pause on leaving foreground, offline-only English/pt-BR selection, queue-flush speech and callback fencing are implemented. Two-rep preview and long-label 200ms phases expose overlap warnings without pace changes. Bounded diagnostics show cue due/dispatch offsets, onset callback delay, completed speech duration and actual finish-dispatch lateness (separate from clamped elapsed display). Missing voice offers setup guidance and silent timing; no voice download or sound library is added.

Local `testDebugUnitTest lintDebug assembleDebug assembleDebugAndroidTest` passes: 11 JVM cases, no failures/errors/skips. Seven result-guard cases pass. Three new Compose cases compile; actual execution awaits hosted CI (five total instrumentation cases). Lint led to the official stable Lifecycle 2.11.0 pin and removal of an empty super call. No Phone 2 connected; physical cases and measured tolerance remain blocked/not run.

## Forward-plan review

Reread P0–P7 and outstanding dependencies. #12 must move session/polling ownership from this foreground ViewModel into a foreground service, retain the pure engine/cue cancellation seam, and verify resource release/notification/overlay on Phone 2. P1 can reuse elapsed-time and callback tests but must implement full routine contracts/manual wait/step navigation separately: the P0 fixture is not a portable schema. P3 audio focus, sound assets, recovery and actual voice/tolerance decisions still need physical evidence. P4–P7 scope/order remain unchanged: no adjustments needed, because this slice adds no persistence, sync or backend assumptions. No measured tolerance or pre-synthesis decision can be accepted before Phone 2 tests. Issue #11 stays open.
