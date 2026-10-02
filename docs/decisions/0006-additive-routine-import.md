# 0006: Import routines as independent copies and keep restoration separate

Date: 2026-10-01. Status: accepted user policy; implementation pending. Related requirements: R16-R18, R22, R24 in [PRODUCT.md](../PRODUCT.md).

## Context and decision

JSON can transfer routines between clients and provide backups. Normal routine import adds independent copies, leaving existing routines untouched. Assign new IDs to the imported routine and its loops, steps and phases while preserving content, order and colors. Backup restoration is a separate identity-preserving flow governed by revision/conflict rules. Neither changes an active workout snapshot.

## Reason and alternatives

The user accepted independent copies instead of overwriting existing routines during ordinary import. Separating restoration avoids confusing copying new content with recovering an existing record. Matching imported IDs and updating records automatically would conflict with this choice; stripping identities during restore would lose recovery lineage.

## Consequences and validation

Define separate import-copy and restore operations in Android and later Phoenix/API contexts. Validate versions/content before mutation; remap related IDs consistently and commit accepted copying atomically. Preview explains which routines will be added and how duplicates are handled. Any import of portable global settings is a separate explicit choice, not inferred from adding routine copies.

P1 fixtures test identity remapping and semantic equivalence. P2 verifies existing records remain unchanged, copied colors/order persist, and invalid imports do not partially mutate data. P4 verifies identity/revision behavior on restoration. P6 exposes/document/tests both operations distinctly. Exact storage/API implementations remain proposed; this record does not approve starting them.
