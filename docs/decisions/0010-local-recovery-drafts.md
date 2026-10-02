# 0010: Local recovery drafts separate from saved routines

Date: 2026-10-01
Status: Accepted behavior; implementation pending
Requirements: R01, R13, R22, R24

## Context and decision

The user requires empty or invalid routines to be rejected on save, but also chose to retain unfinished invalid edits as local recovery drafts. Treating a draft as a saved routine would contradict validation; discarding it on exit would lose the recovery behavior requested.

Persist editor recovery state separately from valid saved routines. Keep it device-local, recoverable when editing resumes and excluded from playback, history, sync and retention cleanup. Preserve the last valid routine. Only a successful validated routine save can publish the edited content into the saved library.

## Alternatives and consequences

Rejecting the invalid routine without preserving unfinished work would lose edits. Saving invalid content into the ordinary library would weaken the agreed save contract and expose invalid records to other clients. A separate draft allows recovery while keeping that contract intact.

P1 defines the storage boundary; P2 implements editor recovery. Test invalid exit/reopen and process recreation, preservation of the valid routine, exclusion from sync/history/cleanup and validation before promotion. Optional Simple preparation/rest/cooldown durations of zero omit those stages; active stages still require positive durations.

Drafts are not cloud backups and will not be available on another device. The full delivery plan remains awaiting approval.
