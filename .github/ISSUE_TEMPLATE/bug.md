---
name: Bug report
about: Reproduce and verify a concrete fault
title: ''
labels: bug
assignees: ''
---

## Expected and actual behavior

## Reproduction

Steps, routine example without private data, Android version/device, app version, and online/offline state.

## Evidence

Logs/screenshots with account credentials and personal content removed.

## Project status and relationships

- Add this issue to the Timey project and verify its existing scope/type labels.
- Initial Status: Ready or Backlog, with the prerequisite/scope/setup reason.
- Parent issue: set the native parent/sub-issue link, or explain why no parent applies.
- Blocked by / blocks: set native dependency links and verify both ends, or explain why none apply. Ordinary related references are context, not invented dependencies.
- Set In progress at start; Review only when user review/approval is the sole remaining gate; Done when closed. Pending CI or manual/device evidence remains In progress.

## Acceptance

- [ ] Regression test reproduces the fault and passes after repair.
- [ ] Relevant real-device behavior verified.
- [ ] Complete manual regression-test instructions supplied when needed and results recorded; otherwise explain why they are unnecessary.
- [ ] Related requirements/docs updated if necessary.
- [ ] Entire remaining plan and outstanding dependencies reviewed in light of the fix; adjustments or "No adjustments needed" recorded.

## Manual regression-test instructions

If unnecessary, state "Not required" with a reason. Otherwise provide a complete verification procedure, including any affected behavior beyond the original reproduction:

- Purpose/acceptance criterion:
- Prerequisites: device/emulator, fixed build/commit and installation steps, permissions/voice/account/accessories, network and initial app state.
- Exact disposable test data/settings and starting screen/playback state:
- Numbered actions with expected results after each action, including exact waits:
- Required variants and pass/fail criteria, with agreed timing tolerance/measurement method where relevant:
- Evidence capture and actual results per case: expected/actual, device/build and pass/fail/blocked/not run.
- Cleanup/recovery and data-preservation instructions if setup affects existing content:
