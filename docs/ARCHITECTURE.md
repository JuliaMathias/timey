# Technical architecture

Status: proposed design, awaiting plan approval. Monorepo choice approved 2026-10-01. Current repository contains documentation only.

## Repository and boundaries

One repository contains `android/`, later `web/`, and shared `docs/` and `contracts/`. Builds and release versions are independent. Share JSON schemas and behavioral fixtures, not executable Kotlin/Elixir code. Continuous integration runs relevant paths plus shared-contract checks. A future split is possible if ownership/distribution diverges; it would require versioned contract releases and cross-repository compatibility checks.

Android: Kotlin, Compose/Material 3, ViewModels, StateFlow/coroutines, Room/SQLite, WorkManager, platform TTS and foreground-service/notification adapters. Start with clear packages in one app module; extract the pure Kotlin domain/engine to a small module for isolated JVM tests. Prefer explicit constructor injection initially. Use Kotlin serialization for portable documents. Pin compatible stable AGP/Kotlin/Compose/Room versions in the version catalog when scaffolding; commit Gradle wrapper and use its checksum. Do not guess version compatibility from release numbers.

Later web: Phoenix LiveView, Ecto/SQLite, application contexts, REST controllers and OpenAPI docs. Local Mac hosting is sufficient initially. UI and API share contexts; all domain behavior is independently tested against cross-language fixtures. No permanent web host is needed for Android Drive sync.

## Android layers

Compose screens → ViewModels → repositories/use cases → Room and sync adapters. The timer service owns an engine session; UI and notification controls send the same commands and observe the same state. Domain code knows nothing about Activities, Google accounts, or speech engines.

Proposed packages: `domain/model`, `domain/validation`, `domain/playback`, `data/local`, `data/sync`, `platform/audio`, `platform/playback`, `ui/library`, `ui/editor`, `ui/player`, `ui/settings`. Create them when implementation needs them, not as empty boilerplate now.

## Data model and portable contract

- Routine: stable UUID, Simple/Advanced discriminator, name, schema version, revision lineage, timestamps; only its type's payload exists. Simple fields include prepare/work/rest/cooldown durations, set count and resolved skip-final-rest policy.
- Advanced payload: ordered loops. Loop has stable UUID, name, set count and ordered steps. A library search indexes loops through their containing routine rather than inventing independently linked reusable objects.
- Step: UUID, name, explicit role, time or repetition payload, color, display/confirmation, start cue, final cue settings, voice inheritance/overrides and skip-last-set.
- Repetition: count and either a pace duration or an ordered nonempty phase list. Phase has UUID/name/duration. Integer milliseconds throughout; decimal seconds are an input/output presentation.
- Naming: store a nonempty display name plus generated/user-provided origin for every named item. Generate a numbered label when absent; suppress generated name cues without suppressing rep numbers. Preserve origin through copying, import/export, sync and API records; never infer origin from text matching "Set 1".
- Portable settings: palette, role colors, theme, home timezone for backup dates, new-step count direction (count-up initially), rep voice frequency (Every rep initially), voice language/preferences (English default; Brazilian Portuguese supported) and sound volumes. Static voice inherits the global new-step count direction unless overridden on the step; do not add an independent Static direction default. Local-only settings: selected installed voice, permissions, device/account session, speech cache and editor disclosure/undo state.
- Session: immutable execution snapshot and cursor (loop/set/step/rep/phase), status, remaining/elapsed offsets and monotonic baseline. Keep it outside cloud content.
- Saved-version history: immutable snapshot (routine versus full-library/settings scope awaits user decision), unique save ID, save kind (manual or edited-session exit), recorded UTC time, source device and checksum. Derive calendar dates using the synced configurable home timezone; retain every snapshot in the five most recent dates containing backups, ignoring inactive dates. Cleanup follows a successful save of new changes (including autosave/import/restore); elapsed time/reconnect/typing alone do not trigger it. Minute autosaves create no snapshots or backup-date buckets. An unchanged editor exit creates no backup. Current data is independent of history retention.

Store routines transactionally in Room with child rows or a versioned payload plus indexed metadata (choose in P1 based on validation/search needs). Keep runtime models separate from persistence entities. DataStore is optional for device-local small preferences; synced settings stay in the repository's data model. Test migration/export consistency before finalizing schema.

