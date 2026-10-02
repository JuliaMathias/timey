# Timey

A personal Android interval timer for named exercise loops, paced repetitions and phases, pleasant cues, and offline routines. Google Drive sync provides recovery of the current library and settings across devices. Saved-version history is per routine; full-library JSON export provides an additional backup. A Phoenix LiveView computer editor and documented API follow after the phone app is accepted.

## Current state

Delivery plan approved on 2026-10-01. The [public GitHub repository](https://github.com/JuliaMathias/timey), [eight milestones](https://github.com/JuliaMathias/timey/milestones), [twelve issues](https://github.com/JuliaMathias/timey/issues) and [Timey board](https://github.com/users/JuliaMathias/projects/7/views/3) are created. The board uses Backlog → Ready → In progress → Review → Done and currently retains GitHub's private visibility; sign in as its owner to view it.

P0 implementation is in progress. The Android scaffold contains a launchable offline foundation screen and Compose tests for launch/recreation. Routine editing, playback, speech, storage and sync are not implemented yet. Mac installation and emulator boot are verified; scaffold validation and real-phone checks are tracked in [#9](https://github.com/JuliaMathias/timey/issues/9). CI and merge enforcement are tracked in [#10](https://github.com/JuliaMathias/timey/issues/10). See the [execution handoff](docs/plans/android-foundation.md) for actual results.

## Start here

1. Review [the delivery plan](docs/PLAN.md) and [confirmed requirements](docs/PRODUCT.md).
2. Read [the architecture](docs/ARCHITECTURE.md) for timing, offline speech, storage, sync, and later API decisions.
3. Follow [Mac setup](docs/SETUP_MAC.md) for verified tools, terminal setup and phone connection instructions.
4. Use [the workflow](docs/WORKFLOW.md), [testing strategy](docs/TESTING.md), and [learning guide](docs/LEARNING.md) during implementation.

## Repository map

| Path | Purpose |
| --- | --- |
| `AGENTS.md` | Mandatory agent rules: small commits with title/body, docs, tests, offline boundaries, approval scope. |
| `android/` | Native phone application and build/test configuration; scoped instructions in `AGENTS.md`. |
| `web/` | Later Phoenix API and LiveView editor; scoped Elixir guidance currently in `AGENTS.md`. |
| `docs/PRODUCT.md` | Product behavior with stable requirement IDs R01-R25 and open questions. |
| `docs/PLAN.md` | P0-P7 phases, dependencies, demonstrations, and approval checkpoint. |
| `docs/ARCHITECTURE.md` | Technical boundaries and proposed models/protocols. |
| `docs/WORKFLOW.md` | GitHub recommendation, task ownership, review, commits, and agent handoffs. |
| `docs/TESTING.md` | Meaningful automated cases and real-phone acceptance evidence. |
| `docs/SETUP_MAC.md` | Android Studio/SDK, emulator/phone, JDK, and later OAuth setup. |
| `docs/LEARNING.md` | Learning alongside the project, with Kotlin/Elixir comparisons and debugging steps. |
| `docs/SOURCES.md` | Official research sources and what each supports. |
| `docs/decisions/` | [Decision index and policy](docs/decisions/README.md), with records of architecture-shaping choices and their rationale. |
| `docs/plans/TEMPLATE.md` | Resumable execution plan for a multi-session task. |
| `.github/` | Task/bug/PR templates and the Android CI workflow. |
| `.gitignore` | Excludes local build state, credentials, databases, and generated caches. |

Versioned portable schemas and compatibility fixtures will be added under `contracts/` in P1. Builds and release versions stay independent even though contracts and docs share one repository. Personal reference media stays outside Git.

## Planned stack and delivery

Kotlin + Jetpack Compose for Android UI, a deterministic Kotlin timer engine, Room for local records, Android offline text-to-speech and local sound assets, a foreground playback service with a movable panel over other apps, and WorkManager for deferred Drive sync. Exact compatible versions and permissions are validated and pinned in P0. Google is optional for local use.

P0 proves device risks; P1 builds contracts/storage/engine; P2 builds offline editors; P3 completes playback/audio/background controls; P4 adds Drive sync/history/new-device recovery; P5 accepts and signs the complete phone app. P6 adds the documented Phoenix API; P7 adds the computer editor. See the plan for acceptance gates.

## Run, test, and contribute

Open `android/` in Android Studio, let Gradle sync, choose the running emulator or a USB-debugging-authorized phone, and click Run. The foundation supports Android 16+ (API 36); compile/target SDK is 37. This targets your two phones; earlier Android versions are outside current validation.

For terminal builds on this Mac:

```sh
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export ANDROID_HOME="$HOME/Library/Android/sdk"
cd android
./gradlew testDebugUnitTest lintDebug assembleDebug
./gradlew clean connectedDebugAndroidTest
python3 ../scripts/verify_android_tests.py app/build/reports/androidTests/connected/debug/index.html
```

The wrapper downloads Gradle 9.6.0 and verifies its checksum. Java 25 runs the build; app bytecode targets Java 17. Versions live in `android/gradle/libs.versions.toml`. No global Gradle installation is needed. The current meaningful tests are two Compose instrumentation tests; `testDebugUnitTest` has no JVM cases until the timing prototype is introduced. Run `python3 -m unittest discover -s scripts -p 'test_*.py'` from the repository root for the six CI-result-guard tests. Build-time downloads need Internet; the installed foundation app does not.

Debug APK: `android/app/build/outputs/apk/debug/app-debug.apk`. Install/run with Studio or `adb -s DEVICE_SERIAL install -r app/build/outputs/apk/debug/app-debug.apk` from `android/`, replacing DEVICE_SERIAL with the intended test device. No clearing app data is needed.

[Android CI](.github/workflows/android.yml) runs build/lint/unit-test tasks and Compose instrumentation on an API 36 emulator for every PR to `main`, plus pushes to `main`. Its `ci-required` job accepts only successful results from both mandatory jobs. The device job also checks that at least two tests actually ran with zero failures/skips: AGP 9.4 was observed returning success after an emulator install failure with zero executed tests. Clean generated reports before local reruns to prevent stale evidence. Actions are pinned to commit hashes, permissions are read-only, and no Google credentials are used. Remote enforcement verification remains in progress under #10; do not claim main is protected until verified.

Read root and relevant scoped `AGENTS.md` before changes. Commit coherent increments with a descriptive title and complete body. Every feature needs meaningful tests and readable module/function documentation; Elixir functions require both `@doc` and `@spec`. Never claim a device behavior or test result without evidence.

Before any PR merges, its automated tests must run and pass in GitHub Actions. P0 adds the actual workflow and configures required checks on `main`; P6 extends it with Phoenix/API tests. This is an agreed engineering requirement, not an already configured protection. See [the merge gate](docs/TESTING.md#required-github-actions-merge-gate).
