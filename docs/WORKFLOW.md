# Project management and agent workflow

Status: GitHub Issues, phase milestones and a Project board selected on 2026-10-01; repository layout approved. Delivery plan approved on 2026-10-01; public repository [JuliaMathias/timey](https://github.com/JuliaMathias/timey) created on 2026-10-02. Eight milestones and twelve issues are created; the [Timey Project board](https://github.com/users/JuliaMathias/projects/7/views/3) is created and populated.

## Selected system

Use the public JuliaMathias/timey repository, GitHub Issues for executable tasks, milestones for phases, and one GitHub Project board. The user explicitly selected public visibility on 2026-10-02. GitHub is not technically necessary: local Markdown and Git are enough to start. It is useful here because human decisions, issue dependencies, code review, and automated checks can live together. We do not need Linear, Jira, Notion, or an agent orchestration service for this scope.

Docs describe enduring requirements and decisions. Issues describe work and status. The board displays those issues; it is not a second backlog. Chats are discussion, not the source of truth for accepted behavior. Do not maintain a competing status table in several places.

`PRODUCT.md` is the current behavior specification; `ARCHITECTURE.md` describes the current technical design; `PLAN.md` sequences delivery. Numbered records in `docs/decisions/` explain why durable architecture-shaping choices were made, which alternatives were considered and their consequences. Use its index/policy to decide when a choice needs a record. Language, color and other ordinary product preferences do not each need an ADR; choices affecting contracts, persistence, synchronization or runtime ownership do. Add/update the record and index alongside the relevant specification change. Mark replaced records superseded and link successors to retain decision history.

Suggested board states: Backlog, Ready, In progress, Review, Done. Labels: android, web, contracts, sync, audio, design, bug, docs, learning. Use milestone P0-P7 from `PLAN.md`. Add blocked-by links and requirement IDs. One owner per issue. Begin with one implementation task at a time; add concurrency only for independent work after the contracts stabilize.

## After plan approval

1. Repository creation is complete: [JuliaMathias/timey](https://github.com/JuliaMathias/timey), public, with documentation history pushed. Keep personal reference media and credentials outside Git.
2. Create milestone issues from the task groups in `PLAN.md`, split into small tasks with observable acceptance criteria and meaningful tests. Preserve requirement IDs, dependencies, scope, and learning objectives.
3. Board creation is complete: [Timey](https://github.com/users/JuliaMathias/projects/7/views/3), with all twelve existing issues and Backlog/Ready/In progress/Review/Done states. #9 is In progress; #10 is Done and remaining dependent tasks are Backlog. Keep the default private project visibility unless the user chooses otherwise; the repository and issues are public. Add existing issues rather than duplicating the backlog.
4. Start P0 only after plan approval. Later phases remain visible but do not block the first runnable phone prototype.
5. As part of P0, add GitHub Actions tests and protect `main` so PRs cannot merge until the required checks pass. Verify enforcement with a deliberately failing test PR followed by a passing revision. Record the actual required check names and settings; do not merely add a workflow file and assume it blocks merges.

The GitHub CLI is already available on this Mac. Authenticated API access as JuliaMathias was verified on 2026-10-02. The user completed additional Projects authorization on 2026-10-02; project creation/linking, Status options, board columns and item placement were verified through GitHub APIs. Templates in `.github/` standardize tasks, bugs, and review without installing anything.

## Task lifecycle

**Human approval and merging are mandatory.** Agents may create branches, commit/push changes, open/update PRs, resolve conflicts, run checks and report readiness. They must never merge a PR, enable auto-merge, schedule a merge, or invoke a merge queue on the user’s behalf. Leave every PR open for the user to review, approve and merge manually in GitHub. Passing CI, plan approval or permission to implement/publish does not authorize an agent to merge.

Before coding, read relevant instructions and requirement IDs, inspect current state, and identify a concrete slice. A multi-session task gets a plan based on `docs/plans/TEMPLATE.md`. Keep progress, evidence, and decisions current so another agent can resume without the original chat.

Implement and test a coherent increment, update its documentation, inspect the diff, and commit with a title and body. Useful increments include a model plus validation tests, persistence plus migration tests, and a screen plus interaction tests. Do not hold all work until a large feature is complete. Do not create knowingly broken intermediate commits.

When manual validation is needed, write complete instructions in the issue before requesting the user's test: prerequisites/build installation, exact fixtures/settings, numbered actions and expected results, variants/pass-fail criteria, evidence and cleanup. Update them to match the implemented UI. Use the procedure requirements in `TESTING.md`; "test on your phone" is insufficient. Record actual results and outstanding cases before closing the issue. If none are needed, state why.

All repository changes must use a branch dedicated to a GitHub issue and a PR targeting `main`. This includes code, documentation, bug fixes and maintenance. Create an issue first if the work has no existing issue. Start from current `main` and name the branch `codex/issue-<number>-<short-description>` (for example, `codex/issue-11-phased-reps`). Do not implement directly on `main`, reuse a merged branch for new work, or combine unrelated issues in one branch.

Keep making small logical commits with complete titles and bodies on that branch. Open a small linked PR, using `Closes #<number>` only when it completes the issue; otherwise use `Refs #<number>` and describe the remaining work. Integrate through the PR after required latest-revision CI passes. Link the issue and include actual validation. Commits are local by default; commit authorization is not automatic permission to publish or merge. A review checks the behavior against the issue, not just style.

Every PR working on an issue must have a real GitHub Development association with that issue, verified in the sidebar; a `Refs #<number>` text reference alone is insufficient. Link it using GitHub’s Development selector, and retain the issue reference and scope in the PR description. Timey disables automatic closure of merged linked issues so partial PRs stay associated without prematurely completing an issue. Close an issue explicitly only after its acceptance criteria, required CI, manual evidence where needed and forward-plan review are complete.

Tests must run and pass in GitHub Actions before merging each PR. Require PRs and an up-to-date branch, require the stable CI gate from GitHub Actions, and apply protections to administrators too, without a routine bypass. Failed, cancelled, missing, or pending tests block merge. Local checks remain useful but do not replace this requirement. See `TESTING.md` for suite coverage and enforcement details.

GitHub's documented branch protection availability depends on account plan and visibility: public repositories support it on Free, while private repositories require a supporting paid plan such as Pro. Verify the user's account capability before choosing final visibility; keep the CI merge requirement and explain any unresolved enforcement limitation. Do not change privacy or purchase a plan without the user's choice.

Done means acceptance criteria met, tests and required CI passed, material limitations explicitly accepted where applicable, device evidence collected where required, documentation updated, work integrated, and the forward-plan review recorded. A generated screen or a passing compile alone is insufficient.

As each issue is completed, reread all remaining phases in `PLAN.md` and outstanding issue dependencies before closing it or beginning the next issue. Use the implementation, test results and device discoveries to assess future architecture, sequencing, scope, risks, validation and acceptance gates. Do not limit the review to the next task or the current milestone.

Adjust affected plan sections, technical docs and future issue descriptions/dependencies when the evidence warrants it. Include every resulting repository adjustment in the same issue branch and PR that made it necessary; update affected GitHub issues before that PR merges and link those completed updates in the PR description. Do not leave forward-plan adjustments as promises for an untracked later task. If new evidence after merge requires more work, create a linked follow-up issue and PR. Record the completed issue, discoveries, adjustments and reasons in its completion note and execution plan; if none are needed, explicitly record "No adjustments needed" and why. Keep issue status in GitHub once migrated rather than duplicating the backlog in Markdown. Routine technical refinements can proceed within approved scope; changes to agreed product behavior or user decisions must be presented for approval. Any resulting code/documentation changes follow the normal commit and PR/CI rules.

## After the user merges

Verify the PR is merged in GitHub and record its merge commit and final CI. Fetch the remote and fast-forward local `main`; preserve uncommitted files and active worktrees. Check the completed local issue branch for unpublished commits before deleting it. Remove completed local branches after integration instead of accumulating them; keep active branches and never delete remote branches without separate authorization.

For squash merges, original commits need not be ancestors of `main`. Verify the merged PR’s head matches the completed branch and its work is integrated before cleanup; do not use force deletion merely to silence an unexplained Git warning. Record the handoff and close the issue only after its acceptance gates and forward-plan review are complete. Agents still never merge PRs themselves.

## Optional agent collaboration

No agents or extra tools are required for planning. Default to one integrating agent. When the user authorizes parallel agents, useful independent assignments are engine/model work, editor/UI work, and bounded device/review research after interfaces are agreed. Each receives an issue, exact file ownership, base revision, dependencies, tests, and delivery format. Shared schema changes have one owner. Use independent worktrees to avoid competing indexes/commits.

An agent handoff records: goal and requirement IDs; base/current commit; changed paths; decisions; checks and results; reproduction/demo steps; remaining work and blockers. A reviewer does not rewrite the owner's files silently. Never spawn agents solely to fill available slots.

## Merge policy

The user selected squash and merge on 2026-10-02. GitHub allows squash merging only; merge commits and rebase merging are disabled. Continue making small logical commits with complete titles and bodies on each issue branch. After review and required latest-revision CI pass, squash the PR into one commit on `main`. Keep PRs small enough that this commit represents one coherent change.

GitHub defaults the squash title to the PR title and the body to the PR description. Before merging, check that these describe the final change completely, including purpose, validation and remaining limitations; remove stale progress notes and unfilled template text. Do not leave an empty squash description. Existing history is preserved; never rewrite earlier merged commits to apply this policy retroactively. Start new work from current `main`, rather than reusing a squashed branch.

## Commit format

Title: imperative description of one logical increment, optionally prefixed with `android:`, `docs:`, or `web:`.

Body: explain what changed and why, then list relevant validation and limitations. Link issue/requirement IDs when available. Conventional Commits is optional; clear titles and complete bodies are mandatory.

Example:

    android: Add phase-aware repetition progression

    Model each repetition as ordered named phases with integer durations.
    Advance against the monotonic clock and cancel stale cue events on skip.

    Validation: fake-clock tests cover phase boundaries, pause, and early next.
    Device audio validation remains part of the separate cue integration task.
