# Delivery plan

Status: approved by the user on 2026-10-01; P0 Android foundation in progress. Repository layout and GitHub Issues/milestones/Project board selected; initial scaffold in development; public JuliaMathias/timey repository created on 2026-10-02 and eight milestone trackers and four initial P0 tasks created. Updated 2026-10-02.

## Outcome and sequencing

Deliver a reliable, readable Android app first, including offline editing/playback, phases/voice, Google Drive sync and per-routine saved-version history across each routine's five most recent backup dates (manual/edited-session exit saves; cleanup after successful new-change saves). Only after phone acceptance, build the documented API and Phoenix web editor. Preserve stable IDs R01-R25 from `PRODUCT.md` when creating issues. The milestones below are task groups; each is split into small tested increments during issue migration.

No calendar estimates yet: target-device background/cue tests and tool setup determine the difficult parts. Progress is measured by demonstrations and passing acceptance, not generated code volume.

## P0: Environment, foundation and risk prototypes

Dependencies: plan approval (complete); Android Studio/SDK installation; access to Phone 2 (Galaxy S22 Ultra) for physical tests; Phone 1 (Galaxy S24 Ultra) remains a compatibility target with a damaged USB port (both Android 16; exact models/One UI in `PRODUCT.md`). Speech languages are English (default) and Brazilian Portuguese (pt-BR).

Task groups: scaffold compatible stable Kotlin/Compose Gradle project; pin toolchain/version catalog and wrapper; add runnable README and GitHub Actions tests/lint/build with a stable required merge gate. Configure `main` protection so tests must run and pass before any PR merges; verify account support and a failing/passing test PR. Build a tiny 2.2-second rep and Down/Up phase demonstration with fake-clock unit tests, real offline TTS, screen-off foreground playback and actionable notification. Measure cue delay/cancellation and prototype the required persistent draggable overlay alongside notification controls on Phone 2. Decide service type/wake-lock lifecycle; record device results and missing permission fallback. Install offline voice data if needed and verify airplane mode.

Gate: a debug APK runs on phone; phase progression stays correct while screen off and switching apps; obsolete voice cancels on skip; offline voice or explicit sound fallback works. Document measured limitations and chosen approach before building full UI. This is a disposable or reusable small vertical slice, not an excuse to build all features at once.

CI gate: the automated tests pass in GitHub Actions, and a PR with failing tests demonstrably cannot merge. Protection/check configuration is documented; add instrumentation and shared-contract checks as those suites appear. Do not mark this gate complete based only on a local test run or workflow file.

Device scope: run the timing, offline English/Brazilian Portuguese speech, screen-off, movable overlay and notification fallback prototype checks on Phone 2. Measure cue preemption and short-phase preview warnings while timer pace stays unchanged. Phone 1 remains supported but its device-specific behavior is unverified; Phone 2 results do not establish a Phone 1 pass.

Learning: Gradle wrapper, app lifecycle, Compose state, monotonic time, coroutines, foreground service versus UI thread.

## P1: Routine contracts, local data and timer engine

Requirements: R01-R03, R05-R06, R09-R10, R18; foundations for R13-R16/R22-R24.

Task groups: define versioned model/schema and shared fixtures, including routine-scoped history identifiers; validate types/durations/names/phases, generate missing display names with explicit origin and suppress generated-name speech; model simple-to-advanced copying equivalence; implement deterministic loop/set/step/rep/phase cursor, skip-last semantics, display projections and manual wait. Add pause/resume, previous/next, restart step/routine and stop cancellation contracts. Room repositories, schema/migrations, atomic saves, portable settings and session checkpoints. Reject empty/invalid routine saves consistently across all write paths. Implement device-local recovery drafts separately from valid saved routines, without history, playback or sync. No HTTP service yet.

Gate: fake-clock tests cover all timing boundaries, manual early completion/clamping/static, navigation state, skipped sets and phased repetitions. Room tests verify persistence/reopen and rollback. Valid routine export/import round-trips in contract fixtures. Engine independent of speech/network/UI.

Contract gate: normal imports generate independent copies with remapped IDs and unchanged existing routines. Retain skip-last preferences but make them inactive in single-set loops, including imported data; restore their effect when multiple sets return. Simple-to-Advanced conversion still preserves one-set final-rest omission. Rep count-up shows 1 at start and N during the last rep without completing early; timed count-up still starts at 0.

Learning: Kotlin data/sealed classes, nullability, immutable snapshots, interfaces/fakes, Room transactions and migrations; compare repositories to Ecto contexts.