In P1 create `contracts/routine.schema.json` and representative fixture JSON for simple, repeated loops, skipped final steps, phased reps and manual/static modes. Include conversion equivalence and validation failures. No HTTP implementation is needed for this contract. Timestamps are UTC ISO-8601, durations integer milliseconds, ordering explicit, IDs UUIDs. Use unknown-field/version migration rules to avoid accidental data loss.

Normal routine JSON import is additive copying: remap routine/loop/step/phase IDs together, keep content/order/colors, and leave existing routines untouched. Backup restore is a distinct identity-preserving operation under revision/conflict rules, not an option silently applied during import. Validate before mutation and commit each accepted import atomically. Explicit portable-settings import behavior must be presented separately from adding routine copies; do not infer permission to replace preferences from routine import. See [ADR 0006](decisions/0006-additive-routine-import.md).

Editor disclosure: Middle exposes name/role/duration-or-reps/phase-summary/color; Full adds sound/voice/display/confirmation/skip settings. New work/custom/cooldown steps cycle colors; Prepare/Rest use fixed configurable colors. Imported/copied colors survive. Disable skip-last configuration for single-set loops but preserve the stored preference on reduction/import. Effective omission requires the stored flag AND set count greater than one AND the current set being final. Increasing set count reactivates the preference. Simple conversion must preserve final-rest omission, including a one-set routine, without relying on an inactive Advanced flag. See [ADR 0007](decisions/0007-dormant-skip-preference.md).

Initialize portable home timezone from the first phone only when no saved/shared preference exists. Sync it and leave it fixed across travel, system-timezone changes and new-device onboarding until the user explicitly changes it. Derive history dates from stored UTC times using that shared preference.

## Deterministic timer and session control

Engine states: idle, running, paused, awaiting confirmation, finished, stopped. Compute progression from injected monotonic elapsed time, not repeated 'subtract one' operations. Pause stores offsets; resume anchors a new deadline. Rendering ticks only refresh the display. A delayed callback reconciles all crossed boundaries, reaches the correct current state, and drops obsolete cue events instead of playing a burst of missed speech.

Build a bounded execution schedule/cursor from a validated immutable snapshot. Expand loop/set semantics, skip-last flags and phase boundaries without unbounded memory use. Count-up/down display are projections of the same timing; reaching the timing target clamps manual display and enters awaiting-confirmation. Static keeps value fixed but emits timing/cues until target and waits. Manual checkmark can advance early. At manual waits no next step begins until user acts.

Rep count-up projects the current rep ordinal (1 immediately at step start), not the number completed. Display and voice align at rep boundaries; elapsed phase timing still determines completion, so displaying N cannot finish a step before the last rep is done. Time count-up continues to project elapsed duration from zero. Preserve final target clamping for manual waits. See [ADR 0008](decisions/0008-current-rep-display.md).

Previous/Next use executed-step occurrences, including set boundaries; restart-step resets its rep/phase; restart-routine resets full cursor after confirmation. Preserve paused/running intent. Stop cancels timing/audio, releases resources, clears resumable session, and makes Home the entry screen. Editing/sync never modifies the session snapshot.

## Background runtime and banner

Start a foreground service from an explicit foreground Start action. Publish a notification with pause/resume/stop and state. Select a valid service type during P0; `specialUse` is a candidate for an interval timer, while `mediaPlayback` only fits actual qualifying playback. Never mislabel a timer as health tracking or data sync to evade restrictions. Platform requirements vary by target SDK.

A foreground service does not itself keep the CPU awake. Prototype a carefully bounded partial wake lock for running sessions if needed for precise screen-off phase cues; release on pause/manual wait/finish/stop and exceptions. Keep-screen-on is separate and optional. WorkManager is for sync, not timer execution. Exact alarms are not the default mechanism for every rep/phase; any future scheduled reminders need their own design.

Timey requires a persistent movable panel over other apps. The reference app's implementation remains unknown; screenshots do not establish which APIs it uses. Prototype a `TYPE_APPLICATION_OVERLAY` window with explicitly granted draw-over-other-apps access on both phones. A platform adapter renders service-owned session state and routes controls to the same engine; it must not create a second timer. Dragging changes window position without changing playback. Keep positions device-local and within usable display bounds after rotation or size changes.

