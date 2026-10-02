# Testing and acceptance strategy

Every feature ships with meaningful tests. Use the smallest level that proves behavior, plus device evidence where Android or audio is involved. A passing compile is not a feature test. The initial Android foundation has two Compose instrumentation cases for launch/network-permission absence and Activity recreation. JVM timing tests follow in #11; never describe a NO-SOURCE task as passing behavioral cases. Also validate documentation links and Git diffs.

## Automated levels

Pure Kotlin tests cover models/validation, routine expansion/cursor, phases, display values, confirmation, navigation, cue planning and conflict/history logic. Inject fake monotonic/UTC clocks; test transitions without sleeping. Prefer fakes over elaborate mock expectations.

Repository/ViewModel tests cover dirty editor state, transactional save, undo/redo, search, import validation, settings inheritance, sync status and mutations during sync. Android Room integration/migration tests exercise real database behavior. Compose instrumentation tests cover critical user flows and accessibility semantics, rather than brittle pixel-exact reproduction of reference screens.

Later ExUnit tests cover contexts, Ecto migrations, API authentication/validation/revision conflicts and LiveView editor flows. Shared fixture/schema tests on both platforms prove conversion, durations, ordering, omission and export compatibility. OpenAPI validation/examples must match API implementation.

## Required behavioral cases

| Area | Cases to prove |
| --- | --- |
| Timer | A/B ×3, cross-set navigation, delayed tick reconciliation, fractional rep pace, Down/Up ×10, pause/resume, restart-step/routine, zero optional durations, empty/all-skipped rejection, skip any final-set step in multi-set loops; one-set Advanced skip control disabled with stored flag inactive; reduction/import preserves choice, increasing sets reactivates it; Simple one-set final-rest omission preserved by conversion. |
| Display/manual | Time and reps in countdown/count-up/static; current rep 1 displayed/spoken at start, N displayed during last rep with no early completion; time count-up starts at zero; correct phase boundaries; early checkmark; manual clamping/wait; no next step begins during wait. |
| Cues | Step-start name, last-N seconds/reps, global/step overrides, all/selected phases; selected Up in Down → Up announces number at rep start and Up at its boundary; Static inherits direction (count-up initially) and respects step override; Every rep factory default; English default and pt-BR selection; short-phase preview warning, newest cue interrupts unfinished speech without changing timing, simultaneous cue grouping and stale callback cancellation; cancel on pause/skip/restart/stop, missing offline voice, no stale cue backlog. |
| Editing | Three disclosure states/bulk buttons with agreed Middle/Full fields, drag entire loop, independent copy IDs/order/colors, Simple conversion equivalence, cooldown/work/custom palette cycling and fixed Prepare/Rest roles, search containing routine, undo/redo and redo invalidation; routine JSON import remaps all IDs and leaves existing routines unchanged; restore preserves identity via its separate flow. |
| Persistence | Changed-only 60-second autosave without history; manual/edited-session exit saves create history, including exit after a minute autosave; unchanged exit creates no backup; successful new-change saves/import/restore trigger cleanup, failed writes/typing do not; invalid/empty routine rejection for manual/autosave/exit/API/import writes with no history or cleanup; local invalid-draft recovery after exit/reopen/process recreation, last valid routine preserved, drafts excluded from playback/history/sync, validated promotion to a saved routine; missing names generated and marked, generated-name cues suppressed while user-provided identical text can speak, naming origin preserved through copies/import/sync; concurrent save calls, DB reopen/migration/rollback, import rejected before mutations. |
| Sync | Offline autosave coalescing; explicit backups preserved; retries/dedup/pagination; edits arriving during upload; disconnected/revoked auth; conflicting concurrent heads; delete/edit conflict; new account isolation; fresh install restore. |
| History | Five most recent distinct backup dates per routine in the shared home timezone with fake clock (midnight/DST/different device zones); first phone initializes zone, travel/new-device restore do not overwrite it, explicit zone change propagates; all snapshots within each date retained; inactive dates ignored; sixth date prunes only that routine's oldest bucket; saving A preserves B's date budget/history, restoring A leaves B and global settings intact; full-library JSON export includes current routines and portable settings; autosave cleanup adds no snapshot/date; long-offline history uploads without time/reconnect pruning; failed saves do not prune; imports/restores qualify; cross-device date reconciliation/dormant re-upload prevention; restore as new revision, corrupted snapshot refusal, current data survives cleanup. |
| API later | Auth scopes/errors, CRUD of all content, order/copy/conversion/search, import/export/history/restore, stale update preconditions, request retry idempotency, docs/examples/contract parity. |