## P2: Routine library and modern offline editors

Requirements: R02-R06, R17-R23, R25; saving part of R13.

Task groups: design/review readable light/dark/system UI; Home/quickstart; Simple editor and safe Advanced-copy conversion; named-loop Advanced editor with three disclosure states and bulk controls; drag/reorder loops/steps/phases; independent copy insertion from routines and searchable loops. Automatic palette with explicit prepare/rest roles; duplicate/delete; global/per-step settings. Search library/current routine, undo/redo, JSON import/export validation/preview. Local autosave every 60s on dirty content and flush on exit with validation, explicit Save action and separate local recovery of invalid unfinished edits.

Valid exit saving is a normal history-producing save even when a minute autosave already persisted the edited content. Keep session edit tracking separate from database dirty state; an unchanged editor exit creates no backup. Successful saves of new changes, including autosaves/imports/restores, trigger history cleanup; failed or unsaved edits do not. Add the synced configurable home timezone, initialized from the first phone and fixed until user change, to settings.

Gate: edit a prehab routine, insert it before lower-body mobility, undo/redo, collapse/drag/reopen at middle, save/kill/reopen in airplane mode. Search loops finds containing routine and reuse copies preserve colors with new IDs. Leaving/reopening an invalid edit recovers a local draft while the last valid routine remains playable; no draft enters sync/history or triggers cleanup. Invalid import doesn't partially mutate DB; Simple conversion cannot overwrite source. Compose interactions and accessibility checks pass.

Editor gate: verify the agreed Middle/Full fields, cooldown palette cycling with only Prepare/Rest fixed colors, additive JSON imports and a disabled skip-last control for one-set loops. Exact visual styling is reviewed during design; these behaviors are confirmed.

Learning: StateFlow/ViewModels, unidirectional data flow, Compose forms and semantics, persistence versus UI state, transactional undo.

## P3: Complete phone player, audio and background behavior

Requirements: R07-R12, R23/R25; uses prototypes from P0 and engine from P1.

Task groups: implement rep count-up display/voice aligned from 1 at rep start, with completion after the final rep duration; configurable rep Off/Every/Last-three default and overrides; user-provided step-name start voice, suppressing generated names; phase/all/selected announcements with number at rep start and selected phase at its actual boundary. Support English by default and Portuguese; Static voice inherits the global new-step count direction. Curate and license pleasant local sounds with previews and separate volumes. Implement cue planner/cache if latency measurements require it; preview mode. Player context/current-next/estimated totals; controls and confirmed full restart; finish/restart. Complete foreground notification and required movable overlay behavior, dragging within usable bounds, permission denial/revocation fallback, screen-off resources and recovery policies. Test headphones/music/audio focus and stale-cue cancellation.

Confirmed defaults: Brazilian Portuguese for the Portuguese option, count-up for new steps and Every rep voice. Permit short phases, warn in preview when speech will not fit, and let the newest cue interrupt unfinished speech without changing timer pace. Group simultaneous number/phase cues; test stale callback cancellation. These are implementation requirements, not unresolved user choices.

Gate: an actual offline workout with phased reps and manual steps runs correctly with screen off and another app open; all controls work from app/notification and the required movable panel when overlay access is granted. Dragging does not affect workout progression; denial/revocation leaves notification controls usable. Stop cancels audio and returns Home. Short-phase voice behavior is tested and explained; no timing waits for speech. Screen readers and large text retain usable controls. Document force-stop/reboot limits.

Recovery gate: crash/reboot then reopen offers saved session paused with Continue/Restart, without replaying interruption time or stale cues. Explicit Stop leaves no recoverable workout. Verify on Phone 2; record Phone 1 as compatibility-target only, not a required test gate.

Learning: service ownership, audio focus, offline TTS, asynchronous callbacks, cancellation and resource cleanup.

## P4: Google Drive sync, backups and new-device recovery

Requirements: R13-R16, R22-R23; no Phoenix dependency.

Task groups: create user's Google Cloud/OAuth configuration guide and Android authorization; implement account isolation/local credential storage; validate Drive app-data access and revision protocol. Add unique WorkManager sync and immediate best-effort trigger after saves; latest autosave coalescing, pending explicit-save history, visible status and Sync now. Reconcile revisions/conflicts/deletions, retention cleanup, per-routine backup browser/restore and fresh-device onboarding of the current full library/settings. Verify retries, pagination, account revoke/switch, corrupt exports, in-flight edits and duplicate uploads. OAuth credentials setup requires user account actions at this phase; local development proceeds with fakes.

