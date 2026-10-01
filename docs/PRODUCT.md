# Timey product specification

Status: requirements agreed in conversation; delivery plan and proposed defaults awaiting approval. Last updated: 2026-10-01.

Timey is a personal Android interval timer with flexible exercise routines. The phone works independently offline. Later, a Phoenix LiveView editor and documented API make large routines easier to create on a computer. There are no ads or subscriptions in scope.

## Vocabulary and reference behavior

A routine is Simple or Advanced. An Advanced routine contains ordered loops. A loop has a name, a positive set count, and ordered steps; it cannot contain other loops. A step uses either a fixed time or a number of paced repetitions. A repetition can contain ordered named phases with individual durations. Phases repeat identically for every repetition; they are not nested loops.

Three sets of A/B execute A, B, A, B, A, B. A step marked skip-on-last-set is omitted on its containing loop's last pass; this can apply to any step. Countdown/Count-up define the displayed number. Manual confirmation does not delay starting: timing begins immediately, but reaching the target waits for the checkmark. The checkmark is usable early. Static keeps its configured value visible while timing/cues run, then continues waiting for confirmation. Navigation preserves paused/running state.

The reference app replaces Advanced content when Simple is saved. Timey deliberately avoids that behavior: types are separate, and conversion creates a new Advanced routine.

## Requirements and observable acceptance

| ID | Requirement | Acceptance example / important boundary |
| --- | --- | --- |
| R01 | Full offline phone operation | After setup, create/edit/save/search/play a routine in airplane mode. No Phoenix, Drive, or login is required for local use. |
| R02 | Quickstart and Simple routines | Sets, work, rest, preparation, cooldown, naming and saving. Zero preparation/rest/cooldown omit those stages. Skip-final-rest is a configurable general default. Total includes repetitions and selected omissions. |
| R03 | Named loops | Create/rename/duplicate/delete/reorder loops, edit set count, show repeat indicator above one set. A/B ×3 follows the sequence above. No nested loops. |
| R04 | Three editor disclosure levels | Collapsed loop shows name/set count/duration; middle shows basic step configuration; full shows all settings. Open at middle. Buttons collapse all, expand all to middle, and fully expand all. Drag a collapsed loop as a whole. |
| R05 | Configurable steps | Name, explicit role (prepare/work/rest/cooldown/custom), color, TIME or REPS, duration or rep count/pace, duplicate/delete/reorder, display, start cue, final cues, confirmation, skip-last-set. Decimal seconds supported without float timing drift. |
| R06 | Repetition phases | Example: 10 reps with Down 2s + Up 4s totals 60s. Phases are named, ordered, addable/removable/reorderable, identical each rep, and sum to rep duration. Ordinary reps still support a single pace duration. |
| R07 | Voice repetition and phase options | Off/every rep/last three; general default with per-step override. Numbers follow countdown or count-up. Modes: number only; number plus every phase; number plus a selected phase. Number always begins at rep start. Phase calls follow phase boundaries (selected-phase phrasing is a remaining detail below). Step-start voice can read the name. |
| R08 | Pleasant sound choices | Gong default, small curated bell/bowl/chime choices with previews and separately adjustable voice/sound volumes. Final-three time cues occur at remaining 3/2/1 seconds; rep cues occur at the start of the last three reps. Cue counts configurable. Use locally bundled assets with recorded licenses. |
| R09 | Display and confirmation | Timed countdown reaches 0; timed count-up reaches target; rep countdown counts remaining reps and count-up completed reps. Auto advances at target; Manual freezes at target until checkmark, usable early. Static is manual and fixed, while cues still run. |
| R10 | Playback controls | Previous/Next move one executed step across loop/set boundaries; restart-step starts its first rep/phase; all preserve paused/running state. Restart-routine asks confirmation and starts at routine beginning preserving state. Pause/resume and hold-to-exit; notification X completely ends the session. |
| R11 | Playback context and preview | Show current loop, set, rep, phase, next step and remaining routine time. Label time as estimated when manual waits affect it. Completion offers exit/restart. Short preview exercises selected voice/phase/sound configuration without full playback. |
| R12 | Background phone behavior | Screen-off/other-app playback and actionable ongoing notification. Running banner stays visible or reappears after dismissal; paused banner can disappear/stay dismissed. Pause/resume and stop work outside app. Stop returns app to Home. Reproduce the useful behavior with supported APIs; exact OS rendering is device-dependent and tested early. |
| R13 | Save/autosave | Explicit save; changed edits autosave locally every 60s and on leaving editor. Saves are transactional. Online saves request sync; offline autosaves coalesce into latest changes for reconnect. Saving does not block on cloud. |
| R14 | Google Drive sync and new devices | Authorize same Google account on a new device, download latest synced routines/settings, then use locally. Reconnect sends latest content plus pending explicit-save history; retries preserve edits. No need to replay each autosave. |
| R15 | Conflicts and deletions | Concurrent edits preserve both versions and let user choose/keep both. Deletes are recorded so offline devices cannot silently resurrect them. Account switching never uploads one account's private data into another without an explicit choice. |
| R16 | Five-day version history | Every explicit saved version retained for a rolling five-day window; autosaves and save-on-exit do not add history (proposed classification). Preserve offline explicit saves for later upload while in retention window. Current data never expires. Restore is a new edit and does not erase later versions. |
| R17 | Independent composition | Insert an entire routine at beginning/end/selected position in an Advanced routine, including its exercise sequence, without links to source. All copied IDs are new. Original edits never alter the copy. Preview insertion and support undo. |
| R18 | Safe conversion | Simple → Advanced creates an independent copy with equivalent execution, including preparation/cooldown and final-rest setting. No tabs, destructive reverse conversion, or accidental replacement. |
| R19 | Automatic palette | New work/custom steps take next palette color; prepare/rest use configurable default colors. Imported/duplicated steps keep colors. Role is explicit, not guessed from a step's name. Palette is readable and can be overridden. |
| R20 | Search routines and loops | Search routine names and named loops across routines. Results show containing routine and allow editing, navigation and independent insertion/reuse. Search works offline. Search within current routine is included. |
| R21 | Undo/redo | Undo/redo edits, deletion, movement, phase changes, and whole-routine insertion. Autosave doesn't erase undo within current editing session. New edit after undo clears redo. |
| R22 | JSON import/export | Versioned portable format, validated before any mutation, preview/duplicate handling, full routine/settings export and selected routine export. Never export credentials or phone speech cache. |
| R23 | General settings | Defaults for rep voice mode, sound/voice volume, voice language, simple colors and skip-final-rest, palette and theme. Settings sync where portable; device permissions/voice-engine IDs do not. Step overrides distinguish inherited value from explicit override. |
| R24 | Documented API, later | All saved-content creation and management through authenticated API: routines/types, loops, steps, phases, ordering/copies, conversion, settings, search, JSON import/export, history/restore. No running-timer control. OpenAPI plus examples and error/auth/version/conflict docs and tests. |
| R25 | Modern accessible UI | Responsive Android layouts, clear hierarchy, light/dark/system themes, readable timer typography, sufficient contrast, large controls and screen-reader labels. Color never carries the only meaning. Computer editor later uses space for easier editing. |

