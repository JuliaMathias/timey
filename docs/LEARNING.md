# Learning through Timey

The goal is a codebase you can understand and debug, not just a generated APK. Read only the concepts needed for the current milestone and immediately use them in a small real change. Use the browser-based [official Kotlin tour](https://kotlinlang.org/docs/kotlin-tour-welcome.html) for language practice; Android concepts use the official sources in [SOURCES.md](SOURCES.md).

## Connect familiar concepts

| Kotlin/Android concept | Useful connection to Elixir | Important difference |
| --- | --- | --- |
| Data class and sealed interface | Structs and tagged variants | Kotlin uses compile-time types; exhaustive `when` makes state variants visible. |
| `val`, immutable snapshots | Immutable values | A `val` prevents rebinding, but its referenced object can still be mutable. Prefer immutable domain models. |
| Nullability (`T?`) | Explicit absence/error handling | Compiler checks nullable access; avoid using `!!` as a routine escape. |
| Coroutines and cancellation | Concurrent tasks and message-based work | Coroutines are not BEAM processes; scope/lifecycle cancellation is central. |
| StateFlow, ViewModel, Compose | State changes followed by LiveView rendering | Compose recomposition does not own the background timer; Android can recreate a screen independently. |
| Room repository/transaction | Ecto contexts and Repo transactions | Local SQLite is the phone's source of truth; Drive reconciliation is separate work. |
| Pure engine with fake clock | Pure transition functions and ExUnit examples | Monotonic elapsed time and Android lifecycle need explicit adapters and device checks. |
| Foreground service and WorkManager | Long-running owner versus deferred jobs | Android restricts service starts and CPU wakefulness; neither is an unrestricted OTP supervisor. |
| Gradle wrapper | Project build/test entry point like Mix | Gradle/Android plugin/JDK versions form a compatibility set pinned in the repository. |

## Learning checkpoints

- P0: run the app and tests, inspect project structure, set a breakpoint, observe lifecycle changes, and distinguish the UI from the timer owner.
- P1: trace a routine through model validation, pure transitions, Room transaction, and reopen. Change a rep-phase example and predict the test result.
- P2: follow a UI event through ViewModel/state/repository back into Compose. Explain why autosave doesn't erase undo and why an invalid draft cannot play.
- P3: trace one cue from schedule to device audio; cancel it by skipping. Explain why speech completion must not determine workout timing.
- P4: walk through an offline edit, queued sync, competing revision, conflict choice and fresh-device restore using safe test data.
- P5: install/update a signed APK and explain why the signing key is needed for future updates. Read a battery/device result rather than relying on a generated assurance.
- P6-P7: compare Kotlin and Elixir fixtures, API validation and LiveView events. Create one routine with a documented API example and run it on the phone.

## A repeatable debugging loop

1. State the expected behavior with a tiny routine: two steps, two sets, or one phased repetition.
2. Reproduce it; record phone/emulator, APK revision, configuration and the exact action. Use fake clocks for domain errors, real device for lifecycle/audio errors.
3. Follow the path: UI event → ViewModel → repository or engine → new state → UI/cue. Use a breakpoint and redacted Logcat events at boundaries. Never log OAuth tokens or full private datasets.
4. Write a test that fails for the observed bug at the appropriate level, then make the smallest clear fix.
5. Run the relevant checks, explain the cause in plain language, update docs if behavior changed, and commit with title/body and real validation.

Every meaningful delivery includes one short learning note explaining a relevant concept or debugging choice. Module/KDoc/Elixir docs explain contracts and boundaries; this guide holds broader teaching material so source files remain readable. Ask for a walkthrough whenever a change is difficult to trace; do not accept unexplained abstractions as necessary.