## Real-device matrix

Use both confirmed phones for device acceptance and at least one full real routine on each, and an emulator for repeatable UI flows. Targets reported on 2026-10-01: Galaxy S24 Ultra SM-S928B/DS, Android 16 / One UI 8.5; Galaxy S22 Ultra SM-S908E, Android 16 / One UI 8.0. Recheck installed software at test time. Record model, Android/One UI version, target SDK, APK commit, voice engine/language, permissions, scenario, expected/actual behavior and evidence. Do not commit personal screenshots or tokens.

P0 validates background timing, offline speech in English and Brazilian Portuguese (pt-BR), short-phase cue interruption with unchanged timer pace, and movable overlay/notification fallback behavior on both devices. P4/P5 use them as independent sync clients to verify restore and concurrent-edit conflicts; fresh-install checks use safe test data and an explicit data-preservation procedure. If a phone is unavailable, mark its cases blocked/not run rather than inferring a pass from the other phone.

Check airplane mode; screen on/off; app background/another app; running/paused notification and banner dismissal; stop/return Home and panel removal; dragging over other apps without timing changes; panel bounds after rotation; overlay permission denial/revocation and usable notification fallback; music/headphones/Bluetooth; rotation/large text/TalkBack; battery saver/idle; process kill and separately force-stop/reboot recovery. Verify wake resources release after stop/finish and waiting, and measure actual timing/audio drift against the P0-agreed tolerance. Hardware-specific results are not universal Android guarantees.

After crash/reboot, verify reopening offers saved workout paused with Continue/Restart and no stale audio or automatic progression through the interruption. Verify Explicit Stop clears the saved session. Do not equate process recreation with reboot; capture checkpoint position/remaining-time fidelity for each case.

## Complete manual-test instructions in each issue

Whenever a feature or fix needs manual validation, its issue must contain a procedure the user can execute without reconstructing setup from chat. The matrix above identifies scenarios, not complete instructions. Each affected issue supplies the following, kept current as implementation changes:

1. **Purpose and prerequisites:** requirement/acceptance criterion being checked; phone versus emulator; build/commit and how to obtain/install it; required permissions, offline voice, accounts/accessories and initial network/app state. Link verified installation instructions and name any issue-specific setup.
2. **Exact test data:** routine name, loop/set/step/rep/phase durations, cue/display settings, or a supplied fixture/import file. Use disposable test content and specify the starting screen and saved/running/paused state.
3. **Numbered actions and observations:** exact buttons/screens/commands, with the expected result after each action. Define durations/waits and distinguish app dismissal, process kill, force-stop and reboot where relevant. Use actual implemented labels; avoid ambiguous instructions such as "try background mode."
4. **Variants and pass/fail:** required online/offline, permission, pause/run or accessory variants; observable success/failure criteria and the agreed timing tolerance/measurement method when timing matters. Do not invent a tolerance or present unvalidated OS behavior as guaranteed.
5. **Evidence and result recording:** model/OS, build/commit, relevant voice/permission settings, expected versus actual results, pass/fail/blocked/not run per case, and requested redacted logs/screenshots or measurements. Explain how to capture any non-obvious evidence.
6. **Cleanup and recovery:** stop the timer, restore changed network/permission/accessibility settings, and remove disposable content if appropriate. Tests involving reinstall/data clearing/account changes must explain data impact and how to preserve/restore user content; never casually clear the user's real database.

