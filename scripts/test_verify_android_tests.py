"""Regression tests for the CI guard against false-successful Android test execution."""

from pathlib import Path
import tempfile
import unittest

from verify_android_tests import verify_report


class AndroidResultGuardTest(unittest.TestCase):
    """Prove infrastructure failures, absent reports and skipped tests cannot pass the gate."""

    def check_counts(self, tests: int, failures: int, skipped: int) -> int:
        """Write a disposable AGP-shaped report and run the production verifier."""
        with tempfile.TemporaryDirectory() as directory:
            report = Path(directory) / "index.html"
            report.write_text("".join(
                f'<div class="infoBox" id="{name}">\n<div class="counter">{value}</div></div>'
                for name, value in (("tests", tests), ("failures", failures), ("skipped", skipped))
            ))
            return verify_report(report)

    def test_accepts_executed_passing_suite(self) -> None:
        """A genuine passing suite satisfies the guard without fixing the total forever."""
        self.assertEqual(self.check_counts(2, 0, 0), 2)
        self.assertEqual(self.check_counts(5, 0, 0), 5)

    def test_rejects_zero_test_infrastructure_failure(self) -> None:
        """Regress the observed AGP install failure that exited zero with no tests."""
        with self.assertRaises(ValueError):
            self.check_counts(0, 0, 0)

    def test_rejects_incomplete_suite(self) -> None:
        """One test cannot satisfy the two-case foundation suite."""
        with self.assertRaises(ValueError):
            self.check_counts(1, 0, 0)

    def test_rejects_failed_tests(self) -> None:
        """Any failed test blocks the suite."""
        with self.assertRaises(ValueError):
            self.check_counts(2, 1, 0)

    def test_rejects_skipped_tests(self) -> None:
        """Skipped mandatory tests must not be treated as passed."""
        with self.assertRaises(ValueError):
            self.check_counts(2, 0, 1)

    def test_rejects_missing_or_changed_report(self) -> None:
        """Missing files and changed report structure fail closed."""
        with tempfile.TemporaryDirectory() as directory:
            report = Path(directory) / "index.html"
            with self.assertRaises(FileNotFoundError):
                verify_report(report)
            report.write_text("<html>No result summary</html>")
            with self.assertRaises(ValueError):
                verify_report(report)


if __name__ == "__main__":
    unittest.main()
