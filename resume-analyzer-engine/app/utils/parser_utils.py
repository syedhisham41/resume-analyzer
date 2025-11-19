# app/utils/parser_utils.py
import re
import torch
from sentence_transformers import util
from app.core.nlp import nlp_processor
from app.core.synonyms import SKILL_SYNONYMS, LANGUAGE_CANONICAL
from app.core.embeddings import (
    embedding_model, skills_db_embeddings, skills_db,
    titles_db_embeddings, titles_db,
    qualification_db, qualification_db_embeddings
)


# -----------------------------
# Helper to extract Doc & text
# -----------------------------
def _get_doc_and_text(input_data):
    """
    Accept raw text, spaCy Doc, or parse_document dict.
    Returns (doc, text)
    """
    if isinstance(input_data, dict) and "doc" in input_data and "text" in input_data:
        return input_data["doc"], input_data["text"]
    elif hasattr(input_data, "sents"):  # spaCy Doc
        return input_data, input_data.text
    elif isinstance(input_data, str):
        doc = nlp_processor.nlp(input_data)
        return doc, input_data
    else:
        raise ValueError("Invalid input: must be str, Doc, or parse_document dict.")


# -----------------------------
# Normalization & filtering utils
# -----------------------------
def normalize_chunk(chunk: str) -> str:
    chunk = chunk.replace("â", "'").replace("â", "'")
    chunk = re.sub(r"^(a|an|the)\s+", "", chunk, flags=re.IGNORECASE)
    return chunk.lower().strip()


def filter_languages(skills: list[str], text: str) -> list[str]:
    text_lower = text.lower()
    filtered_skills = []
    for skill in skills:
        skill_lower = skill.lower()
        canonical = LANGUAGE_CANONICAL.get(skill_lower, skill_lower)
        if canonical in LANGUAGE_CANONICAL.values():  # programming language
            if canonical in text_lower:
                filtered_skills.append(canonical)
        else:
            filtered_skills.append(skill)
    return filtered_skills


# -----------------------------
# Skills parser
# -----------------------------
# def parsed_skills(input_data, threshold: float = 0.75):
#     doc, text = _get_doc_and_text(input_data)

#     # Reuse precomputed noun_chunks if available
#     noun_chunks = input_data.get("noun_chunks") if isinstance(input_data, dict) else [chunk.text for chunk in doc.noun_chunks]

#     found_skills = set()
#     normalized_text = text.lower()

#     # Phase 0: Inject SKILL_SYNONYMS manually
#     for skill, synonyms in SKILL_SYNONYMS.items():
#         for variant in [skill] + synonyms:
#             if re.search(rf"\b{re.escape(variant.lower())}\b", normalized_text):
#                 found_skills.add(skill)

#     # Phase 1: Regex match from noun chunks
#     pattern = re.compile(r"\b(" + "|".join(map(re.escape, skills_db)) + r")\b", re.IGNORECASE)
#     matched_chunks = set()
#     for chunk in noun_chunks:
#         matches = pattern.findall(chunk)
#         if matches:
#             found_skills.update(matches)
#             matched_chunks.add(chunk)

#     # Phase 2: MiniLM fallback
#     unmatched_chunks = [c for c in noun_chunks if c not in matched_chunks]
#     if unmatched_chunks:
#         chunk_embeddings = embedding_model.encode(unmatched_chunks, convert_to_tensor=True)
#         cosine_scores = util.cos_sim(chunk_embeddings, skills_db_embeddings)
#         for i, chunk in enumerate(unmatched_chunks):
#             best_score, best_idx = torch.max(cosine_scores[i], dim=0)
#             if best_score >= threshold:
#                 found_skills.add(skills_db[best_idx])

#     # Phase 3: Normalize programming languages
#     return list(filter_languages(list(found_skills), text))

