# Execution plan: #22 project lifecycle and learning walkthroughs

Status: implementation and metadata audit complete; CI and user review pending. Owner: Codex. Base revision: `73e2101`. Updated 2026-10-02.

## Purpose and scope

Implement [issue #22](https://github.com/JuliaMathias/timey/issues/22): deliberate project status transitions, native issue relationships at creation and handoff, and file-by-file chat explanations after implementation PR creation. This is workflow maintenance; no Android behavior or product requirement changes.

## Context and approach

`WORKFLOW.md` owns detailed lifecycle policy; root `AGENTS.md` makes it mandatory, issue/PR templates prompt verification, and README/PLAN link the policy. Native parent links describe membership; blocking links describe prerequisites. Plain text references remain supporting context rather than substitutes for relationships.

## Progress and validation

- Read required instructions, product, architecture, workflow, testing and the entire remaining P0–P7 plan; inspected all 15 issues and board items.
- Added four parent relationships: #1 owns #9, #10, #11 and #12.
- Added twelve directed blocking relationships: #1 → #2 → #3 → #4 → #5 → #6 → #7 → #8; #9 → #10; #9 → #11; #10 → #11; #10 → #12; #11 → #12. These match existing documented prerequisites. Verified both ends through GitHub GraphQL and checked for cycles before changes.
- #16, #19 and #22 are independent policy maintenance, with no phase parent or prerequisite links. Related references do not imply blocking.
- Added missing #16 and #19 to the project. Verified all 15 statuses remotely: #1/#11/#22 In progress; #2–#8/#12 Backlog; #9/#10/#16/#19 Done. #22 will move to Review only after its latest-revision CI passes. This is dated audit evidence, not a second live backlog.
- Verified #19's PR #20 was already user-merged at `73e21016`, final head `19b4e5c`, with all required checks passing in run `37049551294`. Recorded completion evidence in [its handoff](https://github.com/JuliaMathias/timey/issues/19#issuecomment-5962163300), explicitly closed #19 and verified Done. No PR was merged by the agent.
- Checked changed local Markdown link targets, required status/relationship/walkthrough guidance and whitespace. GitHub Actions remains required for this documentation PR; record its final result in #22 and the PR.

## Manual validation

No Android manual tests are required because this task changes documentation and GitHub metadata only. Remote status and relationship read-back verifies the actual board outcome. Phone 2 speech/timing validation in #11 remains unrun by the user; #11 stays In progress and #12 stays Backlog. Phone 1 remains a compatibility target.

## Decisions and forward-plan review

No adjustments needed to approved product scope, technical approach, P0–P7 ordering, tests or phone-before-web gates. All remaining phase descriptions and issue prerequisites were reviewed. This audit makes the existing ordering executable in GitHub, rather than introducing new dependencies. Corrected stale PLAN references claiming closed #9 remained open. Future issues must choose Ready/Backlog deliberately and verify native relationships; future implementation PRs require a current-chat walkthrough for every changed path. Entirely documentation/workflow PRs are exempt from that walkthrough only.

## Recovery and handoff

Native links and statuses were read back after mutation. Recheck the current board before retries; preserve existing relationships and avoid duplicates/cycles. Changes are isolated to `codex/issue-22-board-relationships`. Preserve active issue #11 work and the unrelated untracked Android Studio daemon configuration. Leave this PR open for user review and manual merge; after verified integration, finish explicit issue closure and Done verification.
