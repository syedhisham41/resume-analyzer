# app/routers/parser.py
from fastapi import APIRouter
from pydantic import BaseModel
from typing import Callable, Any, Dict
from app.utils.parser_utils import (
    parsed_skills,
    parsed_title,
    parsed_jd_qualification,
    parsed_resume_qualification,
)
from app.utils.cleanup_utils import clean_jd_text
from app.core.nlp import nlp_processor

router = APIRouter()


class TextRequest(BaseModel):
    text: str


# --- Generic handler factory ---
def create_text_endpoint(func: Callable[[str], Any], preprocessor: Callable[[str], str] = None) -> Callable:
    async def endpoint(request: TextRequest) -> Any:
        text = request.text
        if preprocessor:
            text = preprocessor(text)
        return func(text)
    return endpoint


# --- NLP endpoints ---
router.add_api_route("/parse", create_text_endpoint(nlp_processor.extract_verb_phrases), methods=["POST"])
router.add_api_route("/noun_chunks", create_text_endpoint(nlp_processor.extract_noun_chunks), methods=["POST"])
router.add_api_route("/lemmatize", create_text_endpoint(nlp_processor.lemmatize), methods=["POST"])

# --- Parsing utilities ---
router.add_api_route("/parsed_skills", create_text_endpoint(parsed_skills), methods=["POST"])
router.add_api_route("/parsed_title", create_text_endpoint(parsed_title), methods=["POST"])
router.add_api_route(
    "/parsed_jd_qualification",
    create_text_endpoint(parsed_jd_qualification, preprocessor=clean_jd_text),
    methods=["POST"]
)
router.add_api_route(
    "/parsed_resume_qualification",
    create_text_endpoint(parsed_resume_qualification, preprocessor=clean_jd_text),
    methods=["POST"]
)

@router.post("/parse_all")
async def parse_all(request: TextRequest):
    text = request.text
    noun_chunks = nlp_processor.extract_noun_chunks(text).get("noun_chunks")
    parsed_skill = parsed_skills(text)
    entities = nlp_processor.extract_entities(text).get("entities")
    return {
        "noun_chunks": noun_chunks,
        "verb_phrases": nlp_processor.extract_verb_phrases(text).get("verb_phrases_clean"),
        "titles": parsed_title(text),
        "skills": parsed_skill,
        "entities" : entities,
        "noun_chunks_count": len(noun_chunks),
        "parsed_skill_count": len(parsed_skill)
    }
