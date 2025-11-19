from fastapi import FastAPI
from app.routers import health
from app.routers import parser
from app.routers import parser_minilm
from app.routers import analyzer

app=FastAPI(title="Resume Analyzer ML microservice")

app.include_router(health.router, prefix="/api/health", tags=["health"])
app.include_router(parser.router, prefix="/api/nlp", tags=["nlp"])
app.include_router(parser_minilm.router, prefix="/api/minilm", tags=["minilm"])
app.include_router(analyzer.router, prefix="/api", tags=["analyze"])