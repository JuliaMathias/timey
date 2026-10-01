# Phoenix/API-specific instructions

Inherit the repository rules. Implementation is deferred until the phone release passes acceptance and the user starts the web/API phase.

- Use Phoenix LiveView, Ecto, and SQLite initially. The web editor may run locally on the user's Mac. Android must not require this application to be available.
- Every module gets `@moduledoc`; every function gets `@spec` and `@doc`. For private functions use `@doc false` plus an explanatory comment. Document shared multi-clause definitions once using a head. For macro-generated code document the generator and explicit callback contracts.
- Put business behavior in contexts. LiveViews and JSON controllers call the same context functions; never maintain two separate implementations of routine rules.
- Validate against shared versioned routine fixtures/schema. Keep JSON duration/number semantics compatible with Kotlin. Use optimistic revisions for updates and preserve deletion/conflict semantics in Drive sync.
- Every HTTP endpoint needs maintained OpenAPI documentation, request/response examples, authentication/error explanations, and contract tests. Include CRUD, ordering/copy/conversion, search, settings, import/export and backup restoration. No live timer control endpoints.
- Add ExUnit context/controller tests and meaningful LiveView interaction tests. Use fake Drive transport for normal tests. Test Ecto/SQLite migrations and transaction boundaries.
- Keep OAuth/client secrets and access tokens out of logs, repo, and exports. Use a Google web OAuth client belonging to the same Cloud project as Android; API authentication is separate from Google credentials.
- At scaffolding, document verified `mix setup`, `mix phx.server`, `mix format --check-formatted`, and `mix test` commands. These commands are not available in this directory yet.
