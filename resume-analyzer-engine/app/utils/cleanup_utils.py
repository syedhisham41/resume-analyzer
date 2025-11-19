import re
import sys
import os

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

def clean_jd_text(text: str) -> str:
    # Step 1: Normalize newlines and strip
    lines = [line.strip() for line in text.splitlines() if line.strip()]

    # Step 2: Remove section headers (longest first)
    SECTION_HEADERS.sort(key=len, reverse=True)
    filtered_lines = []
    for line in lines:
        for header in SECTION_HEADERS:
            line = re.sub(rf'\b{header}\b[:]*', '', line, flags=re.I)
        if line.strip():
            filtered_lines.append(line.strip())

    # Step 3: Remove filler phrases
    cleaned_lines = []
    for line in filtered_lines:
        for filler in FILLER_PHRASES:
            line = re.sub(filler, '', line, flags=re.I)
        line = line.strip()
        if line:
            cleaned_lines.append(line)
            
        def replace_noisy_separators(line: str) -> str:
            # Replace one or more "|" with a period
            return re.sub(r'\|+', '.', line)
        
        def normalize_apostrophes(text: str) -> str:
            # Replace curly apostrophes with straight ones
            return text.replace("â", "'").replace("â", "'")

        cleaned_lines = [replace_noisy_separators(line) for line in cleaned_lines]
        cleaned_lines = [normalize_apostrophes(line) for line in cleaned_lines]

    # Step 4: Replace slashes in skill-like tokens with commas
    # Example: "python/pyspark" -> "python, pyspark"
    def split_slash_tokens(text_line):
        # Only split if slash is between word characters
        #return re.sub(r'(\w)/(\w)', r'\1, \2', text_line)
        return re.sub(r'(\w)\s*/\s*(\w)', r'\1, \2', text_line)

    cleaned_lines = [split_slash_tokens(line) for line in cleaned_lines]

    # Step 5: Add period at end if missing (for NLP sentence separation)
    final_lines = []
    for line in cleaned_lines:
        if not re.search(r'[.!?]$', line):
            line += '.'
        final_lines.append(line.lower())

    # Step 6: Remove duplicate lines
    seen = set()
    unique_lines = []
    for line in final_lines:
        if line not in seen:
            unique_lines.append(line)
            seen.add(line)

    # Step 7: Return cleaned text as one string (space separated)
    return ' '.join(unique_lines)