Gate: edit offline with multiple minute autosaves and manual/exit saves; reconnect uploads latest content plus retained saved versions without replaying autosaves. Retain each routine's versions from its own five most recent backup dates in the synced home timezone; inactive dates do not count. A two-week gap does not discard old backups; a routine's new sixth backup date removes only its oldest bucket after a successful new-change save. Autosave triggers cleanup without creating a snapshot/date; import/restore qualifies too. Reconnection and failed saves do not initiate cleanup. Saving routine A does not consume routine B's history dates. Restoring A leaves B and global settings unchanged. Full-library JSON export contains all current routines and portable settings. A fresh install/device restores current routines/settings. Concurrent changes preserve both; deletion isn't silently undone. Restore writes a new revision; cleanup never deletes current data or resurrects pruned backups from a dormant client. Tests use fake Drive/clock; separately verify real-account flow on device and safe test data.

Learning: OAuth identity versus authorization, background work constraints, idempotency, revision lineage, conflict resolution and restore testing.

## P5: Phone release acceptance and readable handoff

Requirements: all phone requirements R01-R23/R25.

Task groups: full real-device acceptance matrix and regressions; performance/battery measurements; accessibility and visual review; migration/import/restore rehearsals. Produce a signed personal-use APK and explain installation/update. Securely back up signing key outside Git so updates can retain app data. Complete README, troubleshooting, module/function docs and learning walkthrough. CI runs relevant checks; no secrets/private DBs/media in artifacts. Validate all requirement coverage rather than stopping at basic timer MVP.

Gate: user can use Timey daily without a web server; all agreed phone features demonstrated, known OS limits explained and accepted, setup/test docs reproducible. User accepts phone milestone before web/API implementation begins.

Device gate: complete the relevant acceptance matrix and at least one full real routine on Phone 2; verify Drive recovery and cross-client conflicts with Phone 2 plus an emulator or isolated test client. Phone 1 testing is not required for acceptance.

Learning: debug versus release APKs, signing keys, CI evidence, profiling and interpreting regressions.

## P6: Documented Phoenix API and sync-compatible backend

Requirements: R24 and desktop portions of R13-R16/R22-R23.

Task groups: scaffold pinned Elixir/Erlang/Phoenix/Ecto SQLite app; contexts validate shared schemas/fixtures; implement local authenticated versioned API for all saved-content features. Define OpenAPI and maintain interactive reference/curl examples; document scopes/tokens, errors, ordering, revision conflicts, schema compatibility, pagination, import/export and history restore. Web Google OAuth client in same Cloud project and Drive sync adapter use tested lineage/history rules. API never operates the active phone engine.

Gate: API can create a phased routine, find/copy a loop, insert a routine, edit/save/restore it, and sync it for Android use. Kotlin/Elixir fixture behavior agrees. ExUnit/context/controller and OpenAPI contract checks pass; docs correspond to actual endpoints. Test unauthenticated, invalid and stale-revision requests.

Extend the required GitHub Actions gate with Phoenix/LiveView and API contract tests, formatting and cross-language fixture validation. Verify that a failing web/API job also blocks merging.

Learning: familiar Ecto/Phoenix contexts, HTTP contracts and cross-language compatibility; Elixir functions always have docs/specs.

## P7: Phoenix LiveView computer editor

Requirements: editor parity for R02-R08/R13/R17-R23/R25, excluding live playback control.

Task groups: spacious responsive routine/loop search and editors with drag, three states, phase/cue configuration, undo/redo, independent insertion and Simple→Advanced copy; global settings, autosave/status/history and Drive conflicts. Reuse contexts from API, not HTTP requests from LiveView to itself. Provide local run instructions; remote hosting remains optional/separate decision.

Gate: create/edit a large routine on Mac, sync and run it offline on phone. LiveView tests cover changes, autosave and conflict UI. API and UI produce equivalent records. Desktop shutdown doesn't affect Android.

Learning: compare Compose state flow with LiveView assigns/events; maintain both clients through shared contracts.

## Coverage and issue migration

| Requirements | Main milestone(s) |
| --- | --- |
| R01-R03, R05-R06, R09-R10, R18 | P1, P2, P3 |
| R04, R17, R19-R22, R25 | P2, P3, P5 |
| R07-R08, R11-R12 | P0 prototype, P3 complete, P5 verified |
| R13 | P2 local, P4 cloud, P6-P7 desktop |
| R14-R16 | P4, P5, P6-P7 |
| R23 | P1 settings model, P2 UI, P3 audio, P4 sync, P7 desktop |
| R24 | P6 |

