import time
from app.utils.parser_utils import parsed_skills, parsed_title, parsed_jd_qualification, parsed_resume_qualification
from app.utils.analyzer_utils import match_skills, match_titles, match_verb_phrases, match_qualifications, compute_overall_fit, normalize_text
from app.core.nlp import nlp_processor
from app.core.embeddings import embedding_model

class ResumeAnalyzerService:
    def __init__(self, embedding_model=embedding_model, nlp_processor=nlp_processor):
        self.embedding_model = embedding_model
        self.nlp_processor = nlp_processor

    def parse_document(self, text: str) -> dict:
        """Parse text once and store Doc + noun_chunks"""
        doc = self.nlp_processor.nlp(text)
        noun_chunks = [c.text for c in doc.noun_chunks]
        return {"doc": doc, "text": text, "noun_chunks": noun_chunks}

    def analyze(self, jd_text: str, resume_text: str) -> dict:
        timings = {}
        start_total = time.time()

        # -----------------------------
        # Step 0: Parse documents once
        # -----------------------------
        t0 = time.time()
        jd_doc = self.parse_document(jd_text)
        resume_doc = self.parse_document(resume_text)
        timings['parse_document'] = time.time() - t0

        # -----------------------------
        # Step 1: Extract skills
        # -----------------------------
        t1 = time.time()
        jd_skills = parsed_skills(jd_doc)
        resume_skills = parsed_skills(resume_doc)
        timings['parse_skills'] = time.time() - t1

        # -----------------------------
        # Step 2: Extract titles
        # -----------------------------
        t2 = time.time()
        jd_titles = parsed_title(jd_doc)
        resume_titles = parsed_title(resume_doc)
        timings['parse_titles'] = time.time() - t2

        # -----------------------------
        # Step 3: Extract qualifications
        # -----------------------------
        t3 = time.time()
        jd_quals = parsed_jd_qualification(jd_doc)
        resume_quals = parsed_resume_qualification(resume_doc)
        timings['parse_qualifications'] = time.time() - t3

        # -----------------------------
        # Step 4: Extract verb phrases
        # -----------------------------
        t4 = time.time()
        jd_verbs = self.nlp_processor.extract_verb_phrases(jd_doc["doc"]).get("verb_phrases_clean", [])
        resume_verbs = self.nlp_processor.extract_verb_phrases(resume_doc["doc"]).get("verb_phrases_clean", [])
        timings['extract_verbs'] = time.time() - t4

        # -----------------------------
        # Step 5: Match skills
        # -----------------------------
        t5 = time.time()
        matched_skills, unmatched_skills = match_skills(jd_skills, resume_skills, self.embedding_model)
        timings['match_skills'] = time.time() - t5

        # -----------------------------
        # Step 6: Match verb phrases
        # -----------------------------
        t6 = time.time()
        matched_verbs, unmatched_verbs = match_verb_phrases(jd_verbs, resume_verbs)
        timings['match_verbs'] = time.time() - t6

        # -----------------------------
        # Step 7: Match titles
        # -----------------------------
        t7 = time.time()
        title_match_pct = match_titles(jd_titles, resume_titles, self.embedding_model)
        timings['match_titles'] = time.time() - t7

        # -----------------------------
        # Step 8: Match qualifications
        # -----------------------------
        t8 = time.time()
        matched_quals, unmatched_quals, qual_match_pct = [],[],0.0
        if len(jd_quals) != 0:
            matched_quals, unmatched_quals, qual_match_pct = match_qualifications(jd_quals, resume_quals)
        timings['match_qualifications'] = time.time() - t8

        # -----------------------------
        # Step 9: Compute percentages
        # -----------------------------
        #skill_match_pct = len(matched_skills) / max(len(jd_skills), 1)
        #skill_match_pct = sum(matched_skills.values()) / max(len(jd_skills), 1) if jd_skills else 0
        skill_match_pct = sum(matched_skills.values()) / max(len(set(map(normalize_text, jd_skills))), 1)
        
        verb_match_pct = sum(matched_verbs.values()) / max(len(jd_verbs), 1) if jd_verbs else 0
        
        if qual_match_pct : 
            overall_fit = compute_overall_fit(skill_match_pct, verb_match_pct, title_match_pct, qual_match_pct)
        else :
            overall_fit = compute_overall_fit(skill_match_pct, verb_match_pct, title_match_pct)
            

        timings['total'] = time.time() - start_total

        # -----------------------------
        # Step 10: Prepare results
        # -----------------------------
        result = {
            #"matched_skills": sorted(list(matched_skills)),
            "jd_skills": sorted(list(jd_skills)),
            "resume_skills": sorted(list(resume_skills)),
            "matched_skills":{k: round(v, 2) for k, v in matched_skills.items()},
            "unmatched_skills": sorted(list(unmatched_skills)),
            "jd_verbs" : sorted(list(jd_verbs)),
            "resume_verbs" : sorted(list(resume_verbs)),
            "matched_verb_phrases": {k: round(v, 2) for k, v in matched_verbs.items()},
            "unmatched_verb_phrases": sorted(list(unmatched_verbs)),
            "jd_titles": jd_titles,
            "resume_titles": resume_titles,
            "jd_qualifications": jd_quals,
            "resume_qualifications": resume_quals,
            "matched_qualifications": matched_quals,
            "unmatched_qualifications": unmatched_quals,
            "skill_match_pct": round(skill_match_pct, 2),
            "verb_match_pct": round(verb_match_pct, 2),
            "title_match_pct": round(title_match_pct, 2),
            "qual_match_pct": round(qual_match_pct, 2),
            "overall_fit": overall_fit,
            "timings": {k: round(v, 4) for k, v in timings.items()},
        }

        return result
