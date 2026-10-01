# Project management and agent workflow

Status: proposed management system; repository layout approved on 2026-10-01. No remote repository, issues, or board has been created.

## Recommended system

Use one private GitHub repository, GitHub Issues for executable tasks, milestones for phases, and one optional GitHub Project board. GitHub is not technically necessary: local Markdown and Git are enough to start. It is useful here because human decisions, issue dependencies, code review, and automated checks can live together. We do not need Linear, Jira, Notion, or an agent orchestration service for this scope.

Docs describe enduring requirements and decisions. Issues describe work and status. The board displays those issues; it is not a second backlog. Chats are discussion, not the source of truth for accepted behavior. Do not maintain a competing status table in several places.

Suggested board states: Backlog, Ready, In progress, Review, Done. Labels: android, web, contracts, sync, audio, design, bug, docs, learning. Use milestone P0-P7 from `PLAN.md`. Add blocked-by links and requirement IDs. One owner per issue. Begin with one implementation task at a time; add concurrency only for independent work after the contracts stabilize.

## After plan approval

1. Confirm GitHub owner and repository name/visibility (recommend private `timey`), then create/push the repository under explicit user authorization. Do not guess the account or expose reference media.
2. Create milestone issues from the task groups in `PLAN.md`, split into small tasks with observable acceptance criteria and meaningful tests. Preserve requirement IDs, dependencies, scope, and learning objectives.
3. Create the Project board if the user wants it. Add existing issues rather than copying their descriptions into new cards. Record issue URLs back in the plan.
4. Start P0 only after plan approval. Later phases remain visible but do not block the first runnable phone prototype.
5. As part of P0, add GitHub Actions tests and protect `main` so PRs cannot merge until the required checks pass. Verify enforcement with a deliberately failing test PR followed by a passing revision. Record the actual required check names and settings; do not merely add a workflow file and assume it blocks merges.

The GitHub CLI is already available on this Mac. Authentication has not been checked; remote management requires the user's account authorization. Templates in `.github/` standardize tasks, bugs, and review without installing anything.

## Task lifecycle

Before coding, read relevant instructions and requirement IDs, inspect current state, and identify a concrete slice. A multi-session task gets a plan based on `docs/plans/TEMPLATE.md`. Keep progress, evidence, and decisions current so another agent can resume without the original chat.

Implement and test a coherent increment, update its documentation, inspect the diff, and commit with a title and body. Useful increments include a model plus validation tests, persistence plus migration tests, and a screen plus interaction tests. Do not hold all work until a large feature is complete. Do not create knowingly broken intermediate commits.

When manual validation is needed, write complete instructions in the issue before requesting the user's test: prerequisites/build installation, exact fixtures/settings, numbered actions and expected results, variants/pass-fail criteria, evidence and cleanup. Update them to match the implemented UI. Use the procedure requirements in `TESTING.md`; "test on your phone" is insufficient. Record actual results and outstanding cases before closing the issue. If none are needed, state why.

When a remote exists, use a feature branch per issue and a small PR. Link the issue and include actual validation. Commits are local by default; commit authorization is not automatic permission to publish or merge. A review checks the behavior against the issue, not just style.

Tests must run and pass in GitHub Actions before merging each PR. Require PRs and an up-to-date branch, require the stable CI gate from GitHub Actions, and apply protections to administrators too, without a routine bypass. Failed, cancelled, missing, or pending tests block merge. Local checks remain useful but do not replace this requirement. See `TESTING.md` for suite coverage and enforcement details.

GitHub's documented branch protection availability depends on account plan and visibility: public repositories support it on Free, while private repositories require a supporting paid plan such as Pro. Verify the user's account capability before choosing final visibility; keep the CI merge requirement and explain any unresolved enforcement limitation. Do not change privacy or purchase a plan without the user's choice.

Done means acceptance criteria met, tests and required CI passed, material limitations explicitly accepted where applicable, device evidence collected where required, documentation updated, work integrated, and the forward-plan review recorded. A generated screen or a passing compile alone is insufficient.

As each issue is completed, reread all remaining phases in `PLAN.md` and outstanding issue dependencies before closing it or beginning the next issue. Use the implementation, test results and device discoveries to assess future architecture, sequencing, scope, risks, validation and acceptance gates. Do not limit the review to the next task or the current milestone.

Adjust affected plan sections, technical docs and future issue descriptions/dependencies when the evidence warrants it. Record the completed issue, discoveries, adjustments and reasons in its completion note and execution plan; if none are needed, explicitly record "No adjustments needed" and why. Keep issue status in GitHub once migrated rather than duplicating the backlog in Markdown. Routine technical refinements can proceed within approved scope; changes to agreed product behavior or user decisions must be presented for approval. Any resulting code/documentation changes follow the normal commit and PR/CI rules.

## Optional agent collaboration

No agents or extra tools are required for planning. Default to one integrating agent. When the user authorizes parallel agents, useful independent assignments are engine/model work, editor/UI work, and bounded device/review research after interfaces are agreed. Each receives an issue, exact file ownership, base revision, dependencies, tests, and delivery format. Shared schema changes have one owner. Use independent worktrees to avoid competing indexes/commits.

An agent handoff records: goal and requirement IDs; base/current commit; changed paths; decisions; checks and results; reproduction/demo steps; remaining work and blockers. A reviewer does not rewrite the owner's files silently. Never spawn agents solely to fill available slots.

## Commit format

Title: imperative description of one logical increment, optionally prefixed with `android:`, `docs:`, or `web:`.

Body: explain what changed and why, then list relevant validation and limitations. Link issue/requirement IDs when available. Conventional Commits is optional; clear titles and complete bodies are mandatory.

Example:

    android: Add phase-aware repetition progression

    Model each repetition as ordered named phases with integer durations.
    Advance against the monotonic clock and cancel stale cue events on skip.

    Validation: fake-clock tests cover phase boundaries, pause, and early next.
    Device audio validation remains part of the separate cue integration task.
