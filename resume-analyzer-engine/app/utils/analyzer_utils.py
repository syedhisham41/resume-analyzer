# app/utils/analyzer_utils.py

from app.core.embeddings import embedding_model, check_cosine_scores
from app.core.synonyms import *

import torch
import re

def normalize_text(text: str) -> str:
    """Lowercase and strip punctuation/whitespace."""
    text = text.lower()
    text = re.sub(r'[^\w\s]', '', text)
    text = re.sub(r'\s+', ' ', text)
    return text.strip()

# def normalize_text(text: str) -> str:
#     """Normalize text for robust semantic and regex matching."""
#     if not text:
#         return ""
#     text = text.lower().strip()
#     # Keep + and / and - for tech terms like c++, ci/cd, cross-platform
#     text = re.sub(r'[^a-z0-9\s/\+\-]', '', text)
#     text = re.sub(r'\s+', ' ', text)  # collapse extra spaces
#     return text

# Step 1: Normalize everything (as you already do)
NORMALIZED_SKILL_SYNONYMS = {
    normalize_text(key): [normalize_text(s) for s in syns] + [normalize_text(key)]
    for key, syns in SKILL_SYNONYMS.items()
}

# Step 2: Build reverse lookup (synonym â canonical)
REVERSE_SYNONYM_MAP = {}
for canonical, variants in NORMALIZED_SKILL_SYNONYMS.items():
    for variant in variants:
        REVERSE_SYNONYM_MAP[variant] = canonical

def expand_synonyms(skill: str) -> list:
    """Return all synonyms that share the same canonical group as this skill."""
    norm = normalize_text(skill)
    canonical = REVERSE_SYNONYM_MAP.get(norm, norm)
    return NORMALIZED_SKILL_SYNONYMS.get(canonical, [canonical])

def expand_verb_synonyms(verb_phrase: str) -> list:
    """Return verb phrase variants using known verb synonyms."""
    variants = [verb_phrase]
    for verb, synonyms in VERB_SYNONYMS.items():
        if verb in verb_phrase:
            for syn in synonyms:
                variants.append(verb_phrase.replace(verb, syn))
    return variants

def semantic_match(jd_item, resume_list, embedding_model, threshold=0.8):
    """Safe semantic match using embeddings. Returns the matching item or None."""
    if not resume_list:
        return None  # nothing to compare

    jd_embedding = embedding_model.encode([jd_item], convert_to_tensor=True)
    resume_embeddings = embedding_model.encode(resume_list, convert_to_tensor=True)

    if resume_embeddings.shape[0] == 0:  # extra safety
        return None

    cosine_scores = check_cosine_scores(jd_embedding, resume_embeddings)
    best_score, idx = torch.max(cosine_scores, dim=1)

    if best_score.item() >= threshold:
        return resume_list[idx.item()]
    return None

#working code// donot remove accidentally

# def match_skills(jd_skills, resume_skills, embedding_model, threshold=0.8):
#     matched_skills = set()
#     unmatched_skills = set()

#     # Normalize skills
#     normalized_resume = set(normalize_text(s) for s in resume_skills)
#     normalized_jd = set(normalize_text(s) for s in jd_skills)

#     # Synonym-based match
#     for jd_skill in normalized_jd:
#         jd_syns = expand_synonyms(jd_skill)
#         match_found = False
#         for resume_skill in normalized_resume:
#             resume_syns = expand_synonyms(resume_skill)
#             if set(jd_syns) & set(resume_syns):
#                 match_found = True
#                 matched_skills.add(jd_skill)
#                 break
#         if not match_found:
#             unmatched_skills.add(jd_skill)

#     # Embedding-based semantic match
#     still_unmatched = set()
#     for jd_skill in unmatched_skills:
#         sem_match = semantic_match(jd_skill, list(normalized_resume), embedding_model, threshold)
#         if sem_match:
#             matched_skills.add(jd_skill)
#         else:
#             still_unmatched.add(jd_skill)

