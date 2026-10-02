# Timey

A personal Android interval timer for named exercise loops, paced repetitions and phases, pleasant cues, and offline routines. Google Drive sync provides recovery of the current library and settings across devices. Saved-version history is per routine; full-library JSON export provides an additional backup. A Phoenix LiveView computer editor and documented API follow after the phone app is accepted.

## Current state

Delivery plan approved on 2026-10-01; implementation has not started. The [public GitHub repository](https://github.com/JuliaMathias/timey) is created. There is no runnable app, Gradle wrapper, Phoenix project or CI workflow yet. The monorepo layout is approved; GitHub Issues, phase milestones and a Project board are selected; the delivery plan is approved. Eight milestones and twelve issues are created; Project board authorization and Android tool setup remain pending. Do not interpret planned features as shipped features.

## Start here

1. Review [the delivery plan](docs/PLAN.md) and [confirmed requirements](docs/PRODUCT.md).
2. Read [the architecture](docs/ARCHITECTURE.md) for timing, offline speech, storage, sync, and later API decisions.
3. Follow [Mac setup](docs/SETUP_MAC.md) when ready to install the Android tools. Installation has not been performed by this planning task.
4. Use [the workflow](docs/WORKFLOW.md), [testing strategy](docs/TESTING.md), and [learning guide](docs/LEARNING.md) during implementation.

## Repository map

| Path | Purpose |
| --- | --- |
| `AGENTS.md` | Mandatory agent rules: small commits with title/body, docs, tests, offline boundaries, approval scope. |
| `android/` | Phone application first; scoped Kotlin/Android guidance currently in `AGENTS.md`. |
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
| `.github/` | Task/bug issue templates and PR review template; no remote objects created. |
| `.gitignore` | Excludes local build state, credentials, databases, and generated caches. |

Versioned portable schemas and compatibility fixtures will be added under `contracts/` in P1. Builds and release versions stay independent even though contracts and docs share one repository. Personal reference media stays outside Git.

## Planned stack and delivery

Kotlin + Jetpack Compose for Android UI, a deterministic Kotlin timer engine, Room for local records, Android offline text-to-speech and local sound assets, a foreground playback service with a movable panel over other apps, and WorkManager for deferred Drive sync. Exact compatible versions and permissions are validated and pinned in P0. Google is optional for local use.

P0 proves device risks; P1 builds contracts/storage/engine; P2 builds offline editors; P3 completes playback/audio/background controls; P4 adds Drive sync/history/new-device recovery; P5 accepts and signs the complete phone app. P6 adds the documented Phoenix API; P7 adds the computer editor. See the plan for acceptance gates.

## Run, test, and contribute

No run or build command is available until P0 scaffolding. The setup and testing guides clearly label future commands. When scaffolding exists, update this section with verified clone/setup/run/test instructions and actual feature status in the same commit.

Read root and relevant scoped `AGENTS.md` before changes. Commit coherent increments with a descriptive title and complete body. Every feature needs meaningful tests and readable module/function documentation; Elixir functions require both `@doc` and `@spec`. Never claim a device behavior or test result without evidence.

Before any PR merges, its automated tests must run and pass in GitHub Actions. P0 adds the actual workflow and configures required checks on `main`; P6 extends it with Phoenix/API tests. This is an agreed engineering requirement, not an already configured protection. See [the merge gate](docs/TESTING.md#required-github-actions-merge-gate).
