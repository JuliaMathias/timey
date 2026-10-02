# Mac development setup

Status: installation verified 2026-10-02 after the user completed Android Studio setup. This agent did not install tools. See [sources](SOURCES.md) for official installation references. Use current stable compatible releases when P0 begins rather than copying an old version number from a document.

## What is already present

This Mac reports Apple silicon (`arm64`) and macOS 26.6.2. Git 2.50.1 and GitHub CLI 2.86.0 are available. On 2026-10-02, verified:

- Android Studio at `/Applications/Android Studio.app`; bundle version 2026.2, build `262.9437.185.2621.16467767`.
- Bundled Java executable runs successfully: OpenJDK 25.0.3. The scaffold pins Gradle 9.6.0 and CI Java 25.0.3; build validation is recorded in the Android foundation execution plan.
- SDK at `~/Library/Android/sdk`, platform `android-37.0`, Build-Tools 36.0.0, Platform-Tools 37.0.1, emulator and system image installed.
- AVD `Medium_Phone_API_37.0` is configured and now boots successfully after the storage fix and a cold boot with four virtual CPU cores. `adb devices` reports `emulator-5554 device`, `sys.boot_completed=1`, Android 17. Physical-phone connection remains unverified.
- SDK Command-line Tools are installed and execute with Studio's bundled Java. `sdkmanager --version` delegates to Android CLI and reports version 1.0.16486076; prefer current documented CLI commands when scripting SDK setup.

Startup diagnosis confirmed 16 GiB RAM and initially insufficient disk (4568.97 MB available versus 12288 MB needed). After the user freed space, 32–34 GiB was available and userdata creation succeeded. The subsequent five-minute boot timeout was resolved by a cold boot with four virtual CPU cores; this does not prove which change alone resolved it. Authenticated GitHub API access and Projects authorization as JuliaMathias were verified on 2026-10-02.

## Install Android tools first

1. Download current stable Android Studio for **Mac with Apple chip** from the official site. Put it in Applications and open it. Let its setup wizard install the Android SDK. Prefer the standard wizard setup initially.
2. In SDK Manager, install the project's selected Android SDK Platform, SDK Build-Tools, Android SDK Platform-Tools (includes `adb`), Android SDK Command-line Tools, and Android Emulator. P0 selects and pins a supported compile/target SDK and minimum Android version after checking your phone. Keep SDK Manager's actual path recorded; its usual Mac default is `~/Library/Android/sdk`.
3. In Device Manager, create one phone emulator with an ARM64 system image. A Google Play image is useful for later Google authorization testing. Start it and confirm it reaches Home. Use a physical phone for final timing, speech, battery, notification and overlay checks.
4. Open the project using the `android/` folder, not the repository root, and let Studio sync Gradle. Use the bundled JetBrains Runtime (Java 25.0.3) as the Gradle JDK; the scaffold and CI verify this compatible major runtime with the pinned build toolchain.

The official requirements currently list 16 GB RAM for Studio plus emulator and recommend 32 GB; reserve ample SSD space for SDKs/images (32 GB free is recommended). If resources are constrained, a physical device avoids running the emulator. Don't install a global Gradle or Kotlin toolchain: the project will use its checked-in Gradle wrapper and pinned plugins.

## Terminal setup after confirming paths

Android Studio handles its SDK/JDK settings independently. For terminal builds, these are example shell settings for the usual paths; check them before adding to `~/.zshrc`. The agent will not edit your shell profile as part of planning.

```sh
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$PATH"
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
```

Then verify `java -version` and `adb version` in a new terminal. The checked-in wrapper is now available. From `android/`, run `./gradlew testDebugUnitTest lintDebug assembleDebug`, then `./gradlew connectedDebugAndroidTest` with the emulator or a phone connected. See README and the foundation execution plan for results and limitations.

## Connect your Android phone

Enable Developer options (usually tapping Build number repeatedly), enable USB debugging, connect a data-capable USB cable, and accept the computer's debugging authorization on the phone. Mac does not require an extra OEM USB driver. Confirm the device is listed by `adb devices` and select it as Android Studio's Run target. Enable wireless debugging later if useful; USB is simpler for first setup.

Run the debug APK from Android Studio after P0 scaffolding. It may request notification permission; the movable background panel requires separate draw-over-other-apps access through an explicit system setting. Permission prompts must explain the relevant feature and offer a usable fallback. Record phone model/Android version when validating background-service and movable-panel behavior.

Install/download an offline voice for your desired language using the phone's text-to-speech engine settings, whose location varies by manufacturer. Then verify the app's voice in airplane mode. Not all voices work offline. Choose an available offline voice or sound-only fallback; do not assume the emulator's voice is representative of the phone.

## Google Drive setup in P4

Local editing/playback does not need this. When sync work starts, use your Google Cloud project, enable Drive API, configure OAuth consent and an Android OAuth client with the app package/signing certificate, and authorize the narrow `drive.appdata` scope. Keep the same Cloud application for the later web OAuth client so both access the same app-data space. Configure debug and personal release credentials intentionally; never embed a client secret in the APK.

