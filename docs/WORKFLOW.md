# Project management and agent workflow

Status: GitHub Issues, phase milestones and a Project board selected on 2026-10-01; repository layout approved. Delivery plan approved on 2026-10-01; public repository [JuliaMathias/timey](https://github.com/JuliaMathias/timey) created on 2026-10-02. Eight milestones and the initial twelve delivery issues were created; the [Timey Project board](https://github.com/users/JuliaMathias/projects/7/views/3) is created and populated.

## Selected system

Use the public JuliaMathias/timey repository, GitHub Issues for executable tasks, milestones for phases, and one GitHub Project board. The user explicitly selected public visibility on 2026-10-02. GitHub is not technically necessary: local Markdown and Git are enough to start. It is useful here because human decisions, issue dependencies, code review, and automated checks can live together. We do not need Linear, Jira, Notion, or an agent orchestration service for this scope.

Docs describe enduring requirements and decisions. Issues describe work and status. The board displays those issues; it is not a second backlog. Chats are discussion, not the source of truth for accepted behavior. Do not maintain a competing status table in several places.

`PRODUCT.md` is the current behavior specification; `ARCHITECTURE.md` describes the current technical design; `PLAN.md` sequences delivery. Numbered records in `docs/decisions/` explain why durable architecture-shaping choices were made, which alternatives were considered and their consequences. Use its index/policy to decide when a choice needs a record. Language, color and other ordinary product preferences do not each need an ADR; choices affecting contracts, persistence, synchronization or runtime ownership do. Add/update the record and index alongside the relevant specification change. Mark replaced records superseded and link successors to retain decision history.

Every new GitHub issue must receive appropriate existing labels when it is created. Select labels for its actual scope/type: for example `android` and `audio` for speech work, `docs` for workflow/documentation changes, `bug` for defects, and `phase` for milestone trackers. Verify the labels after creation; do not defer labeling until later or leave an issue unlabeled.

## Project status and issue relationships

Every issue belongs on the [Timey project board](https://github.com/users/JuliaMathias/projects/7/views/3), including documentation, workflow and maintenance issues. Update its Status as part of the action below and read it back to verify it persisted; do not leave the board for later cleanup. The board is a view of GitHub issues, not a competing source of acceptance evidence.

| Status | When to set it |
| --- | --- |
| Backlog | At creation when work cannot start yet: an unfinished prerequisite, unresolved scope/decision, missing setup, or a later approved phase blocks it. Record the reason and actual blocking issues. |
| Ready | At creation when the issue is scoped, acceptance/validation are clear, and prerequisites are satisfied so implementation can start. Choose deliberately rather than inheriting an automatic project default. |
| In progress | As soon as work starts on the issue, before implementation. Keep it here while implementation, fixes, CI, required manual/device evidence or other acceptance work remains, even if a PR is already open. Record newly discovered blockers instead of treating them as review. |
| Review | Only when implementation, documentation, required latest-revision CI, manual acceptance evidence and forward-plan review are complete, and user review/approval and the resulting manual merge are the only remaining gate. |
| Done | Whenever the issue is closed. Record its closure reason: closed as duplicate/not planned does not claim the original acceptance criteria passed. Never mark an open issue Done solely because its PR merged. |

A phase tracker becomes In progress when its child work starts. It reaches Review only when its child deliverables and phase gates are satisfied and only user acceptance/review remains. Reopening or discovering further work requires reassessing Status: use In progress for resumed work, or Ready/Backlog for work not yet started according to prerequisites. When a prerequisite closes, reassess its unstarted dependents for Ready; do not start them automatically or skip remaining blockers. After the user merges, finish the integration/closure handoff, close only when all completion gates are met, then verify Done on the board. Agents still never merge PRs.

At issue creation, inspect `PLAN.md`, the relevant phase tracker and existing issues before choosing relationships. Use GitHub's native **parent/sub-issue** links for a deliverable belonging to a larger issue, and native **blocked by/blocking** links for execution prerequisites. A phase sequence is a dependency chain, not a parent hierarchy. A prerequisite A that must finish before B is recorded as **A blocks B / B is blocked by A**; verify both ends after creation. Parent membership alone is not a blocking dependency, and a child must not be blocked by completion of the parent that depends on that child.

Record requirement IDs, milestone, relationship rationale and non-issue blockers in the issue body as supporting context. A `Parent: #1` or `Dependencies: #11` sentence alone does not create native relationships. Use ordinary issue references for related context without inventing a parent or blocking link. If no parent or blockers apply, explicitly record that decision. Preserve existing links and parents; do not replace them or create circular dependencies merely to fill fields. Recheck relationships when scope changes, at handoff/closure and during the forward-plan review; update affected issues before the originating PR merges.

Creation checklist: choose existing scope/type labels; add the issue to the project; decide and set Ready or Backlog with a reason; set its applicable parent and prerequisite links; verify labels, Status and relationships remotely. One owner per issue. Begin with one implementation task at a time; add concurrency only for independent work after the contracts stabilize. Use milestones P0–P7 from `PLAN.md` where applicable; independent workflow maintenance need not become a phase child.

Native APIs and semantics: [creating dependencies](https://docs.github.com/en/issues/tracking-your-work-with-issues/using-issues/creating-issue-dependencies), [issue dependency API](https://docs.github.com/en/rest/issues/issue-dependencies) and [sub-issue API](https://docs.github.com/en/rest/issues/sub-issues). Inspect the actual project Status option IDs rather than hard-coding values from another project.

## After plan approval

1. Repository creation is complete: [JuliaMathias/timey](https://github.com/JuliaMathias/timey), public, with documentation history pushed. Keep personal reference media and credentials outside Git.
2. Create milestone issues from the task groups in `PLAN.md`, split into small tasks with observable acceptance criteria and meaningful tests. Preserve requirement IDs, dependencies, scope, and learning objectives.
3. Board creation is complete: [Timey](https://github.com/users/JuliaMathias/projects/7/views/3), with Backlog/Ready/In progress/Review/Done states. Maintain live item status and native issue relationships using the rules above, including newly created maintenance issues. Keep the default private project visibility unless the user chooses otherwise; the repository and issues are public. Add existing issues rather than duplicating the backlog.
4. Start P0 only after plan approval. Later phases remain visible but do not block the first runnable phone prototype.
5. As part of P0, add GitHub Actions tests and protect `main` so PRs cannot merge until the required checks pass. Verify enforcement with a deliberately failing test PR followed by a passing revision. Record the actual required check names and settings; do not merely add a workflow file and assume it blocks merges.

The GitHub CLI is already available on this Mac. Authenticated API access as JuliaMathias was verified on 2026-10-02. The user completed additional Projects authorization on 2026-10-02; project creation/linking, Status options, board columns and item placement were verified through GitHub APIs. Templates in `.github/` standardize tasks, bugs, and review without installing anything.

## Task lifecycle

**Human approval and merging are mandatory.** Agents may create branches, commit/push changes, open/update PRs, resolve conflicts, run checks and report readiness. They must never merge a PR, enable auto-merge, schedule a merge, or invoke a merge queue on the user’s behalf. Leave every PR open for the user to review, approve and merge manually in GitHub. Passing CI, plan approval or permission to implement/publish does not authorize an agent to merge.

Before coding, read relevant instructions and requirement IDs, inspect current state and dependencies, identify a concrete slice, and set the issue’s project Status to In progress. A multi-session task gets a plan based on `docs/plans/TEMPLATE.md`. Keep progress, evidence, and decisions current so another agent can resume without the original chat.

Implement and test a coherent increment, update its documentation, inspect the diff, and commit with a title and body. Useful increments include a model plus validation tests, persistence plus migration tests, and a screen plus interaction tests. Do not hold all work until a large feature is complete. Do not create knowingly broken intermediate commits.

When manual validation is needed, write complete instructions in the issue before requesting the user's test: prerequisites/build installation, exact fixtures/settings, numbered actions and expected results, variants/pass-fail criteria, evidence and cleanup. Update them to match the implemented UI. Use the procedure requirements in `TESTING.md`; "test on your phone" is insufficient. Record actual results and outstanding cases before closing the issue. If none are needed, state why.

All repository changes must use a branch dedicated to a GitHub issue and a PR targeting `main`. This includes code, documentation, bug fixes and maintenance. Create an issue first if the work has no existing issue. Start from current `main` and name the branch `codex/issue-<number>-<short-description>` (for example, `codex/issue-11-phased-reps`). Do not implement directly on `main`, reuse a merged branch for new work, or combine unrelated issues in one branch.

Keep making small logical commits with complete titles and bodies on that branch. Open a small linked PR, using `Closes #<number>` only when it completes the issue; otherwise use `Refs #<number>` and describe the remaining work. Integrate through the PR after required latest-revision CI passes. Link the issue and include actual validation. Commits are local by default; commit authorization is not automatic permission to publish or merge. A review checks the behavior against the issue, not just style.

Immediately after opening a new implementation PR, send a **file-by-file learning walkthrough in the current project chat**. Explain every changed file's role, what changed and why, and how it connects to the user-visible behavior. Include source, tests, build configuration and accompanying documentation; every changed path must be accounted for, although files with the same purpose may share an explanation. Explain Kotlin/Android concepts in plain language and connect them to familiar Elixir ideas where useful. Describe the relevant test evidence and remaining limitations without presenting unrun checks as passed. Link the PR and files so the user can follow along. The PR description and a terse completion summary do not replace this chat message. Update the walkthrough if later revisions materially change the files or approach.

The walkthrough is exempt only when the PR consists entirely of documentation or workflow changes. Mixed PRs containing application, test or build behavior still require it; explain non-Android implementation in the same accessible way when relevant. This exemption does not waive normal PR descriptions, documentation, validation or CI. Do not send the walkthrough to another chat or external messaging service without the user's authorization.

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
