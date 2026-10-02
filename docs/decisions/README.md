# Decision records

Architecture decision records (ADRs) preserve why a consequential design choice was made. They are short records of context, a decision, reasons/alternatives, consequences and status. They complement the living specification; they are not a second backlog or a separate copy of every requirement. This practice follows the ideas in [Michael Nygard's original ADR article](https://www.cognitect.com/blog/2011/11/15/documenting-architecture-decisions).

## Where each kind of information belongs

- [PRODUCT.md](../PRODUCT.md): current user-facing requirements, confirmed preferences, proposed defaults and open questions.
- [ARCHITECTURE.md](../ARCHITECTURE.md): current implementation boundaries and technical proposals.
- [PLAN.md](../PLAN.md): work sequence, dependencies and acceptance gates.
- Numbered ADRs: context and rationale for choices affecting repository/build boundaries, shared contracts, persistence/sync, runtime ownership or a substantial technical tradeoff. A user-facing choice can need an ADR when it has those consequences.

English as default, a selected palette color or a phone model normally stays in the product specification. History retention by backup dates rather than elapsed time needs a record because it changes data modeling, cleanup and multi-device reconciliation. Avoid creating one ADR for every preference or file edit.

## Lifecycle and authority

Use sequential filenames `NNNN-short-name.md`. Include date, status, decision/context, reason or user intent, alternatives, consequences/validation and remaining questions where relevant. Do not invent rationale that the user did not provide; distinguish observed requirements from engineering interpretation.

Proposed means undecided. Accepted means the particular decision was approved, not that the whole plan or its implementation is approved/completed. Superseded links a replacement; rejected preserves a considered alternative where useful. When changing an accepted substantive decision, add a successor and mark/link the older record instead of replacing its reasoning. Fix typos or add implementation evidence in place when the decision itself is unchanged.

Keep the index and links current. Specifications describe current behavior; records explain its evolution. If documents conflict, check explicit user instructions and resolve the inconsistency before implementation. Existing Git commits preserve changes made before an ADR was introduced.

## Index

| Record | Status | Scope |
| --- | --- | --- |
| [0001: Repository layout](0001-repository-layout.md) | Accepted | One repository, independent Android/Phoenix builds and releases. |
| [0002: Backup history](0002-backup-history.md) | Accepted policy; implementation pending | Five backup dates, save kinds, shared timezone and cleanup eligibility. |
| [0003: Voice timing](0003-voice-timing.md) | Accepted semantics; implementation pending | Rep-start numbers, phase-boundary names and Static count-direction inheritance. |
| [0004: Workout recovery](0004-workout-recovery.md) | Accepted behavior; implementation pending | Paused recovery after crash/reboot and explicit Stop clearing the session. |
| [0005: Short-phase cues](0005-short-phase-cues.md) | Accepted policy; implementation pending | Warn about speech fit, preempt unfinished speech for newer cues, keep timer pace. |
| [0006: Routine import](0006-additive-routine-import.md) | Accepted policy; implementation pending | Independent routine copies with fresh IDs; separate identity-preserving restore. |
| [0007: Dormant skip preference](0007-dormant-skip-preference.md) | Accepted semantics; implementation pending | Keep stored skip-last choice inactive at one set and reactivate at multiple sets. |
| [0008: Current rep display](0008-current-rep-display.md) | Accepted semantics; implementation pending | Rep count-up shows 1 immediately; completion still waits for the final rep duration. |
| [0009: Movable background panel](0009-movable-background-panel.md) | Accepted requirement; implementation pending | Required draggable overlay with notification fallback and one service-owned timer. |
| [0010: Local recovery drafts](0010-local-recovery-drafts.md) | Accepted behavior; implementation pending | Recover unfinished invalid edits locally without saving invalid routines or syncing drafts. |
| [0011: Per-routine history](0011-per-routine-history.md) | Accepted scope; implementation pending | Independent routine history budgets, full current-library/settings sync and full-library JSON export. |
