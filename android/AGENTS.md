# Android-specific instructions

Inherit the repository rules. This directory will contain the Android application; it is not scaffolded yet.

- Use Kotlin, Jetpack Compose, Material 3, coroutines/Flow, ViewModels, Room, and WorkManager. Pin compatible stable versions when scaffolding; use the committed Gradle wrapper and version catalog. No global Gradle/Kotlin installation is needed.
- Start with one app module and clear packages. Extract the platform-independent timer/model into a small Kotlin module when it improves isolated tests; avoid one Gradle module per screen.
- Follow unidirectional state flow: screens render state and send actions; ViewModels/repositories manage state. Composables do not own the active workout clock or call Drive directly.
- Use integer milliseconds for durations, including decimal rep seconds. Inject a monotonic clock. Wall-clock UTC timestamps are for sync/history, never active countdown timing.
- Keep background execution, notification controls, speech/sound, and optional overlay adapters outside the engine. Use WorkManager for deferred sync, never interval pacing.
- Add KDoc to every class/module and function, including private functions. Explain lifecycle/cancellation behavior and ownership of resources.
- Persist edits locally before networking. Test Room transactions and migrations with the actual database. Keep credentials and local SDK paths outside Git.
- Test engine transitions using fake time; test ViewModel state and critical Compose interactions. Instrumented/device tests are necessary for audio, screen-off playback, permission denial, and notifications.
- At scaffolding, add/verify these commands in README: `./gradlew testDebugUnitTest lintDebug assembleDebug`; with a device, `./gradlew connectedDebugAndroidTest`. Until the wrapper exists these are intended commands, not runnable checks.
- Preserve paused/running state for previous/next/restart. Cancel obsolete cues on navigation and stop. Always release wake/audio resources on stop and failures.