Retain the actionable foreground notification independently of the panel. Denied or revoked overlay access falls back to notification controls. Preserve running/paused dismissal behavior; Stop removes the window, cancels cues, clears recovery and returns Home. Android may hide overlays in protected surfaces and system panels; do not promise literally always visible. Avoid notification sounds interrupting rep cues on every update. Verify permission, drag and resource cleanup in P0/P3. See [ADR 0009](decisions/0009-movable-background-panel.md).

Persist checkpoints for process recreation. Confirmed recovery: after a crash or reboot, opening the app offers the saved workout paused, with Continue/Restart choices. Preserve position/remaining offsets rather than continuing expired deadlines or automatically replaying elapsed cues; measure checkpoint/recovery fidelity during P0/P3. Explicit Stop clears recovery. Distinguish app dismissal, OS process kill, explicit notification Stop, user force-stop and reboot. User force-stop cannot be bypassed; recovery happens only on a later app open. No claims of continuous playback through reboot.

## Offline voice and sound

TextToSpeech initialization, language/voice availability, engine performance and speech length vary by device. Choose an installed voice with no network requirement. Offer setup/download guidance before an offline session; missing voice data cannot magically be supplied while offline. Sound fallback works without TTS. Do not add a cloud TTS service or an AI subscription.

Support English and Brazilian Portuguese (pt-BR); English is the default. Verify installed offline voice availability in both languages on both target phones. Initial global new-step direction is count-up and rep voice frequency is Every rep. For Static display, resolve inherited direction from the global new-step count-direction setting when creating the playback snapshot, respecting step overrides.

Create a cue planner separate from the clock: user-provided step name at step start (suppress generated names), rep number at rep start, selected/all phase names at actual boundaries, last-N cue policy based on time/reps. Global defaults resolve into step overrides when building the playback snapshot. Cancel old utterances on navigation/restart/stop; never wait for speech completion to advance the engine.

P0 measures live synthesis latency. If short cues cannot meet timing, pre-synthesize common numbers/names/phases to app-private files using `synthesizeToFile`, with completion listeners. Key cache by text/voice/language/rate; invalidate when these change and generate only necessary phrases with bounded cache size. SoundPool or a suitable local audio player schedules prepared clips. This is an option to validate, not a guarantee of sample-accurate Android playback.

Confirmed speech-overlap policy: permit short phases and warn in preview when a selected phrase cannot fit. A newer cue interrupts unfinished speech and replaces it; never queue stale announcements or change timer pace to finish speech. Group number/phase information due at the same boundary into one utterance so simultaneous cues do not interrupt each other. Verify asynchronous cancellation/callbacks cannot restart an old cue. Test 2.2-second reps and shorter supported phase examples, speaker/Bluetooth, music ducking/interruption, screen-off and airplane mode. See [the cue decision](decisions/0005-short-phase-cues.md). P0 measures latency; it does not impose an arbitrary speech-length-based minimum duration.

Bundle a small gong/bell/bowl/chime set with local license/attribution evidence. Source and audition assets during audio phase. Do not hotlink sounds or imitate proprietary recordings. Sound/voice volume and audio focus behavior have separate controls/tests.

## Save, sync and backups

Every write first commits to local storage. Editor save loop checks dirty state every 60 seconds; explicit Save and leaving editor flush immediately. Save kind determines history: manual and edited-session exit saves create snapshots, minute autosaves do not. Track edits within the editor separately from database dirty state so a prior minute autosave does not suppress the normal exit-save backup. An editor opened and closed unchanged creates no backup. After successfully saving new content, including through autosave/import/restore, cleanup keeps the five most recent backup dates using the synced home timezone; failed writes cannot prune history. Prevent save/autosave/cleanup races with serialized transactions. Validate before every routine write, including autosave and exit save. Invalid or empty editor state cannot overwrite the last valid routine, create history, sync as saved content or trigger cleanup. Show validation errors; persistence of a separate invalid recovery draft requires clarification before implementation.

