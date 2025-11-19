# app/utils/nlp_utils.py
import spacy
import re

from app.core.synonyms import *


class NLPProcessor:
    def __init__(self, spacy_model="en_core_web_md"):
        # Load spaCy model
        self.nlp = spacy.load(spacy_model)
    
    # old logic : working 
    
    # def extract_verb_phrases(self, text_or_doc):
    #     """
    #     Optimized version of extract_verb_phrases:
    #     - Reuses SpaCy Doc objects to avoid repeated parsing
    #     - Disables unnecessary components (like NER)
    #     - Combines filtering and lemmatization in a single pass
    #     """
    #     # Accept either raw text or a pre-parsed Doc
    #     doc = text_or_doc if hasattr(text_or_doc, "sents") else self.nlp(text_or_doc)

    #     skip_words = {"a", "an", "the", "as", "he", "she", "will", "be", "to", "with", "for", "you"}
    #     section_headers = {"skills", "key responsibilities", "technical requirements", "desired skills"}
    #     filler_phrases = {
    #         "interested candidate", "apply here", "looking for immediate", "greeting of the day",
    #         "hiring for", "we are seeking", "in this role", "you will"
    #     }

    #     # Prepare verb lookup
    #     verb_lookup = set(VERB_SYNONYMS.keys())
    #     for syns in VERB_SYNONYMS.values():
    #         verb_lookup.update(syns)

    #     # ---------------------
    #     # Step 1: Extract sentences and clean
    #     # ---------------------
    #     raw_phrases = []
    #     for sent in doc.sents:
    #         sent_text = sent.text.strip()
    #         if "tech stack" in sent_text.lower():
    #             sent_text = sent_text.split("tech stack")[0].strip()
    #         if ":" in sent_text:
    #             sent_text = sent_text.split(":")[0].strip()
    #         sent_text = " ".join(sent_text.split())
    #         if len(sent_text.split()) >= 3:
    #             raw_phrases.append(sent_text)

    #     # ---------------------
    #     # Step 2: Clause splitting and Doc caching
    #     # ---------------------
    #     doc_cache = {}  # phrase -> Doc
    #     processed_phrases = []

    #     def get_doc(phrase):
    #         if phrase not in doc_cache:
    #             doc_cache[phrase] = self.nlp(phrase)
    #         return doc_cache[phrase]

    #     def is_valid_clause(clause_doc):
    #         verbs = [t for t in clause_doc if t.pos_ == "VERB" and t.lemma_.lower() in verb_lookup]
    #         has_object = any(t.dep_ in ("dobj", "pobj", "attr", "nsubj") for t in clause_doc)
    #         return len(clause_doc) >= 3 and bool(verbs) and has_object

    #     for phrase in raw_phrases:
    #         phrase = re.sub(r'^(as (he|she) will be\s+)', '', phrase, flags=re.I)
    #         tokens_doc = get_doc(phrase)
    #         tokens = list(tokens_doc)
    #         num_verbs = sum(1 for t in tokens if t.pos_ == "VERB" and t.lemma_.lower() in verb_lookup)

    #         if "and" in [t.text.lower() for t in tokens] and num_verbs > 1:
    #             start_idx = 0
    #             for i, tok in enumerate(tokens):
    #                 if tok.text.lower() == "and" and i + 1 < len(tokens):
    #                     next_tok = tokens[i + 1]
    #                     if next_tok.pos_ == "VERB" and next_tok.lemma_.lower() in verb_lookup:
    #                         subphrase = " ".join([t.text for t in tokens[start_idx:i]]).strip(" ,")
    #                         if subphrase:
    #                             sub_doc = get_doc(subphrase)
    #                             if is_valid_clause(sub_doc):
    #                                 processed_phrases.append(subphrase)
    #                         start_idx = i + 1
    #             subphrase = " ".join([t.text for t in tokens[start_idx:]]).strip(" ,")
    #             if subphrase:
    #                 sub_doc = get_doc(subphrase)
    #                 if is_valid_clause(sub_doc):
    #                     processed_phrases.append(subphrase)
    #         else:
    #             if is_valid_clause(tokens_doc):
    #                 processed_phrases.append(phrase)

    #     # ---------------------
    #     # Step 3: Split collaborator lists
    #     # ---------------------
    #     split_phrases = []
    #     for phrase in processed_phrases:
    #         match = re.match(r'(collaborate with )(.+)', phrase, flags=re.I)
    #         if match:
    #             prefix, rest = match.groups()
    #             parts = re.split(r',|\band\b', rest)
    #             for part in parts:
    #                 part = part.strip()
    #                 if part:
    #                     split_phrases.append(f"{prefix}{part}")
    #         else:
    #             split_phrases.append(phrase)

    #     # ---------------------
    #     # Step 4 & 5: Filtering + lemmatization in one pass
    #     # ---------------------
    #     cleaned = []
    #     cleaned_lemmatized = []
    #     for phrase in split_phrases:
    #         lower = phrase.lower()
    #         if any(lower.startswith(x) for x in section_headers):
    #             continue
    #         if any(x in lower for x in filler_phrases):
    #             continue

    #         clause_doc = get_doc(phrase)
    #         first_word = next((t.lemma_.lower() for t in clause_doc if t.lower_ not in skip_words), None)
    #         if not first_word or first_word not in verb_lookup:
    #             continue

    #         cleaned.append(" ".join(phrase.split()))
    #         lemmas = [t.lemma_.lower() for t in clause_doc if t.pos_ in {"VERB", "NOUN"}]
    #         if len(lemmas) >= 2:
    #             cleaned_lemmatized.append(" ".join(lemmas))

    #     # ---------------------
    #     # Step 6: Return results
    #     # ---------------------
    #     final_phrases = sorted(set(cleaned))
    #     final_phrases_clean = sorted(set(cleaned_lemmatized))

    #     return {
    #         "verb_phrases": final_phrases,
    #         "verb_phrases_clean": final_phrases_clean,
    #     }
    
    #old logic , enhanced splitting :
    
    def extract_verb_phrases(self, text_or_doc):
        """
        Enhanced version:
        - Preserves all existing logic and filters
        - Adds finer sub-phrase splitting for better semantic matching
        - Keeps valid context while breaking multi-action clauses
        """
        doc = text_or_doc if hasattr(text_or_doc, "sents") else self.nlp(text_or_doc)

        skip_words = {"a", "an", "the", "as", "he", "she", "will", "be", "to", "with", "for", "you"}
        section_headers = {"skills", "key responsibilities", "technical requirements", "desired skills"}
        filler_phrases = {
            "interested candidate", "apply here", "looking for immediate", "greeting of the day",
            "hiring for", "we are seeking", "in this role", "you will"
        }

        # Verb lookup
        verb_lookup = set(VERB_SYNONYMS.keys())
        for syns in VERB_SYNONYMS.values():
            verb_lookup.update(syns)

        # ---------------------
        # Step 1: Sentence extraction
        # ---------------------
        raw_phrases = []
        for sent in doc.sents:
            sent_text = sent.text.strip()
            if "tech stack" in sent_text.lower():
                sent_text = sent_text.split("tech stack")[0].strip()
            if ":" in sent_text:
                sent_text = sent_text.split(":")[0].strip()
            sent_text = " ".join(sent_text.split())
            if len(sent_text.split()) >= 3:
                raw_phrases.append(sent_text)

        # ---------------------
        # Step 2: Clause splitting (existing logic)
        # ---------------------
        doc_cache = {}
        processed_phrases = []

        def get_doc(phrase):
            if phrase not in doc_cache:
                doc_cache[phrase] = self.nlp(phrase)
            return doc_cache[phrase]

        def is_valid_clause(clause_doc):
            verbs = [t for t in clause_doc if t.pos_ == "VERB" and t.lemma_.lower() in verb_lookup]
            has_object = any(t.dep_ in ("dobj", "pobj", "attr", "nsubj") for t in clause_doc)
            return len(clause_doc) >= 3 and bool(verbs) and has_object

        for phrase in raw_phrases:
            phrase = re.sub(r'^(as (he|she) will be\s+)', '', phrase, flags=re.I)
            tokens_doc = get_doc(phrase)
            tokens = list(tokens_doc)
            num_verbs = sum(1 for t in tokens if t.pos_ == "VERB" and t.lemma_.lower() in verb_lookup)

            if "and" in [t.text.lower() for t in tokens] and num_verbs > 1:
                start_idx = 0
                for i, tok in enumerate(tokens):
                    if tok.text.lower() == "and" and i + 1 < len(tokens):
                        next_tok = tokens[i + 1]
                        if next_tok.pos_ == "VERB" and next_tok.lemma_.lower() in verb_lookup:
                            subphrase = " ".join([t.text for t in tokens[start_idx:i]]).strip(" ,")
                            if subphrase:
                                sub_doc = get_doc(subphrase)
                                if is_valid_clause(sub_doc):
                                    processed_phrases.append(subphrase)
                            start_idx = i + 1
                subphrase = " ".join([t.text for t in tokens[start_idx:]]).strip(" ,")
                if subphrase:
                    sub_doc = get_doc(subphrase)
                    if is_valid_clause(sub_doc):
                        processed_phrases.append(subphrase)
            else:
                if is_valid_clause(tokens_doc):
                    processed_phrases.append(phrase)

        # ---------------------
        # â Step 2.5: Smart sub-phrase splitting (context-preserving)
        # ---------------------
        refined_phrases = []
        split_tokens = {"and", "or", ",", ";"}

        for phrase in processed_phrases:
            clause_doc = get_doc(phrase)
            current = []
            for i, tok in enumerate(clause_doc):
                # Split only when next token is a verb start
                if tok.text.lower() in split_tokens and i + 1 < len(clause_doc):
                    next_tok = clause_doc[i + 1]
                    if next_tok.pos_ == "VERB" and next_tok.lemma_.lower() in verb_lookup:
                        # Check current sub-phrase validity
                        sub = " ".join(t.text for t in current).strip(" ,;")
                        if sub:
                            sub_doc = get_doc(sub)
                            if is_valid_clause(sub_doc):
                                refined_phrases.append(sub)
                        current = []
                        continue
                current.append(tok)

            # Add final part
            sub = " ".join(t.text for t in current).strip(" ,;")
            if sub:
                sub_doc = get_doc(sub)
                if is_valid_clause(sub_doc):
                    refined_phrases.append(sub)

        processed_phrases = refined_phrases

        # ---------------------
        # Step 3: Split collaborator lists
        # ---------------------
        split_phrases = []
        for phrase in processed_phrases:
            match = re.match(r'(collaborate with )(.+)', phrase, flags=re.I)
            if match:
                prefix, rest = match.groups()
                parts = re.split(r',|\band\b', rest)
                for part in parts:
                    part = part.strip()
                    if part:
                        split_phrases.append(f"{prefix}{part}")
            else:
                split_phrases.append(phrase)

        # ---------------------
        # Step 4â5: Filtering + Lemmatization
        # ---------------------
        cleaned = []
        cleaned_lemmatized = []
        for phrase in split_phrases:
            lower = phrase.lower()
            if any(lower.startswith(x) for x in section_headers):
                continue
            if any(x in lower for x in filler_phrases):
                continue

            clause_doc = get_doc(phrase)
            first_word = next((t.lemma_.lower() for t in clause_doc if t.lower_ not in skip_words), None)
            if not first_word or first_word not in verb_lookup:
                continue

            cleaned.append(" ".join(phrase.split()))
            lemmas = [t.lemma_.lower() for t in clause_doc if t.pos_ in {"VERB", "NOUN"}]
            if len(lemmas) >= 2:
                cleaned_lemmatized.append(" ".join(lemmas))

        # ---------------------
        # Step 6: Return
        # ---------------------
        final_phrases = sorted(set(cleaned))
        final_phrases_clean = sorted(set(cleaned_lemmatized))

        return {
            "verb_phrases": final_phrases,
            "verb_phrases_clean": final_phrases_clean,
        }

    
    # # new hybrid logic 
    
    # def extract_verb_phrases(self, text_or_doc):
    #     """
    #     Hybrid verb phrase extractor:
    #     - Combines old logic (cohesive clauses) with new logic (short verb-object phrases)
    #     - Expands corporate/auxiliary verbs
    #     - Produces both full phrases and lemmatized clean phrases
    #     """

    #     doc = text_or_doc if hasattr(text_or_doc, "sents") else self.nlp(text_or_doc)

    #     skip_words = {"a", "an", "the", "as", "he", "she", "will", "be", "to", "with", "for", "you"}
    #     section_headers = {"skills", "key responsibilities", "technical requirements", "desired skills"}
    #     filler_phrases = {
    #         "interested candidate", "apply here", "looking for immediate", "greeting of the day",
    #         "hiring for", "we are seeking", "in this role", "you will"
    #     }

    #     # Expand verb lookup
    #     verb_lookup = set(VERB_SYNONYMS.keys())
    #     for syns in VERB_SYNONYMS.values():
    #         verb_lookup.update(syns)
    #     # Add corporate/auxiliary verbs
    #     corporate_verbs = {"automate", "utilize", "ensure", "apply", "execute", "deliver", "leverage", "adhere"}
    #     verb_lookup.update(corporate_verbs)

    #     raw_phrases = []
    #     for sent in doc.sents:
    #         sent_text = sent.text.strip()
    #         if "tech stack" in sent_text.lower():
    #             sent_text = sent_text.split("tech stack")[0].strip()
    #         if ":" in sent_text:
    #             sent_text = sent_text.split(":")[0].strip()
    #         sent_text = " ".join(sent_text.split())
    #         if len(sent_text.split()) >= 2:
    #             raw_phrases.append(sent_text)

    #     doc_cache = {}
    #     def get_doc(phrase):
    #         if phrase not in doc_cache:
    #             doc_cache[phrase] = self.nlp(phrase)
    #         return doc_cache[phrase]

    #     processed_phrases = []

    #     # Clause splitting like old logic + short verb-object capture
    #     for phrase in raw_phrases:
    #         phrase_doc = get_doc(phrase)
    #         tokens = list(phrase_doc)
    #         # Old logic: split on "and" with multiple verbs
    #         if "and" in [t.text.lower() for t in tokens] and sum(1 for t in tokens if t.pos_=="VERB" and t.lemma_.lower() in verb_lookup) > 1:
    #             start_idx = 0
    #             for i, tok in enumerate(tokens):
    #                 if tok.text.lower() == "and" and i+1 < len(tokens):
    #                     next_tok = tokens[i+1]
    #                     if next_tok.pos_=="VERB" and next_tok.lemma_.lower() in verb_lookup:
    #                         subphrase = " ".join([t.text for t in tokens[start_idx:i]]).strip(" ,")
    #                         if subphrase:
    #                             processed_phrases.append(subphrase)
    #                         start_idx = i+1
    #             subphrase = " ".join([t.text for t in tokens[start_idx:]]).strip(" ,")
    #             if subphrase:
    #                 processed_phrases.append(subphrase)
    #         else:
    #             processed_phrases.append(phrase)

    #         # New logic addition: short verb-object phrases (>=2 tokens, verb + noun/propn)
    #         verbs = [t for t in tokens if t.pos_=="VERB" and t.lemma_.lower() in verb_lookup]
    #         objects = [t for t in tokens if t.dep_ in {"dobj","pobj","attr","nsubj"} or t.pos_ in {"NOUN","PROPN"}]
    #         if verbs and objects and len(tokens)>=2:
    #             processed_phrases.append(" ".join([t.text for t in tokens]))

    #     # Split collaborator phrases smartly
    #     split_phrases = []
    #     for phrase in processed_phrases:
    #         match = re.match(r'(collaborate with )(.+)', phrase, flags=re.I)
    #         if match:
    #             prefix, rest = match.groups()
    #             parts = re.split(r',|\band\b', rest)
    #             for part in parts:
    #                 part = part.strip()
    #                 if part:
    #                     split_phrases.append(f"{prefix}{part}")
    #         else:
    #             split_phrases.append(phrase)

    #     # Filter + lemmatize
    #     final_phrases = []
    #     final_phrases_clean = []
    #     for phrase in split_phrases:
    #         lower = phrase.lower()
    #         if any(lower.startswith(x) for x in section_headers):
    #             continue
    #         if any(x in lower for x in filler_phrases):
    #             continue

    #         clause_doc = get_doc(phrase)
    #         first_word = next((t.lemma_.lower() for t in clause_doc if t.lower_ not in skip_words), None)
    #         if not first_word or first_word not in verb_lookup:
    #             continue

    #         final_phrases.append(" ".join(phrase.split()))
    #         lemmas = [t.lemma_.lower() for t in clause_doc if t.pos_ in {"VERB","NOUN","PROPN"}]
    #         if len(lemmas) >= 2:
    #             final_phrases_clean.append(" ".join(lemmas))

    #     return {
    #         "verb_phrases": sorted(set(final_phrases)),
    #         "verb_phrases_clean": sorted(set(final_phrases_clean))
    #     }
        
        
    def extract_noun_chunks(self, text_or_doc):
        """
        Extract noun_chunks from a job description or resume.
        """
        # Accept either raw text or a pre-parsed Doc
        doc = text_or_doc if hasattr(text_or_doc, "sents") else self.nlp(text_or_doc)
        noun_chunks = [chunk.text for chunk in doc.noun_chunks]
        return {"noun_chunks": noun_chunks}

    def lemmatize(self, text):
        """Return a list of (text, pos, lemma) tuples for each token."""
        doc = self.nlp(text)
        return [(t.text, t.pos_, t.lemma_) for t in doc]
    
    
    def extract_entities(self, text_or_doc):
        """
        Extract entities and their labels from a job description or resume.
        """
        # Accept either raw text or a pre-parsed Doc
        doc = text_or_doc if hasattr(text_or_doc, "sents") else self.nlp(text_or_doc)
        entities = [
            {"text": ent.text, "label": ent.label_}
            for ent in doc.ents
        ]
        return {"entities": entities}