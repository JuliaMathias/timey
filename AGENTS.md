# Timey working instructions

## Read first

- Read `README.md`, `docs/PRODUCT.md`, `docs/PLAN.md`, and the relevant parts of `docs/ARCHITECTURE.md` before implementing behavior. Read `docs/WORKFLOW.md` for task ownership and review.
- Explicit user instructions take precedence. Distinguish confirmed requirements from proposed defaults and unresolved questions. Do not silently change either into the other.
- Read `android/AGENTS.md` before touching Android and `web/AGENTS.md` before touching Elixir, even when your working directory is the repository root.
- The user approved the delivery plan on 2026-10-01. Confirm GitHub owner/name/visibility before remote creation, and complete environment setup to begin P0. Android comes first; Phoenix and the documented HTTP API follow phone acceptance. Do not interpret plan approval as permission to install tools or create unrelated external services.

## Git discipline

- Never merge PRs on the user’s behalf, enable auto-merge, schedule merges or invoke a merge queue. Prepare and validate the PR, resolve conflicts and report readiness, then leave it open for the user to approve and merge manually in GitHub. Passing CI or earlier implementation/publication authorization does not grant merge permission.

- All repository changes, including documentation, fixes and maintenance, must use a branch dedicated to a GitHub issue and a linked PR targeting `main`. Create the issue before changing files if none exists. Name branches `codex/issue-<number>-<short-description>`; never implement directly on `main` or mix unrelated issues on one branch. Integrate only through a PR after required latest-revision CI passes.

- Commit as you go in small, logical, reviewable increments. Do not wait for an entire feature. Each commit must have a descriptive title AND a nonempty body explaining all changes, their purpose, and validation or remaining limitations.
- Every PR must be associated with its issue through GitHub’s Development link; verify the sidebar association after creation. A text reference alone is insufficient. Automatic closure of merged linked issues is disabled: close issues explicitly only after all acceptance and forward-plan gates are met.
- Merge PRs using squash and merge only, after required latest-revision CI passes. Keep small logical development commits; the final squash commit must also have a descriptive title and complete nonempty body. GitHub uses the PR title and description by default, so reconcile them with the final implementation and actual validation before merging. Never rewrite existing merged history to apply this policy retroactively.
- After the user merges a PR, verify GitHub reports it merged, fetch and fast-forward local `main`, then delete its completed local issue branch. Check for unpublished commits, uncommitted work and active worktrees first; preserve all unrelated work. Squash merges require verification through the merged PR rather than ancestry alone. Do not delete remote branches without separate authorization.
- Stage explicit paths. Inspect the staged diff; never include unrelated user changes, credentials, personal databases, or raw reference media. Do not amend, reset, rebase, or force-push another contributor's work without authorization.
- Run the meaningful checks for each increment before committing. A scaffold or documentation commit can use structural/link checks; do not claim application tests ran when there is no application.
- Once GitHub CI exists, every PR must run the applicable automated tests through GitHub Actions and pass the required merge check on the latest revision. Do not merge with failed, cancelled, missing, or still-running tests, bypass protections, or substitute local results for required CI. Configure this during P0; add web/API tests when that application exists.
- A feature may span several coherent commits, but do not deliberately leave a broken build. End-of-task reports include commit IDs and actual verification results.

## Readability and documentation

- Document every module/class's purpose, boundaries, and important dependencies. Add a function comment explaining its contract, parameters/results where useful, and non-obvious constraints. Prefer explanations of why over restating syntax.
- In Kotlin, use KDoc for classes and functions, including private functions. In Elixir, give every function a `@spec` and `@doc`, including private functions (`@doc false` with an adjacent explanatory comment where private documentation is not exposed). Add `@moduledoc` for every Elixir module. Use a function head for shared docs/specs across clauses.
- Keep names concrete, functions small, dependencies explicit, and domain behavior separate from platform code. Avoid speculative frameworks and unnecessary abstractions.
- Treat README as the project's documentation entry point: keep feature status, setup/run/test commands, directory map, and links accurate in the same change as behavior. Add focused guides when detail would overwhelm it.
- Explain new Kotlin/Android concepts in plain language and connect them to Elixir concepts where useful. Summarize one relevant learning point with each meaningful delivery; do not fill source files with tutorials.

