# Execution plan: #11 phased reps and offline speech

Status: active. Owner: primary agent. Base revision: 73e2101. Branch: `codex/issue-11-phased-reps-speech`.

## Scope and approach

R06–R09/R11/R23 P0 diagnostic slice only. Fixture A: 10 × 2200ms. Fixture B: 10 × (Down 2000ms + selected Up 4000ms). Short fixture: ten reps with two 200ms long-label phases. English default; pt-BR option. Immutable schedule and injected monotonic clock; grouped cues, cancellation and stale-callback fencing. No portable schema/editor/storage or background reliability claim: #12 owns service/notification/overlay integration.

## Progress and validation

Instructions, requirements, plan, architecture, workflow, Android guidance and testing strategy read. GitHub #9 closed with Phone 2 acceptance; #10 complete. Current main verified against origin. Untracked Studio `android/gradle/gradle-daemon-jvm.properties` is unrelated and excluded. No connected devices at initial inspection.

First increment adds the pure engine and fake-clock tests. Local `testDebugUnitTest lintDebug assembleDebug` passed: 10 JVM behavioral cases, no failures. CI awaits PR. Physical timing/offline speech and measured tolerance remain not run until Phone 2 evidence exists. Phone 1 is compatibility-only, hardware behavior unverified.

## Recovery and next steps

Use README's command-scoped Studio Java/SDK build. Never clear phone data. Add the foreground-only diagnostic UI and offline TTS adapter, then update issue #11 with actual screen labels, exact fixture procedures and result recording. Open a linked PR and verify Development association and latest CI. Leave PR/issue open for missing physical evidence and user merge.
