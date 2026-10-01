# Mac development setup

Status: guide only, checked 2026-10-01. No tools installed or account settings changed by this task. See [sources](SOURCES.md) for official installation references. Use current stable compatible releases when P0 begins rather than copying an old version number from a document.

## What is already present

This Mac reports Apple silicon (`arm64`) and macOS 26.6.2. Git 2.50.1 and GitHub CLI 2.86.0 are available. Android Studio and Android SDK were not found in the usual `/Applications/Android Studio.app`, `~/Applications/Android Studio.app`, and `~/Library/Android/sdk` locations. That is not an exhaustive inventory. `/usr/bin/java` is a macOS launcher, not proof of an installed usable JDK. GitHub authentication and available disk/RAM were not checked.

## Install Android tools first

1. Download current stable Android Studio for **Mac with Apple chip** from the official site. Put it in Applications and open it. Let its setup wizard install the Android SDK. Prefer the standard wizard setup initially.
2. In SDK Manager, install the project's selected Android SDK Platform, SDK Build-Tools, Android SDK Platform-Tools (includes `adb`), Android SDK Command-line Tools, and Android Emulator. P0 selects and pins a supported compile/target SDK and minimum Android version after checking your phone. Keep SDK Manager's actual path recorded; its usual Mac default is `~/Library/Android/sdk`.
3. In Device Manager, create one phone emulator with an ARM64 system image. A Google Play image is useful for later Google authorization testing. Start it and confirm it reaches Home. Use a physical phone for final timing, speech, battery, notification and overlay checks.
4. Open the future project using the `android/` folder, not the repository root. Let Android Studio sync Gradle after the scaffold exists. Use the bundled JetBrains Runtime as the Gradle JDK if compatible with the selected Android Gradle Plugin. P0 verifies that compatibility and makes terminal/CI use the same supported major JDK version.

The official requirements currently list 16 GB RAM for Studio plus emulator and recommend 32 GB; reserve ample SSD space for SDKs/images (32 GB free is recommended). If resources are constrained, a physical device avoids running the emulator. Don't install a global Gradle or Kotlin toolchain: the project will use its checked-in Gradle wrapper and pinned plugins.

## Terminal setup after confirming paths

Android Studio handles its SDK/JDK settings independently. For terminal builds, these are example shell settings for the usual paths; check them before adding to `~/.zshrc`. The agent will not edit your shell profile as part of planning.

```sh
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$PATH"
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
```

Then verify `java -version` and `adb version` in a new terminal. A wrapper comes with the project in P0; don't run Gradle commands in this documentation-only repository yet. Later, from `android/`, start with `./gradlew testDebugUnitTest lintDebug assembleDebug`. Task names and exact JDK guidance will be verified and added to README during scaffolding.

## Connect your Android phone

Enable Developer options (usually tapping Build number repeatedly), enable USB debugging, connect a data-capable USB cable, and accept the computer's debugging authorization on the phone. Mac does not require an extra OEM USB driver. Confirm the device is listed by `adb devices` and select it as Android Studio's Run target. Enable wireless debugging later if useful; USB is simpler for first setup.

Run the debug APK from Android Studio after P0 scaffolding. It may request notification permission; an optional overlay requires a separate explicit system setting. Permission prompts must explain the relevant feature and offer a usable fallback. Record phone model/Android version before choosing background-service and banner behavior.

Install/download an offline voice for your desired language using the phone's text-to-speech engine settings, whose location varies by manufacturer. Then verify the app's voice in airplane mode. Not all voices work offline. Choose an available offline voice or sound-only fallback; do not assume the emulator's voice is representative of the phone.

## Google Drive setup in P4

Local editing/playback does not need this. When sync work starts, use your Google Cloud project, enable Drive API, configure OAuth consent and an Android OAuth client with the app package/signing certificate, and authorize the narrow `drive.appdata` scope. Keep the same Cloud application for the later web OAuth client so both access the same app-data space. Configure debug and personal release credentials intentionally; never embed a client secret in the APK.

App-data is hidden from normal Drive browsing; Timey's JSON export is the user-readable backup path. External OAuth applications left in Testing can have seven-day refresh-token expiry for these scopes; evaluate the appropriate consent/publishing arrangement for personal use rather than promising permanent unattended authorization. Keep OAuth credentials and signing keys outside Git. P4 provides exact console steps and tests actual account recovery. Standard tests use fake Drive adapters.

## GitHub and later Elixir tools

Git and `gh` are already installed. Once the plan and remote ownership are approved, authorize GitHub and create the private repository/issues if chosen. No project-management tool installation is necessary now.

Install compatible Erlang/OTP, Elixir, and Phoenix dependencies at P6, with versions pinned then. The proposed web database is SQLite, so PostgreSQL is not currently required. Follow the generated project's requirements at that time; Node.js is only needed if the chosen asset tooling needs it. Android development does not require Xcode, Docker, or Elixir. We will not install optional tools speculatively.

## First guided session

Check Studio/SDK/JDK, boot emulator, connect the phone, create the minimal scaffold, run one test and the APK, then inspect one Compose screen and one timer transition together. Each step should have an observable result. Diagnose failed setup before adding more libraries.
