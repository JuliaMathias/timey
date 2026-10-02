# 0007: Retain skip-last preferences while inactive in single-set loops

Date: 2026-10-01. Status: accepted user semantics; implementation pending. Related requirements: R03, R05, R18, R22, R24 in [PRODUCT.md](../PRODUCT.md).

## Context and decision

The skip-on-last-set control is disabled when an Advanced loop has one set. If a previously enabled choice exists, preserve it but make it ineffective at one set. It becomes effective again when the loop has multiple sets. Apply the same policy to imported single-set loops.

## Reason and alternatives

The user chose remembering the preference over clearing it on set-count reduction. Preserving storage state avoids losing configuration while disabling its effect prevents the only occurrence of a step from being skipped.

## Consequences and validation

Separate stored preference from effective omission. Omit only when the preference is enabled, the loop has more than one set and the current set is final. Keep the flag intact through save/export/import and set-count changes; disable its editor control with an explanation at one set. Kotlin and later Elixir fixtures must agree.

Simple final-rest omission remains a separate rule. Conversion of a one-set Simple routine must remove/omit the final rest structurally rather than rely on an inactive Advanced flag. P1/P2 tests cover 3 → 1 → 3 sets, imported dormant flags, UI disabled state, execution and conversion equivalence.
