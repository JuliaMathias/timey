# Execution plan: Android foundation and CI

Status: active. Owner: primary agent. Base revision: 1b0f426. Issues: #9 and #10.

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
