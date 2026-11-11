# Nexus AI Assistant V3.0 — Security & Sandboxing Report

## 1. Zero Direct Code Execution on Phone or Production Backend
Untrusted repository code discovered during autonomous scouting is NEVER executed directly on the Android operating system or on the bare backend host.
- Execution occurs only within **OpenSandbox** simulation environments with strict memory, CPU, and network boundaries.
- Syscalls such as `os.system()`, `subprocess.Popen()`, `shutil.rmtree()`, and arbitrary dynamic evaluations (`eval()`, `exec()`) are explicitly intercepted and blocked.

## 2. Package Allowlist Policy
Only verified packages with audited licenses and high community trust are permitted:
- Permitted: `crewai`, `smolagents`, `langgraph`, `pydantic`, `httpx`, `fastapi`, `numpy`, `sympy`, `openwake-word`, `opik`, `promptfoo`
- Any external package outside this allowlist triggers a security pause requiring cryptographic operator authorization.

## 3. Ephemeral Live Tokens & Zero Hardcoded Secrets
- Permanent Gemini, GitHub, or Hugging Face API keys are strictly excluded from the Android APK client.
- The mobile app interacts with the backend via short-lived ephemeral tokens generated on demand by `LiveTokenService`.
- Backend secrets are injected via GitHub Actions Secrets or server environment variables.

## 4. Privacy & PII Scrubbing
- The **Hindsight Memory Engine** automatically scrubs tokens, API keys, email addresses, and phone numbers before caching them in long-term memory.
- Duplicate entries are eliminated via SHA-256 hash checksums.

## 5. Human-in-the-Loop Sensitive Action Gates
- Sensitive actions during browser use (e.g. checkout, payment, credentials entry, account deletions, message dispatching) are flagged with `requires_user_approval = True` and blocked until explicitly authorized by the operator.
