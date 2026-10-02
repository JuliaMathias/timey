"""Require real passing instrumentation results, even if Gradle exits zero on device failure."""

from pathlib import Path
import re
import sys


def verify_report(report: Path, minimum_tests: int = 2) -> int:
    """Validate AGP's generated HTML summary; missing/changed reports fail closed.

    AGP 9.4 can return exit code zero after an APK installation failure, generating
    a zero-test summary. Use its actual result counts as well as Gradle's exit code.
    Clear the generated report before running tests so stale results cannot qualify.
    """
    html = report.read_text(encoding="utf-8")
    matches = re.findall(
        r'<div class="infoBox[^\"]*" id="(tests|failures|skipped)">\s*'
        r'<div class="counter">(\d+)</div>',
        html,
    )
    counts = dict(matches)
    if len(matches) != 3 or set(counts) != {"tests", "failures", "skipped"}:
        raise ValueError("Missing or ambiguous instrumentation summary")
    if int(counts["tests"]) < minimum_tests:
        raise ValueError(f"Expected at least {minimum_tests} executed tests; got {counts['tests']}")
    if int(counts["failures"]) or int(counts["skipped"]):
        raise ValueError(f"Instrumentation was not fully successful: {counts}")
    return int(counts["tests"])


def main() -> None:
    """Read the specified AGP report and exit nonzero if results cannot establish a pass."""
    try:
        count = verify_report(Path(sys.argv[1]), int(sys.argv[2]) if len(sys.argv) > 2 else 2)
    except (IndexError, OSError, ValueError) as error:
        sys.exit(f"Android test verification failed: {error}")
    print(f"Verified {count} executed instrumentation tests; no failures or skips.")


if __name__ == "__main__":
    main()