#     return matched_skills, still_unmatched

def match_skills(jd_skills, resume_skills, embedding_model, 
                          threshold_full=0.75, threshold_partial=0.3):
    """
    Returns:
        matched_skills: dict {jd_skill: weight (0â1)}
        unmatched_skills: set of jd_skills not matched
    """
    matched_skills = {}
    unmatched_skills = set()

    # Normalize
    jd_norm = [normalize_text(s) for s in jd_skills]
    resume_norm = [normalize_text(s) for s in resume_skills]

    # 1. Synonym/exact match â full weight
    for jd in jd_norm:
        jd_syns = expand_synonyms(jd)
        if any(r in jd_syns or jd in r for r in resume_norm):
            matched_skills[jd] = 1.0
        else:
            unmatched_skills.add(jd)

    # 2. Semantic match with weighting
    unmatched_list = list(unmatched_skills)
    if unmatched_list and resume_norm:
        jd_emb = embedding_model.encode(unmatched_list, convert_to_tensor=True)
        resume_emb = embedding_model.encode(resume_norm, convert_to_tensor=True)
        cosine_scores = check_cosine_scores(jd_emb, resume_emb)

        for i, jd_skill in enumerate(unmatched_list):
            best_score, _ = torch.max(cosine_scores[i], dim=0)
            score = best_score.item()
            if score >= threshold_full:
                matched_skills[jd_skill] = 1.0
            elif score >= threshold_partial:
                weight = (score - threshold_partial) / (threshold_full - threshold_partial)
                matched_skills[jd_skill] = round(weight, 2)
            else:
                matched_skills[jd_skill] = 0.0

    unmatched_skills = {s for s, w in matched_skills.items() if w == 0.0}
    return matched_skills, unmatched_skills

def semantic_match_with_score(jd_item, resume_list, embedding_model):
    """Returns best match from resume_list and its similarity score (0â1)."""
    if not resume_list:
        return None, 0.0  # nothing to compare

    jd_embedding = embedding_model.encode([jd_item], convert_to_tensor=True)
    resume_embeddings = embedding_model.encode(resume_list, convert_to_tensor=True)

    if resume_embeddings.shape[0] == 0:  # safety
        return None, 0.0

    cosine_scores = check_cosine_scores(jd_embedding, resume_embeddings)
    best_score, idx = torch.max(cosine_scores, dim=1)
    return resume_list[idx.item()], best_score.item()


def match_titles(jd_titles, resume_titles, embedding_model, threshold=0.75):
    """
    Match JD titles to resume titles using semantic similarity.
    Returns overall title match pct (weighted) between 0â1.
    """
    if not jd_titles or not resume_titles:
        return 0.0  # nothing to match

    normalized_resume = [normalize_text(s) for s in resume_titles]
    normalized_jd = [normalize_text(s) for s in jd_titles]

    weighted_score_sum = 0.0

    for jd_title in normalized_jd:
        # exact or substring match
        exact_match = False
        for res_title in normalized_resume:
            if jd_title == res_title or jd_title in res_title or res_title in jd_title:
                weighted_score_sum += 1.0
                exact_match = True
                break
        if exact_match:
            continue

        # semantic match
        _, score = semantic_match_with_score(jd_title, normalized_resume, embedding_model)
        if score >= threshold:
            weighted_score_sum += 1.0  # full weight
        elif score > 0:
            weighted_score_sum += score / threshold  # proportional weight

    title_match_pct = weighted_score_sum / len(normalized_jd)
    return min(title_match_pct, 1.0)  # clamp to 1

