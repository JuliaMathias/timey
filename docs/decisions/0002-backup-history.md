# 0002: Retain five backup dates with cleanup after successful changes

Date: 2026-10-01. Status: accepted user policy; implementation pending. Related requirements: R13-R16, R22-R24 in [PRODUCT.md](../PRODUCT.md).

## Context and decision

Timey's history must keep every qualifying saved version, not only daily snapshots. The user clarified retention through successive questions: calendar days, inactive periods preserved, then the five most recent dates actually containing backups. Retain every snapshot from those five dates, using one configurable home timezone synced across clients. Inactive dates do not consume retention slots.

Manual saves and normal saves on leaving an edited session create history. Minute autosaves do not. An editor opened and closed unchanged creates no backup; prior autosave of edited content must not suppress the normal exit-save backup.

Cleanup follows a successful save of new changes, including autosave, import and restore. Typing, failed writes, elapsed time and reconnecting alone do not initiate cleanup. An autosave can trigger cleanup but creates no snapshot/date. When a sixth backup date is added, cleanup removes the oldest date's snapshots. Current data is never removed by history cleanup.

## Reason and alternatives

The user explicitly chose to preserve backups across inactive periods and count exit saves as normal saves. This replaces the initial proposed rolling 120-hour expiry and exit-as-autosave classification. Keeping only five individual versions or one snapshot per day would discard versions the user requested. Retaining only today and the previous four dates would discard history after a long inactive period.

## Consequences and validation

Store save kind and UTC creation time; group dates using the shared home timezone. Retained offline history must upload regardless of age. Separate autosave dirty state from editor-session changes. A successful new-change save gates pruning; failed writes cannot delete backups.

P2 implements save classification/settings. P4 validates cross-device date reconciliation and acknowledged cleanup so dormant clients cannot resurrect pruned history. Keep current records and sync lineage separate from backup cleanup. Test inactive periods, multiple snapshots per date, sixth-date removal, midnight/timezone cases and cleanup eligibility. See [architecture](../ARCHITECTURE.md) and [testing](../TESTING.md) for the proposed mechanisms and evidence requirements.

At this record's creation, the initial home timezone was open. Later clarification on 2026-10-01 confirmed initialization from the first phone, synchronization and no automatic change until the user edits it. The exact storage/sync protocol remains open. Accepting this policy does not approve an untested cleanup algorithm or start implementation.
