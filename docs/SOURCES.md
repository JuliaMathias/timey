# Research sources

Checked 2026-10-01 using official documentation. These sources support platform facts and workflow mechanics. Timey's phases, repository choice, revision protocol, defaults and tests are project proposals or user decisions, not requirements imposed by these sites. Recheck changing SDK/service/OAuth rules and library compatibility when implementing; do not freeze versions from a planning-time search.

## Agent instructions and management

- [OpenAI: AGENTS.md](https://learn.chatgpt.com/docs/agent-configuration/agents-md) — directory-scoped instructions and discovery. Root instructions explicitly direct agents to read Android/web guidance even when working from root.
- [OpenAI: execution plans](https://developers.openai.com/cookbook/articles/codex_exec_plans) — living, resumable plans with progress, decisions and validation. Timey's execution template adapts these ideas; Markdown plan files are loaded because AGENTS points to them, not because every filename is special.
- [GitHub: planning and tracking work](https://docs.github.com/en/issues/tracking-your-work-with-issues/learning-about-issues/planning-and-tracking-work-for-your-team-or-project) — issues, milestones, templates and optional Project views. No separate agent-management product is required by these mechanisms.
- [GitHub: protected branches](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches) — required status checks, up-to-date branches, administrator enforcement, accepted skipped statuses and account-plan/visibility availability. A workflow alone does not prohibit merging; configure and verify the protection too.
- [Michael Nygard: documenting architecture decisions](https://www.cognitect.com/blog/2011/11/15/documenting-architecture-decisions) — short records of context, decision, status and consequences for significant choices; preserve superseded records. Timey's decision index explains its project-specific scope.

## Android architecture and tooling

- [Android: architecture recommendations](https://developer.android.com/topic/architecture/recommendations) — Compose, ViewModels, unidirectional state and repositories.
- [Android: offline-first data layer](https://developer.android.com/topic/architecture/data-layer/offline-first) — local data source of truth and deferred synchronization work.
- [Android: install Studio](https://developer.android.com/studio/install) — Mac hardware/OS requirements and installation.
- [Android: JDK configuration](https://developer.android.com/build/jdks) — bundled runtime and compatible Gradle/JDK selection.
- [Android: run on a device](https://developer.android.com/studio/run/device) — debugging authorization, physical-device setup and Mac USB-driver guidance.
- [Kotlin: language tour](https://kotlinlang.org/docs/kotlin-tour-welcome.html) — beginner/intermediate browser exercises without local installation.

## Timing, background and speech

- [Android: foreground-service types](https://developer.android.com/develop/background-work/services/fgs/service-types) — service-type declarations and restrictions; evaluate legitimate timer service use in P0, rather than assuming audio cues justify any media exemption.
- [Android: keep a device awake](https://developer.android.com/develop/background-work/background-tasks/awake) — wakefulness/resource choices; a service alone is not a timing guarantee.
- [Android: overlay permission](https://developer.android.com/reference/android/Manifest.permission#SYSTEM_ALERT_WINDOW) — separately granted permission for optional drawing above apps.
- [Android: text-to-speech](https://developer.android.com/reference/android/speech/tts/TextToSpeech) — asynchronous speech and synthesis APIs; callbacks/queue cancellation need lifecycle ownership.
- [Android: voice network requirement](https://developer.android.com/reference/android/speech/tts/Voice) — select installed voices that don't require a network connection; verify with airplane mode.

## Drive, authorization and API contracts

- [Google Drive: application data](https://developers.google.com/workspace/drive/api/guides/appdata) — hidden app-data storage and narrow scope. This supports storage choice, not a ready-made multi-device conflict algorithm.
- [Android: Google authorization](https://developer.android.com/identity/authorization) — authorization for Google data is separate from sign-in identity.
- [Google: OAuth 2.0](https://developers.google.com/identity/protocols/oauth2) — OAuth lifecycle and external Testing refresh-token limits.
- [OpenAPI specification 3.2.1](https://spec.openapis.org/oas/v3.2.1.html) — current published specification checked during planning. Select a version supported by the Phoenix documentation/validation tooling in P6; 3.1 may be the compatible choice then.

## GitHub migration follow-up, checked 2026-10-02

- [GitHub CLI: Projects](https://cli.github.com/manual/gh_project) — Project commands require the `project` authorization scope; repository access alone does not grant it. Refresh authorization with `gh auth refresh -h github.com -s project`.
- [GitHub: REST Project views](https://docs.github.com/en/rest/projects/views) — create a board with explicit Status columns using `vertical_group_by`; the created Timey view was verified through API responses.

## Android foundation compatibility checked 2026-10-02

- [AGP 9.4 compatibility](https://developer.android.com/build/releases/agp-9-4-0-release-notes): supports API 37; requires Gradle 9.6.0, Build-Tools 36.0.0 and JDK 17 minimum.
- [Built-in Kotlin](https://developer.android.com/build/migrate-to-built-in-kotlin): AGP 9 replaces the separate kotlin-android plugin. Official Google Maven AGP 9.4.0 POM pins Kotlin 2.2.10; match the Compose compiler plugin.
- [Gradle Java matrix](https://docs.gradle.org/current/userguide/compatibility.html): Java 25 is supported starting with Gradle 9.1.0; local JBR and CI use Java 25.0.3, with app bytecode 17.
- [Activity releases](https://developer.android.com/jetpack/androidx/releases/activity) and [Compose BOM](https://developer.android.com/develop/ui/compose/bom): stable Activity 1.13.0 and Google Maven stable BOM 2026.09.00. BOM pins UI/test library versions together, independently of the compiler plugin.
- [Emulator runner's maintainer documentation](https://github.com/ReactiveCircus/android-emulator-runner): Ubuntu KVM setup and API/architecture/working-directory inputs for instrumentation CI.
- [Gradle Actions](https://github.com/gradle/actions) and [setup-java](https://github.com/actions/setup-java): wrapper validation/build caching and selected CI runtime. Action references are pinned by immutable Git commits.
- [Android Auto Backup rules](https://developer.android.com/identity/data/autobackup): explicit modern data-extraction exclusions for cloud backup and device transfer; allowBackup alone is insufficient on some manufacturers.