With approval recorded, convert task groups to GitHub issues with these IDs and dependencies. Don't publish the entire plan as an unstructured single issue, and don't create hundreds of tasks before the first prototype teaches us anything. Use P0 evidence to refine later slices without quietly changing requirements. Track status in issues once migrated; this document retains scope/gates and links.

For every issue needing manual validation, include complete issue-specific test instructions per `TESTING.md`: setup/build, exact test data, numbered actions with expected results, variants/pass-fail criteria, evidence and cleanup. Refine them against the implemented UI before asking the user to test; record results or blocked/unrun cases. Otherwise document why manual testing is not required.

At completion of every issue, reread the entire remaining plan and outstanding dependencies. Check whether the completed work changes technical assumptions, ordering, risk, test coverage or acceptance gates in any later phase. Update affected sections and future issues before moving on; record the evidence and rationale in the completed issue's note and execution plan, including an explicit "No adjustments needed" outcome when applicable. Preserve agreed requirements and seek approval for changes to user decisions. This review is part of issue completion, not an optional end-of-milestone activity.

## GitHub migration

Created 2026-10-02 in the public [JuliaMathias/timey repository](https://github.com/JuliaMathias/timey). GitHub Issues is now the task-status source of truth; this table is a navigation index. Phase trackers retain approved scope and acceptance gates. Split later phases into implementing child tasks when their dependencies and earlier evidence are available. Scaffold/setup task #9 and CI/protection task #10 are complete; #11 and #12 retain the remaining P0 phone prototype gates.

| Phase | Milestone | Tracker |
| --- | --- | --- |
| P0 | [Milestone](https://github.com/JuliaMathias/timey/milestone/1) | [#1](https://github.com/JuliaMathias/timey/issues/1) |
| P1 | [Milestone](https://github.com/JuliaMathias/timey/milestone/2) | [#2](https://github.com/JuliaMathias/timey/issues/2) |
| P2 | [Milestone](https://github.com/JuliaMathias/timey/milestone/3) | [#3](https://github.com/JuliaMathias/timey/issues/3) |
| P3 | [Milestone](https://github.com/JuliaMathias/timey/milestone/4) | [#4](https://github.com/JuliaMathias/timey/issues/4) |
| P4 | [Milestone](https://github.com/JuliaMathias/timey/milestone/5) | [#5](https://github.com/JuliaMathias/timey/issues/5) |
| P5 | [Milestone](https://github.com/JuliaMathias/timey/milestone/6) | [#6](https://github.com/JuliaMathias/timey/issues/6) |
| P6 | [Milestone](https://github.com/JuliaMathias/timey/milestone/7) | [#7](https://github.com/JuliaMathias/timey/issues/7) |
| P7 | [Milestone](https://github.com/JuliaMathias/timey/milestone/8) | [#8](https://github.com/JuliaMathias/timey/issues/8) |

Initial P0 tasks:

- [#9: Scaffold Android and verify Mac/device setup](https://github.com/JuliaMathias/timey/issues/9)
- [#10: Add GitHub Actions and enforce the required merge gate](https://github.com/JuliaMathias/timey/issues/10)
- [#11: Prototype phased reps and offline speech on Phone 2](https://github.com/JuliaMathias/timey/issues/11)
- [#12: Prototype screen-off playback and draggable overlay](https://github.com/JuliaMathias/timey/issues/12)

The [Timey Project board](https://github.com/users/JuliaMathias/projects/7/views/3) is created, linked to the repository and contains the phase trackers, implementation tasks and maintenance issues. Columns are Backlog, Ready, In progress, Review and Done. Maintain current status and native parent/blocking relationships per [WORKFLOW.md](WORKFLOW.md#project-status-and-issue-relationships); the board holds live state rather than this historical migration snapshot. The project retains default private visibility. Required CI and main protection were implemented and verified in #10 through failing/repaired test PR #14; scaffold/CI PR #13 merged only after all final-revision checks passed. See [migration handoff](plans/github-migration.md).

## Approval checkpoint

GitHub Issues, phase milestones and a Project board are selected. The user approved the delivery plan and phase order on 2026-10-01. The public JuliaMathias/timey repository was created on 2026-10-02; eight milestone trackers and four P0 tasks are created; the linked Project board is created and populated. Verify actual required branch protection in P0 alongside its CI workflow. The scaffold and CI are integrated; complete the remaining phone prototype checks in #11 and #12 using their issue-specific procedures. Phone acceptance remains required before API/web implementation.
