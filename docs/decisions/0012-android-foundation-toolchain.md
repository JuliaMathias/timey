# 0012: Android foundation toolchain and device scope

Date: 2026-10-02
Status: Accepted engineering choice within approved P0; validation in progress
Requirements: R01, R25 and the P0 build/CI gate

## Context and decision

Both confirmed personal-use phones run Android 16 (API 36). The installed Mac SDK/emulator is API 37. Use minimum API 36, compile/target API 37, one app module, AGP 9.4.0, checksum-verified Gradle 9.6.0, AGP's built-in Kotlin 2.2.10 with the matching Compose compiler plugin, and Compose BOM 2026.09.00. Local Java 25.0.3 and CI Temurin 25.0.3 run Gradle; generated app bytecode targets Java 17. Exact library versions live in the version catalog.

This is an engineering selection permitted by the product's minimum-version open question, not a user request to support every Android device. Official compatibility sources are in SOURCES.md; actual build/device evidence is in the foundation execution plan.

## Alternatives and rationale

An older minimum such as API 26 would enlarge the lifecycle/permissions/test matrix without a requested target phone requiring it. Minimum 37 would exclude both phones. Using API 36 for compilation would require another SDK download without improving this scaffold's compatibility. CI instrumentation uses API 36 to check the minimum supported OS; local instrumentation uses the installed API 37 emulator.

A global Gradle/Kotlin installation would be harder to reproduce. The wrapper and version catalog keep build inputs in Git. AGP 9 includes Kotlin support, so do not add the obsolete separate Android Kotlin plugin. Room, WorkManager and audio dependencies are added when their implementing tasks need them.

## Consequences and validation

The APK supports Android 16 and later; older-phone support requires an explicit future compatibility decision and test matrix. API 37 foreground-service/overlay rules must still be researched and tested in #12; choosing a target SDK does not demonstrate background reliability. No product runtime behavior changes in this scaffold.

Run lint/build plus actual Compose launch/recreation tests. A successful NO-SOURCE JVM task is not a behavioral test. Add fake-clock JVM tests in #11/P1 and require both build and device-test jobs through the stable CI gate.
