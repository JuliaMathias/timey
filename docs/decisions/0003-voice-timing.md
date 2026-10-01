# 0003: Rep-start numbers, phase-boundary names and inherited Static direction

Date: 2026-10-01. Status: accepted user semantics; implementation pending. Related requirements: R07, R09, R23 in [PRODUCT.md](../PRODUCT.md).

## Context and decision

A repetition can contain phases with different durations. Voice must announce the rep number at rep start. In number-plus-selected-phase mode, speak the phase name when that phase actually begins. For Down → Up with Up selected, say "1" at Down's start and "Up" at Up's start. Do not move the number later or announce a later phase early.

Static display remains fixed while the timer/cues run. Its voice count direction inherits the global default for creating new steps (countdown/count-up), respecting step overrides. Do not introduce a separate global Static count-direction preference. Resolve inherited settings into the immutable playback snapshot.

## Reason and alternatives

The user chose actual phase-boundary announcements over saying "1 Up" together at the beginning of Down or making both timings configurable. They also specified the existing global new-step direction as Static's default instead of a separate Static countdown default. These choices affect cue events and portable settings semantics, beyond the screen's presentation.

## Consequences and validation

Represent display style separately from effective counting direction. Schedule number and phase-name events independently at their boundaries. Timer progression is independent of speech completion; preserve the previously agreed cue cancellation on pause/navigation/stop. Tests cover a selected later phase and Static inheritance/overrides on both counting directions.

At this record's creation, Portuguese locale, initial global direction/voice frequency and short-phase speech-overlap policy were unresolved. Later clarification on 2026-10-01 confirmed Brazilian Portuguese, count-up and Every rep defaults in [PRODUCT.md](../PRODUCT.md); these preferences do not require individual ADRs. The separate [short-phase cue decision](0005-short-phase-cues.md) resolves overlap without changing this record's phase-boundary or inheritance semantics. P0 measures offline voice behavior; P3 implements and verifies these semantics.
