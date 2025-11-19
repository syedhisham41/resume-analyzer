# remove_duplicates.py

INPUT_FILE = "skills_new.txt"
OUTPUT_FILE = "skills.txt"

def remove_duplicates(input_file: str, output_file: str):
    seen = set()
    unique_lines = []

    with open(input_file, "r", encoding="utf-8") as f:
        for line in f:
            line_clean = line.strip()
            if line_clean and line_clean not in seen:
                seen.add(line_clean)
                unique_lines.append(line_clean)

    with open(output_file, "w", encoding="utf-8") as f:
        f.write("\n".join(unique_lines))

    print(f"â {len(unique_lines)} unique lines saved to {output_file}")

if __name__ == "__main__":
    remove_duplicates(INPUT_FILE, OUTPUT_FILE)
