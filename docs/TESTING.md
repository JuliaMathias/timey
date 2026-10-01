# Testing and acceptance strategy

Every feature ships with meaningful tests. Use the smallest level that proves behavior, plus device evidence where Android or audio is involved. A passing compile is not a feature test. Current documentation-only work has no application test suite; validate links, requirement coverage and Git diffs instead.

## Automated levels

Pure Kotlin tests cover models/validation, routine expansion/cursor, phases, display values, confirmation, navigation, cue planning and conflict/history logic. Inject fake monotonic/UTC clocks; test transitions without sleeping. Prefer fakes over elaborate mock expectations.

Repository/ViewModel tests cover dirty draft state, transactional save, undo/redo, search, import validation, settings inheritance, sync status and mutations during sync. Android Room integration/migration tests exercise real database behavior. Compose instrumentation tests cover critical user flows and accessibility semantics, rather than brittle pixel-exact reproduction of reference screens.

Later ExUnit tests cover contexts, Ecto migrations, API authentication/validation/revision conflicts and LiveView editor flows. Shared fixture/schema tests on both platforms prove conversion, durations, ordering, omission and export compatibility. OpenAPI validation/examples must match API implementation.

## Required behavioral cases

| Area | Cases to prove |
| --- | --- |
| Timer | A/B ×3, cross-set navigation, delayed tick reconciliation, fractional rep pace, Down/Up ×10, pause/resume, restart-step/routine, zero optional durations, empty/all-skipped rejection, skip any final-set step. |
| Display/manual | Time and reps in countdown/count-up/static; correct boundaries; early checkmark; manual clamping/wait; voice numbering separate from completed-count display; no next step begins during wait. |
| Cues | Step-start name, last-N seconds/reps, global/step overrides, all/selected phases, cancel on pause/skip/restart/stop, missing offline voice, long phrase in short phase, no stale cue backlog. |
| Editing | Three disclosure states/bulk buttons, drag entire loop, independent copy IDs/order/colors, Simple conversion equivalence, next palette with explicit prepare/rest role, search containing routine, undo/redo and redo invalidation. |
| Persistence | Changed-only 60-second autosave, exit flush, explicit save versus autosave history, draft invalidity, concurrent save calls, DB reopen/migration/rollback, import rejected before mutations. |
| Sync | Offline autosave coalescing; explicit backups preserved; retries/dedup/pagination; edits arriving during upload; disconnected/revoked auth; conflicting concurrent heads; delete/edit conflict; new account isolation; fresh install restore. |
| History | Five-day boundary with fake UTC clock, explicit-save-only snapshots, restore as new revision, corrupted snapshot refusal, expired pending history, current data survives cleanup. |
| API later | Auth scopes/errors, CRUD of all content, order/copy/conversion/search, import/export/history/restore, stale update preconditions, request retry idempotency, docs/examples/contract parity. |

## Real-device matrix

Use the user's actual phone for at least one full real routine, and an emulator for repeatable UI flows. Record model, Android version, target SDK, APK commit, voice engine/language, permissions, scenario, expected/actual behavior and evidence. Do not commit personal screenshots or tokens.

Check airplane mode; screen on/off; app background/another app; running/paused notification and banner dismissal; stop/return Home; permission denial; music/headphones/Bluetooth; rotation/large text/TalkBack; battery saver/idle; process kill and separately force-stop/reboot recovery. Verify wake resources release after stop/finish and waiting, and measure actual timing/audio drift against the P0-agreed tolerance. Hardware-specific results are not universal Android guarantees.

## Expected commands after scaffolding

From `android/`: `./gradlew testDebugUnitTest lintDebug assembleDebug`. With emulator/phone: `./gradlew connectedDebugAndroidTest`. Confirm task names in P0 and update docs if modules/flavors change. JVM domain-module task is added once that module exists.

From `web/` later: `mix format --check-formatted` and `mix test`, plus the selected OpenAPI/schema lint command. No commands above currently run: there is no Gradle wrapper or Mix project yet.

Continuous integration should use a compatible pinned JDK/SDK, unit tests/lint/build on Android changes, shared fixture checks on contract changes, and later Mix checks on web changes. Device acceptance cannot be replaced by hosted CI alone. Avoid real Google accounts in standard CI; credentials are only used in deliberate integration verification.

## Reporting

Each issue/PR records commands run, results, relevant device evidence and unrun checks. A reviewer verifies requirement IDs and failure cases. Mark complete only when behavior is demonstrated; blocked hardware/account work stays visible. No coverage percentage substitutes for these acceptance cases.