## Tests and completion

- Every feature needs meaningful automated behavioral tests. Every bug fix needs a regression test where feasible. Use `docs/TESTING.md` to select the level; test outcomes and failure cases, not getters or implementation mirrors.
- Use controllable clocks and fake cue/sync adapters. Do not rely on real sleep, external accounts, or a real TTS engine for ordinary unit tests.
- Add device tests/manual evidence for Android lifecycle, screen-off timing, audio, permissions, and notification/overlay behavior. Unit tests alone do not establish device reliability.
- For each issue requiring manual validation, include a complete runnable manual-test procedure in the issue itself: prerequisites, exact test data, numbered actions with expected results, timing/pass-fail criteria, evidence to record, and cleanup. Keep it aligned with the implemented UI/build; do not leave "test on device" as the instruction. Record actual results or blocked/unrun status. If manual validation is unnecessary, state why; manual checks do not replace required automated CI.
- Update requirements/architecture/plan and learning notes when decisions or behavior change. Never mark an acceptance criterion passed without evidence. Report missing tools and unrun checks clearly.
- Record accepted architecture-shaping decisions in `docs/decisions/` with context, rationale, alternatives and consequences, following its README. Product preferences stay in `PRODUCT.md`; architecture records link to those requirements rather than replacing them. Preserve superseded decisions and link replacements instead of silently rewriting the reasoning.
- At completion of every issue, reread the entire remaining delivery plan and outstanding issue dependencies before closing the issue or starting the next one. Assess what the completed work changes about future scope, technical approach, ordering, risks, tests and acceptance gates. Update affected plans/docs/issues with evidence and rationale; record "No adjustments needed" when appropriate. Preserve agreed product requirements and flag proposed changes to user decisions for approval. Include the review outcome in the issue handoff/completion report. All resulting repository adjustments belong in the same issue branch and PR that caused them; complete affected GitHub issue updates before merging and link them in the PR. Do not defer those adjustments to untracked later work.

## Product boundaries

- Phone editing and playback must work offline without Phoenix or Google. Cloud sync is optional and must not block local saves or active playback.
- Sync versioned structured records, not a live SQLite file. Autosaves coalesce; explicit-save backups survive reconnect. Preserve both versions on conflicts and prevent deleted records from reappearing.
- Runtime takes an immutable routine snapshot. No editing/sync mutation may change a running workout. Timer progression never depends on speech completion or UI frame ticks.
- Simple and Advanced are separate routine types; converting creates an Advanced copy. Loops are named, ordered, repeatable, and not nested; rep phases are not nested loops.
- Provide the required movable panel over other apps when overlay access is granted, with notification controls as permission-denial fallback. The panel renders service state; it never owns a separate timer.
- Do not infer technical implementation from screenshots (for example, whether the banner uses heads-up notifications or an overlay). Verify on the actual target phone.

## Agents and research

- Assign appropriate existing GitHub labels whenever creating an issue, as part of creation rather than a later cleanup. Use labels matching its scope/type (for example `android`, `audio`, `sync`, `web`, `docs`, `bug`, or `phase`); verify the created issue has them. Do not leave new issues unlabeled.

- One owner integrates each task. Delegate only when explicitly authorized for that task. When authorized, assign bounded work, acceptance criteria, file ownership, and base revision; isolate concurrent edits in worktrees when appropriate.
- Shared contracts require one owner; coordinate before editing them. Agents return changed paths, commits, tests, risks, and remaining work. Do not use chat history as the only handoff.
- Verify changing platform rules and library compatibility from official sources. Record material sources and decisions. Never install tools or create external services merely because this guide mentions them.

## Code review rules

- Check offline operation, monotonic timing, cue cancellation, database migrations, sync races, deletion/conflict preservation, backup retention, permissions, accessibility, and documentation/tests.
- Confirm every commit has a title/body and every new function follows the language documentation rules. Keep review findings concrete and tied to observable behavior.
