import re
import sys
import os

# -------------------------
# Config: Patterns
# -------------------------

# Section headers commonly found in JDs (longest first)
SECTION_HEADERS = [
    r"technical requirements(?: \(must have\))?",
    r"desired skills and experience",
    r"required qualifications",
    r"minimum qualifications",
    r"preferred qualifications",
    r"experience required",
    r"job responsibilities",
    r"role and responsibilities",
    r"role description",
    r"role proficiency",
    r"measure of outcomes",
    r"outcomes",
    r"key responsibilities",
    r"core responsibilities",
    r"primary responsibilities",
    r"responsibilities and duties",
    r"position summary",
    r"responsibilities",
    r"job description",
    r"about the role",
    r"about the job",
    r"introduction",
    r"about the company",
    r"role overview",
    r"what you'll do",
    r"what we expect",
    r"you should have",
    r"you will be responsible for",
    r"competencies",
    r"education and experience",
    r"technical skills",
    r"skills",
]

# Common filler/boilerplate phrases in JDs
FILLER_PHRASES = [
    r"greeting of the day",
    r"interested candidate can apply here",
    r"looking for immediate to \d+ days joiners only",
    r"hiring for",
    r"apply now",
    r"we are looking for",
    r"we are hiring",
    r"job location.*",
    r"work from home",
    r"remote work available",
    r"full time position",
    r"part time position",
    r"contract position",
    r"temporary position",
    r"salary and benefits.*",
    r"about us.*",
    r"company overview.*",
    r"our company.*",
    r"we offer.*",
    r"equal opportunity employer.*",
]


# -------------------------
# Helpers
# -------------------------

def remove_section_headers(line: str) -> str:
    """Remove known section headers."""
    for header in sorted(SECTION_HEADERS, key=len, reverse=True):
        line = re.sub(rf'\b{header}\b[:]*', '', line, flags=re.I)
    return line.strip()


def remove_filler_phrases(line: str) -> str:
    """Remove boilerplate/filler phrases."""
    for filler in FILLER_PHRASES:
        line = re.sub(filler, '', line, flags=re.I)
    return line.strip()


def replace_noisy_separators(line: str) -> str:
    """Fix noisy separators like leading '-' or '|-' sequences."""
    line = re.sub(r'^\s*-\s*', '', line)         # remove leading dashes
    return re.sub(r'\|+-', '.', line)            # replace |--- with .


def split_slash_tokens(line: str) -> str:
    """Convert slash-separated skills into comma-separated."""
    return re.sub(r'(\w)\s*/\s*(\w)', r'\1, \2', line)


def normalize_bullets(line: str) -> str:
    """Turn leading '-' bullets into '.' sentences."""
    return re.sub(r'^\s*-\s*', '. ', line)


def fix_colon_dot(line: str) -> str:
    """Fix ':.' â '.' cases without breaking 'skills: python' style."""
    return re.sub(r':\s*\.', '.', line)


def ensure_sentence_end(line: str) -> str:
    """Ensure each line ends with a period."""
    if not re.search(r'[.!?]$', line):
        line += '.'
    return line


# -------------------------
# Main Cleanup Logic
# -------------------------

def clean_jd_text(text: str) -> str:
    # Step 1: Normalize newlines, strip blanks
    lines = [line.strip() for line in text.splitlines() if line.strip()]

    # Step 2: Remove headers
    lines = [remove_section_headers(line) for line in lines if line.strip()]

    # Step 3: Remove filler phrases
    lines = [remove_filler_phrases(line) for line in lines if line.strip()]

    # Step 4: Clean noisy separators
    lines = [replace_noisy_separators(line) for line in lines]

    # Step 5: Fix slash tokens
    lines = [split_slash_tokens(line) for line in lines]

    # Step 6: Normalize bullets
    lines = [normalize_bullets(line) for line in lines]

    # Step : Fix colon+dot issues
    # lines = [fix_colon_dot(line) for line in lines]

    # Step 7: Ensure sentence end & lowercase
    lines = [ensure_sentence_end(line) for line in lines if line.strip()]

    # Step 8: Collapse multiple dots
    lines = [re.sub(r'\.{2,}', '.', line) for line in lines]
    
    # Step 9: Fix colon+dot issues
    lines = [re.sub(r'[:\.]{1,2}', '.', line) for line in lines]

    # Step 10: Remove duplicates
    seen = set()
    unique_lines = []
    for line in lines:
        if line not in seen:
            unique_lines.append(line)
            seen.add(line)

    # Step 11: Join into single cleaned string
    final_text = ' '.join(unique_lines)
    #final_text = '. '.join(unique_lines)
    return re.sub(r'\.{2,}', '.', final_text)


def clean_jd_file(input_file: str, output_file: str):
    if not os.path.exists(input_file):
        print(f"Error: Input file '{input_file}' does not exist.")
        sys.exit(1)

    with open(input_file, 'r', encoding='utf-8') as f:
        original_text = f.read()

    cleaned_text = clean_jd_text(original_text)

    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(cleaned_text)

    print(f"Cleaned JD written to {output_file}")


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python clean_jd_text.py <input_jd_file>")
        sys.exit(1)

    input_file = sys.argv[1]
    output_file = "cleaned_jd.txt"
    clean_jd_file(input_file, output_file)
