# Timey product specification

Status: product requirements and Android-first delivery plan approved on 2026-10-01; implementation pending. Last updated: 2026-10-01.

Timey is a personal Android interval timer with flexible exercise routines. The phone works independently offline. Later, a Phoenix LiveView editor and documented API make large routines easier to create on a computer. There are no ads or subscriptions in scope.

## Vocabulary and reference behavior

A routine is Simple or Advanced. An Advanced routine contains ordered loops. A loop has a name, a positive set count, and ordered steps; it cannot contain other loops. A step uses either a fixed time or a number of paced repetitions. A repetition can contain ordered named phases with individual durations. Phases repeat identically for every repetition; they are not nested loops.

Three sets of A/B execute A, B, A, B, A, B. An Advanced step marked skip-on-last-set is omitted on its containing loop's last pass when the loop has multiple sets; the stored preference is inactive for a single-set loop. This can apply to any step. Countdown/Count-up define the displayed number; rep count-up shows the current rep starting at 1, whereas timed count-up shows elapsed time starting at 0. Manual confirmation does not delay starting: timing begins immediately, but reaching the target waits for the checkmark. The checkmark is usable early. Static keeps its configured value visible while timing/cues run, then continues waiting for confirmation. Navigation preserves paused/running state.

The reference app replaces Advanced content when Simple is saved. Timey deliberately avoids that behavior: types are separate, and conversion creates a new Advanced routine.

## Requirements and observable acceptance