def match_verb_phrases(jd_verbs, resume_verbs, threshold_full=0.55, threshold_partial=0.25):
    """
    Matches JD verb phrases with resume verb phrases using:
    1. Exact match / synonym expansion (full weight)
    2. Embedding-based semantic similarity with partial weighting
       - score >= threshold_full -> weight 1
       - threshold_partial <= score < threshold_full -> scaled weight
       - score < threshold_partial -> weight 0
       
    Returns:
        matched_verbs: dict {jd_verb: weight}
        unmatched_verbs: set of jd_verbs with zero weight
    """

    matched_verbs = {}
    unmatched_verbs = set()

    # normalize
    jd_verbs_norm = [normalize_text(v) for v in jd_verbs]
    resume_verbs_norm = [normalize_text(v) for v in resume_verbs]

    # -----------------------------
    # 1. Exact / synonym match
    # -----------------------------
    for jd_v in jd_verbs_norm:
        variants = expand_verb_synonyms(jd_v)
        found = False
        for variant in variants:
            if any(variant in r for r in resume_verbs_norm):
                matched_verbs[jd_v] = 1.0  # full weight
                found = True
                break
        if not found:
            unmatched_verbs.add(jd_v)

    # -----------------------------
    # 2. Embedding-based semantic match
    # -----------------------------
    unmatched_list = list(unmatched_verbs)
    if unmatched_list and resume_verbs_norm:
        unmatched_embeddings = embedding_model.encode(unmatched_list, convert_to_tensor=True)
        resume_embeddings = embedding_model.encode(resume_verbs_norm, convert_to_tensor=True)

        if unmatched_embeddings.shape[0] > 0 and resume_embeddings.shape[0] > 0:
            cosine_scores = check_cosine_scores(unmatched_embeddings, resume_embeddings)

            for i, jd_v in enumerate(unmatched_list):
                best_score, _ = torch.max(cosine_scores[i], dim=0)
                score = best_score.item()

                if score >= threshold_full:
                    matched_verbs[jd_v] = 1.0
                elif score >= threshold_partial:
                    # scale weight linearly between threshold_partial and threshold_full
                    scaled_weight = (score - threshold_partial) / (threshold_full - threshold_partial)
                    matched_verbs[jd_v] = scaled_weight
                else:
                    # similarity too low, keep as unmatched
                    unmatched_verbs.add(jd_v)

    # remove any matched verbs from unmatched set
    unmatched_verbs -= set(matched_verbs.keys())

    return matched_verbs, unmatched_verbs

QUAL_LOOKUP = {}
for canon, syns in QUALIFICATION_SYNONYMS.items():
    for s in syns:
        QUAL_LOOKUP[s.lower()] = canon.lower()
        
def normalize_qualification(q: str) -> str:
    q = q.lower().strip()
    return QUAL_LOOKUP.get(q, q)  # fall back to itself if not mapped

def match_qualifications(jd_quals, resume_quals):
    jd_norm = [normalize_qualification(q) for q in jd_quals]
    resume_norm = [normalize_qualification(q) for q in resume_quals]
    
    matched, unmatched = [], []
    for jd_q in jd_norm:
        if jd_q in resume_norm:
            matched.append(jd_q)
        else:
            unmatched.append(jd_q)

    match_pct = len(matched) / max(len(jd_norm), 1)
    return matched, unmatched, match_pct

def compute_overall_fit(
    skill_match_pct: float,
    verb_match_pct: float,
    title_match_pct: float,
    qual_match_pct: float = None,
    weights: dict = None
) -> float:
    """
    Compute overall fit score for resume vs JD with dynamic weighting.
    Ignores title/qualification weight if JD does not specify them.
    """
    default_weights = {
        "skills": 0.55,
        "verbs": 0.25,
        "title": 0.1,
        "qualifications": 0.1
    }
    if weights is None:
        weights = default_weights

    active_weights = weights.copy()
    if qual_match_pct is None:
        active_weights.pop("qualifications")
    total_weight = sum(active_weights.values())

    overall_fit = (
        skill_match_pct * active_weights.get("skills", 0) +
        verb_match_pct * active_weights.get("verbs", 0) +
        title_match_pct * active_weights.get("title", 0) +
        (qual_match_pct or 0) * active_weights.get("qualifications", 0)
    )

    # normalize
    overall_fit /= total_weight
    return round(overall_fit, 2)
