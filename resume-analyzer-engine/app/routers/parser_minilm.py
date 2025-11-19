from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from app.core.embeddings import embedding_model, check_cosine_scores

router = APIRouter()


# ---------------------
# Pydantic Models
# ---------------------
class SentencesInput(BaseModel):
    sentences: list[str]


class SimilarityResponse(BaseModel):
    similarity: float


class EmbeddingResponse(BaseModel):
    embeddings: list[list[float]]


# ---------------------
# Helper function
# ---------------------
def encode_sentences(sentences: list[str], to_tensor: bool = True):
    """Encode sentences into embeddings using shared MiniLM model."""
    return embedding_model.encode(sentences, convert_to_tensor=to_tensor)


# ---------------------
# Endpoints
# ---------------------
@router.post("/similarity", response_model=SimilarityResponse)
async def calculate_similarity(input_data: SentencesInput):
    """
    Calculate cosine similarity between two sentences.
    Input JSON: { "sentences": ["text1", "text2"] }
    """
    if len(input_data.sentences) != 2:
        raise HTTPException(status_code=400, detail="Provide exactly 2 sentences.")

    embeddings = encode_sentences(input_data.sentences)
    similarity_score = check_cosine_scores(embeddings[0], embeddings[1]).item()
    
    return SimilarityResponse(similarity=float(similarity_score))


@router.post("/embedding", response_model=EmbeddingResponse)
async def get_embeddings(input_data: SentencesInput):
    """
    Generate embeddings for input sentences.
    Input JSON: { "sentences": ["text1", "text2", ...] }
    """
    embeddings = encode_sentences(input_data.sentences)
    embeddings_list = [emb.tolist() for emb in embeddings]

    return EmbeddingResponse(embeddings=embeddings_list)
