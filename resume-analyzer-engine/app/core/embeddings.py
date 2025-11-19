#app/core/embeddings.py
import os
from sentence_transformers import SentenceTransformer, util
from app.utils.loader_utils import load_skills, load_extra_job_titles, load_job_titles, load_qualifications

# Load MiniLM model once
# local_model_path = os.path.expanduser(
#     "~/.cache/huggingface/hub/models--sentence-transformers--all-MiniLM-L6-v2/snapshots/c9745ed1d9f207416be6d2e6f8de32d1f16199bf"
# )
# embedding_model = SentenceTransformer(local_model_path)

local_model_path = os.path.join(os.path.dirname(__file__), "models", "all-MiniLM-L6-v2")
embedding_model = SentenceTransformer(local_model_path)

# Load skills DB
skills_db = load_skills()
skills_db = list(load_skills())

#titles_db = list(load_job_titles().union(load_extra_job_titles()))
titles_db = list(load_extra_job_titles())
qualification_db = list(load_qualifications())

# Pre-compute embeddings for skills DB
skills_db_embeddings = embedding_model.encode(skills_db, convert_to_tensor=True)

titles_db_embeddings = embedding_model.encode(titles_db, convert_to_tensor=True)

qualification_db_embeddings = embedding_model.encode(qualification_db, convert_to_tensor=True)


def check_cosine_scores(embedding1, embedding2):
    return util.cos_sim(embedding1, embedding2)