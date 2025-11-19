# app/utils/loader_utils.py

import csv
import os
from typing import Set

# ---------------------
# Resource paths
# ---------------------
RESOURCE_PATHS = {
    "skills": os.path.join("app", "resources", "skills.txt"),
    "titles": os.path.join("app", "resources", "job_titles.txt"),
    "titles_extra": os.path.join("app", "resources", "job_titles_extra.txt"),
    "qualifications": os.path.join("app", "resources", "qualifications.txt"),
}


# ---------------------
# Helper function
# ---------------------
def _load_lines(filepath: str) -> Set[str]:
    """Load non-empty, lowercased lines from a text file."""
    if not os.path.exists(filepath):
        raise FileNotFoundError(f"File not found: {filepath}")
    with open(filepath, encoding="utf-8", errors="ignore") as f:
        return {line.strip().lower() for line in f if line.strip()}


def _load_tsv_column(filepath: str, column_name: str) -> Set[str]:
    """Load values from a TSV file column into a lowercase set."""
    if not os.path.exists(filepath):
        raise FileNotFoundError(f"File not found: {filepath}")
    values = set()
    with open(filepath, encoding="utf-8", errors="ignore") as f:
        reader = csv.DictReader(f, delimiter="\t")
        for row in reader:
            value = row.get(column_name, "").strip()
            if value:
                values.add(value.lower())
    return values


# ---------------------
# Public loader functions
# ---------------------
def load_skills(filepath: str = RESOURCE_PATHS["skills"]) -> Set[str]:
    """Load skills from a text file."""
    return _load_lines(filepath)


def load_job_titles(filepath: str = RESOURCE_PATHS["titles"]) -> Set[str]:
    """Load job titles from TSV file column 'Reported Job Title'."""
    return _load_tsv_column(filepath, "Reported Job Title")


def load_extra_job_titles(filepath: str = RESOURCE_PATHS["titles_extra"]) -> Set[str]:
    """Load additional job titles from text file."""
    return _load_lines(filepath)


def load_qualifications(filepath: str = RESOURCE_PATHS["qualifications"]) -> Set[str]:
    """Load qualifications from a text file."""
    return _load_lines(filepath)