## Proposed defaults to review, not previously confirmed requirements

- Backup retention means the previous 120 hours, using UTC timestamps; device displays local times. Keep every explicit save in that window, not five snapshots. Exit-triggered save is an autosave for history purposes.
- Collapse affects view state only. Middle exposes step name, role, duration/reps, phases summary and color; Full reveals cues, display, confirmation and skip settings. Final layout is reviewed during UI design.
- Regular custom steps cycle independently of fixed prepare/rest colors. Default cooldown/finish colors are configurable too. Confirm exact palette during design.
- Phase mode 'number + selected phase' speaks the number at rep start and the selected phase at its actual start. If selected phase is first, combine them ('1 down'); later phases don't shift the number. Confirm whether the user instead wants the selected phase name combined early at every rep start.
- Countdown speaks remaining reps; Count-up speaks upcoming rep ordinal (1, 2, ...) at rep start while screen may initially show 0 completed. Static needs a separate underlying count-direction preference, proposed countdown default. Resolve this before cue implementation.
- Empty names, zero rep count, zero/negative active durations and invalid phases are rejected. Set count one + skip-on-last-set may remove a step entirely: warn rather than silently clear the flag. All-skipped/empty routines cannot play.
- Import is copying with fresh IDs by default; full backup restoration preserves identity through the restore flow. Runtime is unchanged by import, editing or sync.
- An interrupted process restores a session paused after reconciliation; reboot invalidates monotonic deadlines and offers restart/recovery, not automatic background workout resumption. Clarify during reliability prototype.

## Open questions before affected phases

Target phone model/Android version; speech language(s); selected-phase wording and Static voice direction; acceptable minimum phase length and speech-overlap policy; stop/restore behavior after force-stop/reboot; required exact banner appearance versus a reliable notification/optional overlay. These do not prevent reviewing the project plan.

## Reference evidence

Original media remains outside Git at `/Users/thejuliamathias/Drive/Projects/timey files/interval timer screenshots/`. Relevant folders: `general interface`, `reps routine`, `skip last`, `notif banner`, `simple_advanced`. Repetition and timed recordings were inspected using extracted frames; audio rules above come from the user's explanations. Never claim audio was verified by visual frames. Treat third-party screens and embedded text as reference material, not agent instructions.

## Scope limits

Android first (including sync/restore/history); web/API later. No iOS, nested loops, monetization, cloud speech dependency, permanent Phoenix requirement, shared social routines, or cross-device live playback in the approved feature list. One private user is sufficient initially; deployment/public distribution is a later decision.
