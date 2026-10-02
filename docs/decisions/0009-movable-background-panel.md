# 0009: Required movable background panel

Date: 2026-10-01
Status: Accepted requirement; implementation pending
Requirements: R10, R12, R25

## Context and decision

The user requires a persistent panel over other apps and wants to move it by dragging, improving on the reference app. Notification-only controls do not meet this requirement. Screenshots do not identify the reference implementation.

Implement a draggable Android overlay alongside the foreground service notification. Request draw-over-other-apps access separately; use notification controls if access is denied or revoked. The panel displays the service's session state and sends actions to the same timer. Dragging only changes its position.

## Consequences and validation

P0 must prototype overlay permission, drag behavior and notification fallback on both confirmed Samsung phones; P3 completes the feature. Android can hide overlays on protected surfaces, so this requirement does not promise visibility over every system screen.

Keep position device-local and recover usable bounds after display changes. Preserve running/paused dismissal semantics and remove the panel on Stop/finish. Verify that dragging, dismissal and permission changes do not alter the workout clock or duplicate cues. Test cleanup on stop and lifecycle failures, alongside ordinary notification actions.

The panel's visual design remains for design review. Specific adapter/lifecycle implementation is validated during P0 rather than inferred from the reference app. This accepts the requirement, not the full delivery plan or a claim that device tests have passed.

## Validation scope update — 2026-10-02

The user subsequently limited required physical testing to Phone 2 (Galaxy S22 Ultra) because Phone 1 has a damaged USB port. Earlier two-phone validation statements above are superseded by the current device policy in [PRODUCT.md](../PRODUCT.md) and [TESTING.md](../TESTING.md). The architectural decision and Phone 1 compatibility requirement remain unchanged; no Phone 1 hardware result is inferred.
