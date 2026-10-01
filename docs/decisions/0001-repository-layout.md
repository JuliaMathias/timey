# 0001: One repository, independently built applications

Date: 2026-10-01. Status: accepted by the user.

## Decision

Keep Android in `android/`, later Phoenix/API in `web/`, and shared specifications, fixtures and documentation in the root repository. Git history includes both applications, with descriptive scoped titles and complete bodies. Their versions/builds/releases remain independent.

## Reason

One user maintains both products. Routine schemas, Drive sync semantics, import/export and API behavior must agree. One change can update contracts, both implementations and tests together, and agents can discover consistent guidance from one checkout. Android is delivered first; the web directory initially contains only scoped instructions.

## Alternatives and consequences

Separate repositories give independent permissions/history but add versioned contract distribution, duplicate documentation and coordinated PR/release work. Reconsider when ownership or distribution changes. If splitting later, preserve history with a documented migration rather than rewriting an existing shared remote without approval.

## Management boundary

The user approved the local repository layout. GitHub owner, repository creation, task migration and application implementation remain subject to the requested plan approval. No remote system is created by this decision.
