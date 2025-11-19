# app/routers/analyzer.py
from fastapi import APIRouter
from pydantic import BaseModel
from app.services.resume_analyzer_service import ResumeAnalyzerService

router = APIRouter()
analyzer_service = ResumeAnalyzerService()

class AnalyzeRequest(BaseModel):
    jd_text: str
    resume_text: str

@router.post("/analyze")
def analyze_jd_resume_text(request: AnalyzeRequest):
    jd_text = request.jd_text
    resume_text = request.resume_text

    # Optional: clean up text if needed
    # jd_text = clean_jd_text(jd_text)
    # resume_text = clean_jd_text(resume_text)

    result = analyzer_service.analyze(jd_text, resume_text)
    return result