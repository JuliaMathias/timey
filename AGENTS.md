# Timey working instructions

## Read first

- Read `README.md`, `docs/PRODUCT.md`, `docs/PLAN.md`, and the relevant parts of `docs/ARCHITECTURE.md` before implementing behavior. Read `docs/WORKFLOW.md` for task ownership and review.
- Explicit user instructions take precedence. Distinguish confirmed requirements from proposed defaults and unresolved questions. Do not silently change either into the other.
- Read `android/AGENTS.md` before touching Android and `web/AGENTS.md` before touching Elixir, even when your working directory is the repository root.
- Current scope is planning. Do not begin application implementation or create remote issues until the user approves the plan. Android comes first; Phoenix and the documented HTTP API come later.

## Git discipline

- Commit as you go in small, logical, reviewable increments. Do not wait for an entire feature. Each commit must have a descriptive title AND a nonempty body explaining all changes, their purpose, and validation or remaining limitations.
- Stage explicit paths. Inspect the staged diff; never include unrelated user changes, credentials, personal databases, or raw reference media. Do not amend, reset, rebase, or force-push another contributor's work without authorization.
- Run the meaningful checks for each increment before committing. A scaffold or documentation commit can use structural/link checks; do not claim application tests ran when there is no application.
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
- Update requirements/architecture/plan and learning notes when decisions or behavior change. Never mark an acceptance criterion passed without evidence. Report missing tools and unrun checks clearly.

## Product boundaries

- Phone editing and playback must work offline without Phoenix or Google. Cloud sync is optional and must not block local saves or active playback.
- Sync versioned structured records, not a live SQLite file. Autosaves coalesce; explicit-save backups survive reconnect. Preserve both versions on conflicts and prevent deleted records from reappearing.
- Runtime takes an immutable routine snapshot. No editing/sync mutation may change a running workout. Timer progression never depends on speech completion or UI frame ticks.
- Simple and Advanced are separate routine types; converting creates an Advanced copy. Loops are named, ordered, repeatable, and not nested; rep phases are not nested loops.
- Do not infer technical implementation from screenshots (for example, whether the banner uses heads-up notifications or an overlay). Verify on the actual target phone.

## Agents and research

- One owner integrates each task. Delegate only when explicitly authorized for that task. When authorized, assign bounded work, acceptance criteria, file ownership, and base revision; isolate concurrent edits in worktrees when appropriate.
- Shared contracts require one owner; coordinate before editing them. Agents return changed paths, commits, tests, risks, and remaining work. Do not use chat history as the only handoff.
- Verify changing platform rules and library compatibility from official sources. Record material sources and decisions. Never install tools or create external services merely because this guide mentions them.

## Code review rules

- Check offline operation, monotonic timing, cue cancellation, database migrations, sync races, deletion/conflict preservation, backup retention, permissions, accessibility, and documentation/tests.
- Confirm every commit has a title/body and every new function follows the language documentation rules. Keep review findings concrete and tied to observable behavior.

