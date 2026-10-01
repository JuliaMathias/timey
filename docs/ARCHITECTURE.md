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
- Portable settings: palette, role colors, theme, home timezone for backup dates, new-step count direction, voice language/preferences (English default; Portuguese supported) and sound volumes. Static voice inherits the global new-step count direction unless overridden on the step; do not add an independent Static direction default. Local-only settings: selected installed voice, permissions, device/account session, speech cache and editor disclosure/undo state.
- Session: immutable execution snapshot and cursor (loop/set/step/rep/phase), status, remaining/elapsed offsets and monotonic baseline. Keep it outside cloud content.
- Saved-version history: immutable snapshot, unique save ID, save kind (manual or edited-session exit), recorded UTC time, source device and checksum. Derive calendar dates using the synced configurable home timezone; retain every snapshot in the five most recent dates containing backups, ignoring inactive dates. Cleanup follows a successful save of new changes (including autosave/import/restore); elapsed time/reconnect/typing alone do not trigger it. Minute autosaves create no snapshots or backup-date buckets. An unchanged editor exit creates no backup. Current data is independent of history retention.

Store routines transactionally in Room with child rows or a versioned payload plus indexed metadata (choose in P1 based on validation/search needs). Keep runtime models separate from persistence entities. DataStore is optional for device-local small preferences; synced settings stay in the repository's data model. Test migration/export consistency before finalizing schema.

In P1 create `contracts/routine.schema.json` and representative fixture JSON for simple, repeated loops, skipped final steps, phased reps and manual/static modes. Include conversion equivalence and validation failures. No HTTP implementation is needed for this contract. Timestamps are UTC ISO-8601, durations integer milliseconds, ordering explicit, IDs UUIDs. Use unknown-field/version migration rules to avoid accidental data loss.

## Deterministic timer and session control

Engine states: idle, running, paused, awaiting confirmation, finished, stopped. Compute progression from injected monotonic elapsed time, not repeated 'subtract one' operations. Pause stores offsets; resume anchors a new deadline. Rendering ticks only refresh the display. A delayed callback reconciles all crossed boundaries, reaches the correct current state, and drops obsolete cue events instead of playing a burst of missed speech.

Build a bounded execution schedule/cursor from a validated immutable snapshot. Expand loop/set semantics, skip-last flags and phase boundaries without unbounded memory use. Count-up/down display are projections of the same timing; reaching target clamps manual display and enters awaiting-confirmation. Static keeps value fixed but emits timing/cues until target and waits. Manual checkmark can advance early. At manual waits no next step begins until user acts.

Previous/Next use executed-step occurrences, including set boundaries; restart-step resets its rep/phase; restart-routine resets full cursor after confirmation. Preserve paused/running intent. Stop cancels timing/audio, releases resources, clears resumable session, and makes Home the entry screen. Editing/sync never modifies the session snapshot.

## Background runtime and banner

Start a foreground service from an explicit foreground Start action. Publish a notification with pause/resume/stop and state. Select a valid service type during P0; `specialUse` is a candidate for an interval timer, while `mediaPlayback` only fits actual qualifying playback. Never mislabel a timer as health tracking or data sync to evade restrictions. Platform requirements vary by target SDK.

A foreground service does not itself keep the CPU awake. Prototype a carefully bounded partial wake lock for running sessions if needed for precise screen-off phase cues; release on pause/manual wait/finish/stop and exceptions. Keep-screen-on is separate and optional. WorkManager is for sync, not timer execution. Exact alarms are not the default mechanism for every rep/phase; any future scheduled reminders need their own design.

The persistent top banner in the reference might involve repeatedly refreshed heads-up notifications or an overlay; images do not prove implementation. P0 compares supported approaches on the user's phone. Prefer a reliable actionable notification as baseline. Add an optional `TYPE_APPLICATION_OVERLAY` window with explicitly granted draw-over-other-apps access if necessary to satisfy the visible panel behavior. Permission denial falls back gracefully. Android may hide overlays in protected surfaces and system panels; do not promise literally always visible. Avoid notification sounds interrupting rep cues on every update.

Persist checkpoints for process recreation. Confirmed recovery: after a crash or reboot, opening the app offers the saved workout paused, with Continue/Restart choices. Preserve position/remaining offsets rather than continuing expired deadlines or automatically replaying elapsed cues; measure checkpoint/recovery fidelity during P0/P3. Explicit Stop clears recovery. Distinguish app dismissal, OS process kill, explicit notification Stop, user force-stop and reboot. User force-stop cannot be bypassed; recovery happens only on a later app open. No claims of continuous playback through reboot.

## Offline voice and sound

TextToSpeech initialization, language/voice availability, engine performance and speech length vary by device. Choose an installed voice with no network requirement. Offer setup/download guidance before an offline session; missing voice data cannot magically be supplied while offline. Sound fallback works without TTS. Do not add a cloud TTS service or an AI subscription.

Support English and Portuguese; English is the default. Verify installed offline voice availability in both languages on both target phones. The Portuguese locale is a remaining user choice. For Static display, resolve inherited direction from the global new-step count-direction setting when creating the playback snapshot, respecting step overrides.

Create a cue planner separate from the clock: step name at step start, rep number at rep start, selected/all phase names at actual boundaries, last-N cue policy based on time/reps. Global defaults resolve into step overrides when building the playback snapshot. Cancel old utterances on navigation/restart/stop; never wait for speech completion to advance the engine.

P0 measures live synthesis latency. If short cues cannot meet timing, pre-synthesize common numbers/names/phases to app-private files using `synthesizeToFile`, with completion listeners. Key cache by text/voice/language/rate; invalidate when these change and generate only necessary phrases with bounded cache size. SoundPool or a suitable local audio player schedules prepared clips. This is an option to validate, not a guarantee of sample-accurate Android playback.

Resolve speech-overlap with a documented policy: don't queue stale speech, prioritize current rep/phase information, and warn when a selected phrase cannot fit its phase. Preview demonstrates this before exercise. Test 2.2-second reps and the shortest approved phases, speaker/Bluetooth, music ducking/interruption, screen-off and airplane mode. Selected-phase behavior is confirmed: number at rep start, phase name at its actual boundary; voice-overlap policy and Portuguese locale remain review questions.

Bundle a small gong/bell/bowl/chime set with local license/attribution evidence. Source and audition assets during audio phase. Do not hotlink sounds or imitate proprietary recordings. Sound/voice volume and audio focus behavior have separate controls/tests.

## Save, sync and backups

Every write first commits to local storage. Editor save loop checks dirty state every 60 seconds; explicit Save and leaving editor flush immediately. Save kind determines history: manual and edited-session exit saves create snapshots, minute autosaves do not. Track edits within the editor separately from database dirty state so a prior minute autosave does not suppress the normal exit-save backup. An editor opened and closed unchanged creates no backup. After successfully saving new content, including through autosave/import/restore, cleanup keeps the five most recent backup dates using the synced home timezone; failed writes cannot prune history. Prevent save/autosave/cleanup races with serialized transactions. Autosave must not overwrite partially typed invalid fields: retain a local draft and expose validation while keeping the last valid runnable routine.

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