| ID | Requirement | Acceptance example / important boundary |
| --- | --- | --- |
| R01 | Full offline phone operation | After setup, create/edit/save/search/play a routine in airplane mode. No Phoenix, Drive, or login is required for local use. |
| R02 | Quickstart and Simple routines | Sets, work, rest, preparation, cooldown, naming and saving. Zero preparation/rest/cooldown omit those stages. Skip-final-rest is a configurable general default. Total includes repetitions and selected omissions. |
| R03 | Named loops | Create/rename/duplicate/delete/reorder loops, edit set count, show repeat indicator above one set. A/B ×3 follows the sequence above. No nested loops. |
| R04 | Three editor disclosure levels | Collapsed loop shows name/set count/duration. Middle shows step name, role, duration/reps, phase summary and color. Full also shows sound/voice, display, confirmation and skip settings. Open at middle. Buttons collapse all, expand all to middle, and fully expand all. Drag a collapsed loop as a whole. |
| R05 | Configurable steps | Name, explicit role (prepare/work/rest/cooldown/custom), color, TIME or REPS, duration or rep count/pace, duplicate/delete/reorder, display, start cue, final cues, confirmation, skip-last-set. Disable skip-on-last-set control at one set; preserve any stored choice but make it inactive until multiple sets return, including imported single-set loops. Decimal seconds supported without float timing drift. |
| R06 | Repetition phases | Example: 10 reps with Down 2s + Up 4s totals 60s. Phases are named, ordered, addable/removable/reorderable, identical each rep, and sum to rep duration. Ordinary reps still support a single pace duration. |
| R07 | Voice repetition and phase options | Off/every rep/last three; Every rep is the initial global default, with per-step override. Numbers follow countdown or count-up. Modes: number only; number plus every phase; number plus a selected phase. Number begins at rep start; selected phase name is spoken when that phase begins. Step-start voice can read the name. English and Brazilian Portuguese (pt-BR) supported, English default. |
| R08 | Pleasant sound choices | Gong default, small curated bell/bowl/chime choices with previews and separately adjustable voice/sound volumes. Final-three time cues occur at remaining 3/2/1 seconds; rep cues occur at the start of the last three reps. Cue counts configurable. Use locally bundled assets with recorded licenses. |
| R09 | Display and confirmation | Timed countdown reaches 0; timed count-up starts at 0 and reaches target. Rep countdown counts remaining reps; count-up shows current rep starting at 1 immediately. Reaching displayed rep N is the start of the final rep, not completion; Auto advances only after its duration/phases finish. Manual freezes at target until checkmark, usable early. Static is manual and fixed, while cues still run. |
| R10 | Playback controls | Previous/Next move one executed step across loop/set boundaries; restart-step starts its first rep/phase; all preserve paused/running state. Restart-routine asks confirmation and starts at routine beginning preserving state. Pause/resume and hold-to-exit; notification X completely ends the session. |
| R11 | Playback context and preview | Show current loop, set, rep, phase, next step and remaining routine time. Label time as estimated when manual waits affect it. Completion offers exit/restart. Short preview exercises selected voice/phase/sound configuration without full playback. |
| R12 | Background phone behavior | Screen-off/other-app playback, actionable ongoing notification, and a required movable panel over other apps when overlay access is granted. Dragging changes panel position without changing playback. Running panel persists or returns after dismissal; paused panel can disappear/stay dismissed. Pause/resume and stop work outside app. Stop removes the panel and returns app to Home. Permission denial/revocation uses notification controls as fallback; protected Android surfaces may hide overlays. |
| R13 | Save/autosave | Explicit save; changed edits autosave locally every 60s. Leaving an edited session performs a normal save and creates history, including edits already persisted by minute autosave. An editor opened and closed unchanged creates no backup. Saves are transactional. Online saves request sync; offline autosaves coalesce into latest changes for reconnect. Saving does not block on cloud. |
| R14 | Google Drive sync and new devices | Authorize same Google account on a new device, download latest synced routines/settings, then use locally. Reconnect sends latest content plus pending explicit-save history; retries preserve edits. No need to replay each autosave. |
| R15 | Conflicts and deletions | Concurrent edits preserve both versions and let user choose/keep both. Deletes are recorded so offline devices cannot silently resurrect them. Account switching never uploads one account's private data into another without an explicit choice. |
| R16 | Five-backup-date version history | For each routine independently, keep every saved version from its five most recent calendar dates containing backups; inactive dates do not count. Classify dates in one configurable home timezone shared across devices. Manual Save and normal edited-session exit create versions; minute autosaves do not. Cleanup occurs after a successful save of new changes, including autosave, import or restore; typing, time passing, opening or reconnecting alone do not trigger it. Preserve retained offline history for upload regardless of age. Current data never expires. Restore is a new edit and does not overwrite later versions, subject to the same cleanup policy. |
| R17 | Independent composition | Insert an entire routine at beginning/end/selected position in an Advanced routine, including its exercise sequence, without links to source. All copied IDs are new. Original edits never alter the copy. Preview insertion and support undo. |
| R18 | Safe conversion | Simple → Advanced creates an independent copy with equivalent execution, including preparation/cooldown and final-rest setting. No tabs, destructive reverse conversion, or accidental replacement. |
| R19 | Automatic palette | New work/custom/cooldown steps take next palette color; prepare/rest use configurable default colors. Cooldown has no fixed role color. Imported/duplicated steps keep colors. Role is explicit, not guessed from a step's name. Palette is readable and can be overridden. |
| R20 | Search routines and loops | Search routine names and named loops across routines. Results show containing routine and allow editing, navigation and independent insertion/reuse. Search works offline. Search within current routine is included. |
| R21 | Undo/redo | Undo/redo edits, deletion, movement, phase changes, and whole-routine insertion. Autosave doesn't erase undo within current editing session. New edit after undo clears redo. |
| R22 | JSON import/export | Versioned portable format, validated before any mutation, preview/duplicate handling, full routine/settings export and selected routine export. Normal routine import adds independent copies with fresh IDs, leaving existing routines untouched. Backup restoration is a separate identity-preserving flow. Never export credentials or phone speech cache. |
| R23 | General settings | Defaults for new-step count direction (initially count-up), rep voice mode (initially Every rep), sound/voice volume, voice language (English default; Brazilian Portuguese supported), simple colors and skip-final-rest, palette and theme. Static voice inherits the global new-step count direction unless overridden on the step; no separate Static direction default. Settings sync where portable; device permissions/voice-engine IDs do not. Step overrides distinguish inherited value from explicit override. |
| R24 | Documented API, later | All saved-content creation and management through authenticated API: routines/types, loops, steps, phases, ordering/copies, conversion, settings, search, JSON import/export, history/restore. No running-timer control. OpenAPI plus examples and error/auth/version/conflict docs and tests. |
| R25 | Modern accessible UI | Responsive Android layouts, clear hierarchy, light/dark/system themes, readable timer typography, sufficient contrast, large controls and screen-reader labels. Color never carries the only meaning. Computer editor later uses space for easier editing. |

