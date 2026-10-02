# 0011: Per-routine history with full current-library sync

Date: 2026-10-01
Status: Accepted scope; implementation pending
Requirements: R14, R16, R22, R24

## Context and decision

The user accepted per-routine saved-version history, current full-library/settings sync for new-device access, and full-library JSON export as an additional backup. This resolves previously unspecified snapshot granularity; the retention policy in [ADR 0002](0002-backup-history.md) still applies.

Retain each routine's qualifying saved versions from its own five most recent backup dates. Frequent saves to one routine do not consume another routine's date budget. Restoring a routine writes a new revision of that routine, preserving other routines and global settings. Use the shared home timezone and existing save-kind/cleanup rules.

Sync current routines and portable settings independently of history snapshots. Full-library JSON export includes current routines and portable settings, excluding credentials, device caches and local drafts. No whole-library saved-version history is required.

## Rationale and alternatives

The whole-library proposal matched the original database-backup wording and would enable restoring a combined library/settings state. Per-routine history better matches focused editing and restoring one routine without rolling back unrelated work. The user chose that approach; current-state sync still supports new-device recovery.

## Consequences and validation

P1 contracts identify the routine associated with a history entry. P4 groups retention and cleanup acknowledgements by routine identity, reconciles offline copies safely and exposes routine-specific restore. P2/P5 verify full-library export. Later API/web behavior uses the same scope.

Test independent date budgets, restoring A without changing B/settings, the existing offline-retention rules and full current-library recovery on another device. Preserve deletion/conflict safeguards; restoring history must never silently resurrect a deleted routine through ordinary sync. Exact storage and conflict implementation remains subject to validation. This decision does not approve the full delivery plan.