Keep the executable instructions in the issue body, even if reusable setup is linked. A generic checklist or link to this strategy is insufficient. If manual tests are unnecessary, write "Not required" with the reason. Missing device/account access means blocked or not run, never passed. Manual evidence complements the mandatory automated GitHub Actions checks; it does not waive them.

## Expected commands after scaffolding

From `android/`: `./gradlew testDebugUnitTest lintDebug assembleDebug`. With emulator/phone: `./gradlew clean connectedDebugAndroidTest`, followed by `python3 ../scripts/verify_android_tests.py app/build/reports/androidTests/connected/debug/index.html`. The result guard rejects missing/zero/skipped/failed execution even if Gradle exits zero; cleaning prevents stale reports. Run its six regression cases from root with `python3 -m unittest discover -s scripts -p 'test_*.py'`. Confirm task names in P0 and update docs if modules/flavors change. JVM domain-module task is added once that module exists.

From `web/` later: `mix format --check-formatted` and `mix test`, plus the selected OpenAPI/schema lint command. The Android wrapper now exists; see README and the foundation execution plan for actual check results. The web commands remain future commands: no Mix project exists.

Continuous integration must use a compatible pinned JDK/SDK and run the established automated suites on PRs. Device acceptance cannot be replaced by hosted CI alone. Avoid real Google accounts in standard CI; credentials are only used in deliberate integration verification.

## Required GitHub Actions merge gate

User requirement, agreed 2026-10-01: automated tests must run and pass through a GitHub Actions workflow before a PR can merge. The Android workflow is being introduced alongside the scaffold; verify its runs and remote enforcement in #10 before merging.

- Trigger CI for every PR targeting `main`, including new commits and branch updates; also run on pushes to `main` to detect integration regressions. Start with all established suites on every PR rather than path-filtering required workflows. Optimize later only with verified coverage and an always-reported merge gate.
- Android checks include the pure Kotlin/domain and app unit tests, lint and debug build. Add Room migration/integration and critical Compose instrumentation tests on a CI emulator as those suites are introduced. Real-phone audio/background evidence remains separately required where relevant.
- When contracts exist, validate schemas/fixtures. When the Phoenix application exists, add ExUnit context/controller/LiveView and API contract tests plus formatting checks. A monorepo PR cannot omit an established required suite merely because its author believes the change is unrelated.
- Use a stable, uniquely named final check such as `ci-required`. It runs even when dependencies fail and succeeds only when all mandatory jobs actually succeed. It must reject failed, cancelled, unexpectedly skipped or missing required jobs. Never use `continue-on-error` for required tests or turn a test failure into a successful status.
- Configure `main` protection to require PRs, the Actions-produced final check, and an up-to-date branch. Apply the protection to administrators and prevent routine bypass. An earlier green run does not authorize merging later changes. Add future suites to the gate without weakening its success criteria.
- Verify with a test PR containing a failing test: merge must be blocked; repair and rerun on the new revision, then verify eligibility. Record workflow paths, check names and protection settings in README. GitHub can treat skipped checks as acceptable, so the final gate must explicitly validate required job results.

Check account/visibility support before remote setup: GitHub's branch protection for a private repository requires a supporting paid plan such as Pro; public repositories support it on Free. If unavailable, report the enforcement gap and obtain a user decision rather than claiming merges are technically blocked. This does not authorize buying a subscription or making the project public.

## Reporting

Each issue/PR records commands run, results, relevant device evidence and unrun checks. A reviewer verifies requirement IDs and failure cases. Mark complete only when behavior is demonstrated; blocked hardware/account work stays visible. No coverage percentage substitutes for these acceptance cases.