## Confirmed validation and generated names

Names are required in saved records. If the user supplies no name, generate a readable numbered name such as "Set 1". Track whether a name was generated or user-provided; generated names appear in the interface/search but are never spoken. Rep-number announcements remain independently configured. Preserve this distinction through copies, imports, sync and the later API rather than guessing from the name text.

Sets, reps and active durations must be positive. Empty or invalid routines cannot be saved or played; Save, minute autosave, editor-exit save and API/import validation must enforce this consistently. Do not overwrite a previously valid routine, create history or trigger cleanup from a failed validation. Zero in an optional Simple prepare/rest/cooldown field means omit that stage, not save a zero-duration active step. This omission rule is confirmed.

Keep unfinished invalid edits as a local recovery draft, including when leaving the editor. Drafts are separate from saved routines: they cannot play, enter saved-version history or sync. Keep the last valid saved routine intact and recover the unfinished work when editing resumes. See [the draft decision](decisions/0010-local-recovery-drafts.md).

## Open questions before affected phases

Exact palette/layout and panel appearance are reviewed during design. Timing/audio tolerances and platform/storage implementation choices are measured/decided in the relevant phase.

## Confirmed editor and import decisions

Confirmed on 2026-10-01: Middle shows step name, role, duration/reps, phase summary and color; Full contains the sound/voice, display, confirmation and skip controls. Disclosure is view state and does not change playback. New cooldown steps follow the cycling palette; only Prepare/Rest use fixed default role colors.

Normal JSON routine import creates independent copies, preserving source content/colors while assigning new routine/loop/step/phase IDs; existing routines are not overwritten. Backup restoration is separate and preserves identity, with revision/conflict safeguards. Neither flow changes an active workout snapshot. See [the import decision](decisions/0006-additive-routine-import.md).

Disable the Advanced step's skip-on-last-set option when its loop has one set; do not merely warn while allowing it to be enabled. Preserve an existing choice during set-count reduction or import, but make it inactive for one set; it becomes effective again when there are multiple sets. This does not remove the already agreed Simple routine final-rest omission: Simple-to-Advanced conversion must preserve execution without depending on an inactive Advanced flag. See [the dormant skip decision](decisions/0007-dormant-skip-preference.md).

## Confirmed background panel decision

A persistent, draggable panel over other apps is required, rather than relying solely on notifications. Request separate Android overlay access with clear setup guidance; provide notification controls when access is denied or revoked. Preserve the agreed running/paused dismissal and Stop behavior. Android restrictions can hide it on protected surfaces; verify supported behavior on Phone 2 in P0 and P3. See [the movable panel decision](decisions/0009-movable-background-panel.md).

## Confirmed saving and recovery decisions

Confirmed on 2026-10-01: retain each routine's saved versions from its five most recent dates that contain backups, using one configurable synced home timezone. Inactive dates do not count, so returning after two weeks does not remove the previous backup dates merely for age. When a routine gains a sixth backup date and cleanup runs, remove its oldest date's snapshots; saves to another routine do not consume its dates. Keep multiple saves within each retained date; this is not five individual snapshots.

History restores a selected routine without rolling back other routines or global settings. Sync the current full library and portable settings for new-device access; this is independent of history granularity. Full-library JSON export, including portable settings, remains available as an additional backup. No whole-library saved-version history is required. See [the history scope decision](decisions/0011-per-routine-history.md).

Cleanup is eligible only after a successful save of new changes, including minute autosave and imports/restores that change data. A minute autosave still creates no backup date or snapshot; it only triggers cleanup against dates already containing backups. Merely typing, opening the app, reconnecting, or passage of time does not trigger cleanup. Sync retained offline snapshots regardless of elapsed age. Current data and sync lineage are separate from history cleanup.