App-data is hidden from normal Drive browsing; Timey's JSON export is the user-readable backup path. External OAuth applications left in Testing can have seven-day refresh-token expiry for these scopes; evaluate the appropriate consent/publishing arrangement for personal use rather than promising permanent unattended authorization. Keep OAuth credentials and signing keys outside Git. P4 provides exact console steps and tests actual account recovery. Standard tests use fake Drive adapters.

## GitHub and later Elixir tools

Git and `gh` are already installed. The approved public repository is JuliaMathias/timey. GitHub API access and additional Projects authorization were verified on 2026-10-02; the repository, milestones, issues and board are created. No project-management tool installation is necessary now.

Install compatible Erlang/OTP, Elixir, and Phoenix dependencies at P6, with versions pinned then. The proposed web database is SQLite, so PostgreSQL is not currently required. Follow the generated project's requirements at that time; Node.js is only needed if the chosen asset tooling needs it. Android development does not require Xcode, Docker, or Elixir. We will not install optional tools speculatively.

## First guided session

Check Studio/SDK/JDK, boot emulator, connect the phone, create the minimal scaffold, run one test and the APK, then inspect one Compose screen and one timer transition together. Each step should have an observable result. Diagnose failed setup before adding more libraries.

## Emulator startup failure: insufficient disk space

On 2026-10-02, the user saw “The emulator process … has terminated.” A bounded headless launch of the existing AVD, without loading/saving snapshots or wiping device data, exited with code 1 and reported:

```text
Not enough space to create userdata partition.
Available: 4568.97 MB … need 12288.00 MB.
```

This is the confirmed failure, rather than a guessed graphics or installation problem. Free enough space to satisfy the 12 GiB userdata allocation with additional headroom for builds (around 20 GiB free is a practical next target), then retry the same AVD. Choose which personal files to move/remove yourself; no cleanup was performed by the agent. Alternatively connect one of the confirmed physical phones through USB debugging to continue setup without allocating emulator userdata.

The diagnostic launch reported automatic RAM increase to 4096 MB for API 37 and successful system/GPU checks. Those checks do not establish boot success: userdata creation failed first. Record a successful boot/device connection only after retry evidence.

### Five-minute boot timeout after freeing space

The original one-core AVD remained offline and Studio timed out after five minutes. Verified Hypervisor.Framework acceleration, adequate available memory and 32–34 GiB disk headroom. Restarted the stopped AVD using `-no-snapshot-load -no-snapshot-save -cores 4 -show-kernel`, preserving userdata and snapshots. Android reached boot completion around 142 seconds; adb then reported `device`, `sys.boot_completed=1` and Android 17.

Persisted `hw.cpu.ncore=4` in the local AVD config for future Studio launches, keeping the prior config in a temporary local backup. The runtime automatically uses 4096 MB RAM for API 37. Cold boot and CPU count changed together; do not claim an isolated core-count diagnosis. No virtual-device wipe or SDK reinstall was performed. Emulator boot is now verified; application build/install and physical-phone checks remain task #9 work.

### Boot completion versus usable development device

During scaffold testing, API 37 sometimes lost Activity/package services. After restarting, individual properties/services responded but full `adb shell getprop` enumeration hung, preventing Gradle's device-property collection. Changing to the documented SwiftShader renderer did not fix full enumeration; no permanent graphics change is recorded. A boot-complete flag alone therefore does not establish a usable Gradle test device. Hosted API 36 instrumentation is passing; both physical-phone runs remain unverified. Direct adb installation and the actual AndroidJUnitRunner completed both screen tests successfully (172.575 seconds). This is a real local test pass, but not a successful Gradle connected-test command; the property-fetching limitation remains. No device properties or test outcomes were fabricated.

If this installed emulator still reports unknown API level/unresponsive properties in Studio, create an Android 16 / API 36 Google APIs ARM64 AVD using Device Manager (+ → Create virtual device → choose a phone → select/download an ARM64 Android 16 system image). Keep the existing AVD; no wipe is required. Record download/disk usage and actual boot/test evidence. This alternative image is not installed by this task. The phone matching API 36 is also a valid development target after USB authorization.

### Invalid JAVA_HOME from an existing shell profile

On this Mac, `~/.zshrc` contains an old `export JAVA_HOME=/opt/jdk-17.0.8+7`; that directory is not installed. A newly started shell can restore this value even after Java was exported in another session. Studio's bundled Java executable is verified at `/Applications/Android Studio.app/Contents/jbr/Contents/Home/bin/java`. From `android/`, pass the correct values directly to each Gradle invocation:

```sh
env JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
    ANDROID_HOME="$HOME/Library/Android/sdk" \
    ./gradlew testDebugUnitTest lintDebug assembleDebug
```

This exact command passes locally. `env` supplies variables for that command and its children; it does not rely on earlier exports surviving across terminal sessions. Use the same prefix for connected tests. For a permanent interactive-shell setting, replace the stale JAVA_HOME line in `~/.zshrc` with the quoted Studio path and open a new terminal; no profile edit is required to use the command above.