Online saves trigger best-effort immediate sync in the app and a unique WorkManager job with connectivity constraints. Coalesce superseded autosaves; preserve manual/exit history snapshots. Reconnect reconciles latest changes with Drive and uploads retained history even when older than five calendar days, without initiating cleanup solely on reconnection. OS scheduling can delay background sync, so show Saved locally / Waiting / Syncing / Synced / Action needed and a Sync now button. Failure never blocks playback or loses saved edits.

Use Drive REST API application data storage with narrow `drive.appdata` authorization. Configure Android and later web OAuth clients in the same Google Cloud project/account. Android authorization and account identity are separate concerns; use official Android authorization flow rather than a client secret in the APK. Store credentials through appropriate platform storage and never export/log them.

Drive stores portable documents, not SQLite/WAL files. Proposed conflict-safe protocol:

1. Each device persists latest pending record plus its known parent revision(s), and manual/exit-save snapshots separately.
2. Publish immutable uniquely identified revision documents per routine/settings/deletion. Autosaves queued offline may become one revision based on the last synced parent. Avoid relying on a single overwritten 'latest database' file.
3. List/download revision metadata, deduplicate revision IDs, and determine heads from parent relationships. Concurrent heads are conflicts, not last-writer-wins by device clock. An optional manifest/index accelerates discovery but is not sole authority.
4. Import a non-conflicting newer state transactionally. Concurrent edits expose both copies. Resolution writes a revision acknowledging both parents. Tombstones record deletes; delete/edit races are conflicts.
5. Upload retained manual/exit-save snapshots idempotently by save ID, then mark the exact acknowledged local revision synced. History cleanup follows successful saves of new changes and retains the five most recent distinct backup dates in the shared home timezone; it is not an automatic consequence of upload. If an edit occurs during upload, leave the new revision pending. Reconcile cleanup across devices so a dormant device cannot silently re-upload snapshots already pruned by an acknowledged cleanup event; validate that protocol in P4. Concurrent new backup dates must be reconciled without converting this into clock-age-based expiry or deleting records based on an incomplete offline view.

Validate API capabilities, pagination, crash recovery and consistency in P4 before locking this protocol. Never equate Drive modification time with conflict detection. Large-scale revision garbage collection is deferred; delete obsolete data only after a tested safe compaction design so dormant devices do not resurrect old state. New-edit-triggered calendar history cleanup must not discard lineage/tombstone information required for sync.

Restore downloads/imports valid current data on a new device; history restore creates a fresh revision. Same-account data is shared, installed voices/permissions are configured locally. Local-only account partitions prevent accidental cross-account upload. Current snapshot survives history cleanup. Successful imports/restores that change data qualify as new-change saves for cleanup. Downloading/reconciling existing cloud state during onboarding/sync is distinct from a user import/restore and must not itself initiate cleanup. Keep retained old snapshots regardless of inactivity; only surplus backup-date buckets are pruned after the qualifying event.

Google OAuth test-mode constraints can require renewed authorization; validate personal-use publishing/scopes before release. Do not promise no Cloud setup or permanent tokens.

## Later documented API and web editor

Phoenix exposes versioned `/api/v1` resources with token authentication (stored hashed, scoped/revocable), validation errors, optimistic revision preconditions and idempotency for retryable mutations. Bind locally by default; separately decide public hosting/security. Google access tokens are not Timey API tokens.

Cover R24 completely; read/write the same contexts as LiveView. Provide OpenAPI in `contracts/openapi.yaml`, interactive reference docs, curl/JSON examples, authentication setup, pagination/search, ordering/copy/conversion, error codes, revision conflicts, history and schema-version compatibility. Select OpenAPI version based on supported Phoenix tooling (current specification is 3.2.1; a supported 3.1 dialect remains an option). Endpoint implementation, contract docs and tests ship together.

The web editor gets its own local DB and Drive sync identity. Both Android and web use the portable contracts and revision rules. Desktop saves flow through Drive to Android when available; the phone remains usable when the computer is off. API creation does not control a live phone session.

## Technical risk order

First: target-device background timing and offline TTS/cue cancellation. Next: editor validation/undo and composition. Then: Drive OAuth, revision races, history retention and new-device recovery. Finally: Kotlin/Elixir contract compatibility and web/API usability. See `PLAN.md` for gates and `TESTING.md` for evidence.