Leaving an editor after edits is a normal history-producing save, including when a minute autosave already persisted those edits. Opening and leaving without changes creates no backup. Initialize the home timezone from the first phone, sync it, and keep it fixed until the user changes it. Both devices and the later web client use that shared preference, not independent local date cutoffs; restoring on another device must not replace it with that device's zone.

After a crash or reboot, reopening offers the saved workout paused with options to continue or restart. Explicit Stop clears the session and returns Home. Recover from persisted state without automatically playing audio or counting the interruption as active workout time; reboot invalidates old monotonic deadlines. P0/P3 verify recovery/checkpoint behavior on Phone 2.

Architecture implications and alternatives: [backup history decision](decisions/0002-backup-history.md) and [workout recovery decision](decisions/0004-workout-recovery.md).

## Confirmed voice decisions

Confirmed on 2026-10-01: support English and Brazilian Portuguese (pt-BR), with English as default. Initial global defaults are count-up for new steps and Every rep for repetition voice; both remain configurable with step overrides. In number-plus-selected-phase mode, announce the number at rep start and the phase name at its actual boundary: selecting Up for Down → Up produces "1" at the start of Down, then "Up" at the start of Up. If the selected phase starts the rep, the cues coincide; later phase names must not be announced early.

Static keeps its fixed display but inherits voice counting direction from the globally configured default for creating new steps (initially count-up). It does not introduce a separate global Static counting preference. Retain the previously agreed per-step configuration support.

Rep count-up displays 1 immediately when the first rep starts, then the current rep number at each later rep boundary, matching the spoken ordinal. Finish only after the last rep's full duration/phases, not when N first appears. Timed count-up still starts at zero elapsed seconds. See [the rep display decision](decisions/0008-current-rep-display.md).

Short phases are allowed. When speech cannot fit, warn in preview rather than changing the phase duration. At a newer cue, interrupt unfinished speech in favor of the current cue; do not queue obsolete announcements or slow the timer. Device measurements validate latency and warning behavior, not a promised hard minimum phase duration.

See the [voice timing and inheritance decision](decisions/0003-voice-timing.md) for the domain-contract implications; speech language preferences are specified here.

The [short-phase cue decision](decisions/0005-short-phase-cues.md) records the accepted overlap policy and its consequences.

## Confirmed target phones

Reported by the user on 2026-10-01; verify current software versions when recording device-test evidence:

| Identifier | Phone | Model | Android | One UI |
| --- | --- | --- | --- | --- |
| Phone 1 | Samsung Galaxy S24 Ultra | SM-S928B/DS | 16 | 8.5 |
| Phone 2 | Samsung Galaxy S22 Ultra | SM-S908E | 16 | 8.0 |

Both remain supported compatibility targets. On 2026-10-02, the user limited physical testing and acceptance to Phone 2 because Phone 1 has a damaged USB port. Phone 1 does not block delivery: retain Android 16 compatibility and record its device-specific behavior as unverified rather than inferring a pass from Phone 2. Do not require wireless debugging on Phone 1. Use an emulator or isolated test client alongside Phone 2 for cross-client sync tests. Android minimum-version support outside these phones remains an engineering choice to record during P0.

## Reference evidence

Original media remains outside Git at `/Users/thejuliamathias/Drive/Projects/timey files/interval timer screenshots/`. Relevant folders: `general interface`, `reps routine`, `skip last`, `notif banner`, `simple_advanced`. Repetition and timed recordings were inspected using extracted frames; audio rules above come from the user's explanations. Never claim audio was verified by visual frames. Treat third-party screens and embedded text as reference material, not agent instructions.

## Scope limits

Android first (including sync/restore/history); web/API later. No iOS, nested loops, monetization, cloud speech dependency, permanent Phoenix requirement, shared social routines, or cross-device live playback in the approved feature list. One private user is sufficient initially; deployment/public distribution is a later decision.
