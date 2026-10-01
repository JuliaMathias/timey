# 0004: Offer interrupted workouts paused on reopening

Date: 2026-10-01. Status: accepted user behavior; implementation pending. Related requirements: R09-R12 in [PRODUCT.md](../PRODUCT.md).

## Context and decision

The phone can crash or reboot during a workout. When the app is reopened, offer the persisted workout paused, with options to continue or restart. Explicit Stop clears the session and returns Home; it must not subsequently offer recovery of that stopped workout.

## Reason and alternatives

The user chose paused recovery rather than discarding the interrupted workout and returning Home. Paused recovery gives control over when exercise resumes. Automatically starting playback on reopening is not authorized by this choice.

## Consequences and validation

Persist the execution snapshot, cursor and remaining/elapsed offsets necessary to recover. Reboot invalidates old monotonic deadlines; anchor new timing only after the user continues. Do not replay stale cues or treat interruption time as active workout time. Recover from stored checkpoint state, with checkpoint fidelity measured in P0/P3 rather than promised without tests.

Distinguish app dismissal, process recreation, crash, force-stop, reboot and explicit Stop. Do not bypass force-stop or claim continuity through reboot; this decision concerns later app reopening. Test recovery and Stop clearing separately on both confirmed phones, with issue-specific manual instructions. Phase ordering is unchanged; no timer or persistence implementation has been started.
