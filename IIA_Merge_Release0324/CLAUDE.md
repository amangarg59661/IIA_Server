# Project AI Instructions

Before analyzing architecture or making code changes, check Graphify output first.

Graphify files:
- graphify-out/GRAPH_REPORT.md
- graphify-out/graph.json
- graphify-out/graph.html

Use `GRAPH_REPORT.md` for high-level architecture.
Use `graph.json` for file/entity relationships.
Use raw source files only when graph context is incomplete or stale.

When using Ruflo:
- Start with a plan.
- Use Graphify context before spawning agents.
- For Spring Boot work, trace controller -> service -> repository -> entity -> database.
- Do not edit files until the plan is approved.