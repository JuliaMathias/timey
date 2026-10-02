# 0008: Rep count-up displays the current repetition from one

Date: 2026-10-01. Status: accepted user semantics; implementation pending. Related requirements: R06-R07, R09, R11 in [PRODUCT.md](../PRODUCT.md).

## Context and decision

For repetitions in count-up mode, show 1 immediately when the first rep starts, then the current rep ordinal at each new rep. This aligns with the spoken rep number. Timed count-up still displays elapsed time starting at zero; Static retains its fixed target display.

## Reason and alternatives

The user selected showing the current rep rather than completed reps (0 while the first rep is underway). The distinction changes the display projection, not the exercise's duration.

## Consequences and validation

Do not derive completion from the displayed number reaching the configured count: N appears when the final rep starts. The engine finishes only after that rep's full duration/phases unless the user confirms early or navigates. Manual completion clamps/waits according to the existing contract. P1/P3 tests cover first/last rep starts, phase boundaries, delayed ticks, final completion and matching voice/display values.
