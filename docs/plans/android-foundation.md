# Execution plan: Android foundation and CI

Status: scaffold and CI integrated; #9 device/setup evidence remains active. Owner: primary agent. Base revision: 1b0f426. Issues: #9 and #10.

## Purpose and scope

Create a launchable offline Compose foundation and executable build/tests. Configure the required GitHub merge gate before integrating it. R01/R25 foundations only; editors, timer, audio, Room, Drive and Phoenix remain later tasks. Neither issue is complete until its own acceptance evidence exists.

## Approach and validation

One app module. AGP 9.4.0, Gradle 9.6.0 with distribution checksum, Kotlin/Compose compiler 2.2.10 (AGP's built-in Kotlin), Compose BOM 2026.09.00, Activity 1.13.0. Compile/target API 37, minimum API 36 for the user's Android 16 phones. No older-device compatibility claim. Java 25 runs Gradle; compile to Java 17 bytecode. Versions verified from official release notes and Google Maven metadata on 2026-10-02.

The foundation has no Internet permission/account gate and accurately labels unimplemented features. Instrumentation tests cover actual Activity launch, network-permission absence and Activity recreation. No artificial JVM test is added to pretend a timer exists; the unit-test command is established, with domain tests following in #11/P1. Tests on the emulator do not satisfy physical-phone acceptance.

## Progress and evidence

- Mac tools installed; API 37 ARM64 emulator boot verified, currently connected.
- Branch `codex/android-foundation` created.
- Scaffold builds with local JBR 25.0.3; lint passes after adding explicit icon/extraction rules and excluding version-update advisories for deliberately reviewed pins. Wrapper JAR checksum matches the official Gradle checksum.
- Initial emulator instrumentation failed before install: Android activity/package services disappeared. AGP returned exit zero with a zero-test HTML summary. Added a fail-closed result guard and six passing regression cases; verified the guard rejects that actual zero-test report. Cold-boot retry in progress.
- CI, protection verification and manual phone evidence pending.

## Recovery and handoff

Use the checked-in wrapper from `android/`, with Studio's bundled Java and the installed SDK. Never clear the user's phone data to test. Update #9 with exact implemented-screen manual instructions before requesting phone tests. Review the entire remaining delivery plan and outstanding dependencies at completion; do not close #9 based only on the emulator.

## CI/protection progress

PR #13 created. First CI run 37037106035 failed because hosted runners had no sdkmanager on PATH; adding explicit pinned SDK setup rather than assuming a preinstalled path. Main protection was applied and read back: `ci-required` bound to GitHub Actions app 15368, strict up-to-date checks, PR required with zero required approvals for the sole owner, administrator enforcement, conversation resolution, force pushes/deletions disabled. Failed PR currently reports merge state blocked. Synthetic behavioral-failure/repaired revision verification still pending.

## Local instrumentation compatibility finding

The cold-booted API 37 emulator reached Activity/package readiness. Two tests executed but Espresso's inherited older version failed before assertions because InputManager.getInstance was removed on this OS. Explicitly pin Espresso 3.7.0, whose official release notes replace that reflection with system-service access. The changed app/test APKs compile and lint passes; subsequent instrumentation encountered an unresponsive emulator (unknown API level), so no API 37 pass is claimed. A bounded SwiftShader renderer retry is in progress after logs reported the default guest ANGLE path unstable above API 35. Hosted API 36 build/lint and result-guard cases pass; instrumentation remains pending.

## Verified hosted results and negative gate

Foundation revision a4d06df: [CI run 37038040750](https://github.com/JuliaMathias/timey/actions/runs/37038040750) passed android-build, android-device-tests and ci-required. Downloaded the actual report and independently verified two executed tests, zero failures/skips; six result-guard cases pass in CI. Local unit task has no JVM cases.

Synthetic revision e55ef7c: [run 37037775434](https://github.com/JuliaMathias/timey/actions/runs/37037775434) has passing build but two intentional wrong-heading assertions fail. Uploaded reports confirm that exact cause. Required ci-required fails; non-draft PR #14 reports mergeable_state blocked. Restored the correct heading at 8370c8e; its fresh CI is running. Never merge the synthetic branch.

## Forward-plan review

Reviewed the entire P0–P7 plan and open task dependencies. Refinement needed: every instrumentation suite must prove tests executed, not merely trust Gradle exit status. Added the regression-tested guard now, so future Room/editor/background suites inherit that requirement. API 37 local property-fetching instability remains #9 setup work; API 36 hosted testing preserves the target-phone OS gate. #11 still needs fake-clock JVM tests and both phones/offline voices; #12 must validate foreground/overlay rules for target SDK 37 on both API 36 Samsungs. P1 contracts/persistence, P2 editors, P3 audio/player, P4 sync/history, P5 phone acceptance, and P6/P7 API/web order and product requirements remain unchanged. No adjustments needed to those phases' scope or order: scaffold evidence does not establish timing/audio/sync behavior.

#9 remains open for physical-phone connections/runs and local emulator tooling evidence. #10 can close only after repaired synthetic CI/eligibility, test-branch cleanup, passing latest real PR CI and integration.

## Local direct-run evidence

At a4d06df, `adb -s emulator-5554 install -r` succeeded for app-debug.apk and app-debug-androidTest.apk. `adb -s emulator-5554 shell am instrument -w -r com.juliamathias.timey.test/androidx.test.runner.AndroidJUnitRunner` executed both FoundationScreenTest cases with status code 0 and ended `OK (2 tests)` in 172.575 seconds, INSTRUMENTATION_CODE -1. This verifies the actual API 37 app launch and recreation with Espresso 3.7. Gradle connected tests remain blocked by full getprop enumeration; hosted API 36 Gradle/instrumentation pass is separate evidence. No Samsung tests have run.

## Gate repair verified and Mac shell discovery

Synthetic repaired revision 8370c8e: [CI 37038639425](https://github.com/JuliaMathias/timey/actions/runs/37038639425) passes all three jobs; downloaded report verifies two tests, zero failures/skips. Non-draft PR #14 changed from blocked to clean/merge-eligible. Closed it without merging and deleted only its dedicated remote branch. No protection bypass was used.

User encountered JAVA_HOME=/opt/jdk-17.0.8+7. Read-only inspection found that stale export in the user's .zshrc; Studio Java exists. The command-scoped `env JAVA_HOME=... ANDROID_HOME=... ./gradlew testDebugUnitTest lintDebug assembleDebug` passes in four seconds. Update README/Mac guide/#9 to use this robust form; the user's profile remains editable through their own normal setup. Physical phone access is still pending. Final real-PR CI reruns after this documentation increment before integration.

## Integration and task handoff

[Final CI 37039572771](https://github.com/JuliaMathias/timey/actions/runs/37039572771) passed every mandatory job on 964e862. [PR #13](https://github.com/JuliaMathias/timey/pull/13) merged with commit be46b01 under protection, preserving five logical implementation/documentation commits (b6f5a0c through 964e862). #10 closed and board set Done; #9 stays In progress. The user's current blocker was the stale JAVA_HOME, for which the exact command-scoped workaround is verified and documented. Next: connect/install/check both phones per #9, resolve local Gradle emulator enumeration or use a separately created API 36 AVD, then #11's timing/offline-speech prototype. No change to agreed product scope or Android-before-web gate.

## Revised physical-device scope — 2026-10-02

User confirmed Phone 1 has a damaged USB port: test only Phone 2, while retaining Phone 1 as a supported Android 16 compatibility target. This supersedes earlier two-phone setup/prototype gates in this handoff. Updated P0/P3/P5 physical gates and P4/P5 cross-client validation to use Phone 2 plus an emulator or isolated client; no Phone 1 device pass is implied. Phone 1 access is not a delivery blocker. Product behavior and Android-before-web order are unchanged.