def parsed_skills(input_data, threshold: float = 0.7):
    doc, text = _get_doc_and_text(input_data)
    noun_chunks = (
        input_data.get("noun_chunks")
        if isinstance(input_data, dict)
        else [chunk.text for chunk in doc.noun_chunks]
    )

    found_skills = set()
    normalized_text = text.lower()
    

     # ---- Phase 0: Synonym-based detection (robust multi-word matching) ----
    synonym_to_skill = {}
    for skill, synonyms in SKILL_SYNONYMS.items():
        for term in [skill] + synonyms:
            synonym_to_skill[term.lower().strip()] = skill

    # Sort longer phrases first (so "scalability design" matches before "scalability")
    all_terms = sorted(synonym_to_skill.keys(), key=len, reverse=True)

    # Build regex that matches phrases even with punctuation/spaces between words
    # Example: "scalability design" â will match "scalability design", "scalability-design"
    escaped_terms = []
    for term in all_terms:
        # Escape regex metachars *after* replacing spaces
        escaped = re.sub(r"\s+", lambda m: "[\\s\\-]+", term.strip())  # match any space or dash between words
        escaped = re.escape(escaped).replace(r"\[\s\\\-]\+", r"[\s\-]+")  # reapply the group correctly
        escaped_terms.append(escaped)
    

    pattern_str = r"(?<!\w)(" + "|".join(escaped_terms) + r")(?!\w)"
    #pattern_str = r"\b(" + "|".join(escaped_terms) + r")\b[\.,;:!?]?"
    #pattern_str = r"\b(" + "|".join(escaped_terms) + r")\b"
    #pattern_str = r"(?<!\w)(" + "|".join(escaped_terms) + r")(?!\w|[.,;:!?])"
    synonym_pattern = re.compile(pattern_str, re.IGNORECASE)

    matches = synonym_pattern.findall(normalized_text)
    for match in matches:
        canonical = synonym_to_skill.get(match.lower().strip())
        if canonical:
            found_skills.add(canonical)

    # ---- Phase 1: Regex match from noun chunks ----
    pattern = re.compile(r"\b(" + "|".join(map(re.escape, skills_db)) + r")\b", re.IGNORECASE)
    matched_chunks = set()
    for chunk in noun_chunks:
        matches = pattern.findall(chunk)
        if matches:
            found_skills.update(matches)
            matched_chunks.add(chunk)

    # ---- Phase 2: MiniLM fallback (unchanged) ----
    unmatched_chunks = [c for c in noun_chunks if c not in matched_chunks]
    if unmatched_chunks:
        chunk_embeddings = embedding_model.encode(unmatched_chunks, convert_to_tensor=True)
        cosine_scores = util.cos_sim(chunk_embeddings, skills_db_embeddings)
        for i, chunk in enumerate(unmatched_chunks):
            best_score, best_idx = torch.max(cosine_scores[i], dim=0)
            if best_score >= threshold:
                found_skills.add(skills_db[best_idx])

    # ---- Phase 3: Normalize programming languages ----
    return list(filter_languages(list(found_skills), text))


# -----------------------------
# Title parser
# -----------------------------
def parsed_title(input_data, threshold: float = 0.80) -> list[str]:
    doc, _ = _get_doc_and_text(input_data)
    noun_chunks = input_data.get("noun_chunks") if isinstance(input_data, dict) else [chunk.text for chunk in doc.noun_chunks]

    candidate_chunks = [c for c in noun_chunks if len(c.split()) >= 2]
    found_title = set()

    if candidate_chunks:
        chunk_embeddings = embedding_model.encode(candidate_chunks, convert_to_tensor=True)
        cosine_scores = util.cos_sim(chunk_embeddings, titles_db_embeddings)
        for i, chunk in enumerate(candidate_chunks):
            best_score, best_idx = torch.max(cosine_scores[i], dim=0)
            if best_score >= threshold:
                found_title.add(titles_db[best_idx])

    return list(found_title)


# -----------------------------
# JD Qualification parser
# -----------------------------
def parsed_jd_qualification(input_data, threshold: float = 0.80):
    doc, _ = _get_doc_and_text(input_data)
    noun_chunks = input_data.get("noun_chunks") if isinstance(input_data, dict) else [c.text for c in doc.noun_chunks]
    noun_chunks = [normalize_chunk(c) for c in noun_chunks]

    found_qualification = set()
    norm_db = [normalize_chunk(q) for q in qualification_db]
    pattern = re.compile(r"\b(" + "|".join(map(re.escape, norm_db)) + r")\b", re.IGNORECASE)

    for chunk in noun_chunks:
        matches = pattern.findall(chunk)
        if matches:
            found_qualification.update(matches)

    unmatched_chunks = [c for c in noun_chunks if not any(q in c for q in found_qualification)]
    if unmatched_chunks:
        chunk_embeddings = embedding_model.encode(unmatched_chunks, convert_to_tensor=True)
        cosine_scores = util.cos_sim(chunk_embeddings, qualification_db_embeddings)
        for i, chunk in enumerate(unmatched_chunks):
            best_score, best_idx = torch.max(cosine_scores[i], dim=0)
            if best_score >= threshold:
                found_qualification.add(qualification_db[best_idx])

    return list(found_qualification)


# -----------------------------
# Resume Qualification parser
# -----------------------------
def parsed_resume_qualification(input_data, threshold: float = 0.80):
    _, text = _get_doc_and_text(input_data)
    text_lower = text.lower()

    found_qualification = set()
    for q in qualification_db:
        if re.search(rf"\b{re.escape(q)}\b", text_lower):
            found_qualification.add(q)

    if not found_qualification:
        text_tokens = [t.strip() for t in re.split(r"[.,;\n]", text_lower) if t.strip()]
        token_embeddings = embedding_model.encode(text_tokens, convert_to_tensor=True)
        cosine_scores = util.cos_sim(token_embeddings, qualification_db_embeddings)
        for i, token in enumerate(text_tokens):
            best_score, best_idx = torch.max(cosine_scores[i], dim=0)
            if best_score >= threshold:
                found_qualification.add(qualification_db[best_idx])

    return list(found_qualification)
