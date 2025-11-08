# 🧠 Resume Analyzer

**AI-driven Resume Intelligence Platform built with Spring Boot, FastAPI, and NLP — helping job seekers and recruiters bridge the hiring gap through intelligent resume–JD matching, skill extraction, and ATS optimization.**

---

## 🚀 Overview

Resume Analyzer intelligently parses resumes and job descriptions, extracts skills and roles using NLP, and computes a fit score based on embeddings and semantic similarity.  
The system enables job seekers to identify missing keywords, recruiters to shortlist efficiently, and developers to explore real-world microservice architecture with NLP integration.

This project demonstrates clean, modular, production-style design — blending Spring Boot (Java) for backend orchestration and FastAPI (Python) for the NLP/ML microservice — fully Dockerized for easy deployment.

---

## ⚙️ Tech Stack

| Layer | Technology |
|-------|-------------|
| **Backend** | Spring Boot · Spring Data JPA · Spring Security · JWT Auth · MySQL |
| **Frontend/UI** | HTML · CSS · Vanilla JavaScript · Thymeleaf Templates |
| **NLP Service** | Python · FastAPI · SpaCy (md model) · MiniLM Transformers · PyTorch |
| **Parsing** | Apache Tika (PDF/DOCX/Text Parsing) |
| **Architecture** | Microservices · RESTful Communication (HTTP + WebClient) · Clean Modular Layers |
| **DevOps** | Docker · Docker Compose |

---

## 💡 Key Features

### 🧾 Resume & JD Management
- Upload resumes and job descriptions (supports `.pdf`, `.docx`, `.txt`)
- Extract and store text content via Apache Tika
- Persist data in MySQL using Spring Data JPA

### 🧠 NLP-Powered Skill & Role Extraction
- Extract skills, titles, qualifications, and verbs from both resumes and JDs  
- Uses SpaCy (NER, noun-chunking) + MiniLM Transformers for embeddings  
- Computes semantic similarity (via cosine similarity using PyTorch)

### ⚖️ Smart Comparison & Scoring
- Compares extracted data between Resume ↔ JD  
- Generates a Fit Score based on skills, roles, action words and qualifications overlap  
- Identifies missing keywords and improvement areas  
- Generates a PDF report for download

### 👤 User Authentication & Security
- JWT-based authentication using Spring Security  
- Password hashing with BCrypt  
- Role-based access (User/Admin)  
- Custom Security Filter Chains and secured REST endpoints

### 📊 Dashboards & Insights
- **User Dashboard:** manage resumes, job descriptions, and reports  
- JD Dashboard / Resume Dashboard / Analysis Dashboard  
- Recent Activities Page for tracking user actions  
- Interactive charts and visualizations  
- Search and filter by name, company, title, or date  
- Change password, update user details, delete account

### 🎨 UI & Experience
- Built with Thymeleaf, HTML, CSS, and Vanilla JS  
- Clean and responsive layout  
- Modals, alerts, and visualization charts  
- Guest access mode to try the analyzer without signup

---

## 🧩 Architecture Overview

The system follows a **microservices architecture**:

- **Spring Boot Backend** – manages users, resumes, job descriptions, analysis and recent activity workflows.
- **Python FastAPI ML Service** – performs NLP tasks such as keyword extraction, similarity scoring, and skill matching.
- **MySQL Database** – stores resumes, JDs, recent activities, JD candidates and analysis results.
- **Docker** – containerizes both backend and ML service for seamless deployment.

### 🗂 Project Structure

```bash
resume-analyzer/
├── docker-conmpose.yaml                # Docker compose file
├── ml-service/                         # Python NLP Microservice (FastAPI + SpaCy + MiniLM)
│   ├── Dockerfile                      # ML Docker setup
│   ├── clean_text.py                   # Script t cleanup the input text
│   ├── requirements.txt                # ML dependencies
│   └── app/
│       ├── core/
│       │   ├── models/                # ML models (MiniLM embeddings)
│       │   └── __pycache__/
│       ├── resources/                 # Skills.db, verbs.db, synonyms.db, etc.
│       ├── routers/                   # FastAPI routes
│       ├── services/                  # NLP logic, similarity scoring
│       ├── utils/                     # Common helper functions
│       └── __pycache__/
│
└── resume-analyzer/                   # Spring Boot Backend
    ├── src/
    │   ├── main/
    │   │   ├── java/com/resumeanalyzer/
    │   │   │   ├── auth/             # Authentication & JWT modules
    │   │   │   ├── analyzer/         # JD ↔ Resume comparison logic
    │   │   │   ├── resume/           # Resume CRUD and parsing
    │   │   │   ├── jd/               # Job Description CRUD and parsing
    │   │   │   ├── activity/         # Recent activity tracking
    │   │   │   ├── guest/            # Guest mode (no signup)
    │   │   │   ├── ui/               # Thymeleaf view controllers
    │   │   │   └── common/           # Config, Security, Enums, Utilities
    │   │   ├── resources/
    │   │   │   ├── static/           # HTML, CSS, JS, Images
    │   │   │   ├── templates/        # Thymeleaf templates & fragments
    │   │   │   └── db/migration/     # Flyway migrations
    │   └── test/                     # Unit and integration tests
    │
    ├── data/                         # Sample input data
    ├── Dockerfile                    # Backend Docker setup
    └── pom.xml                       # Maven configuration
```
### Communication Flow:
- Spring Boot backend sends HTTP requests to ML microservice using WebClient.  
- ML service performs NLP extraction & similarity computation.  
- Results are returned to backend → persisted in DB → served to UI.

---

## 🧱 Setup Instructions

### 🐳 1. Run with Docker Compose (Recommended)

```bash
# From project root
docker-compose up --build
```
This will spin up:
- resume-analyzer (Java service)
- ml-service (Python FastAPI NLP microservice)
- resume-analyzer-db (database container)

Access the app at http://localhost:8080


### 🧩 2. Manual Run (for developers)
- Start MySQL locally or via Docker
- Run ML Service
```bash
cd ml-service
pip install -r requirements.txt
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

- Run Spring Boot Backend
```bash
cd backend
mvn spring-boot:run
```

Open browser → http://localhost:8080

---

## 📈 Future Roadmap

- 🧭 Enhanced insights with real-time job-market data
- 📬 Email/Slack notifications for analysis reports
- 🧠 Resume gap & red-flag detection (experience consistency)
- 📅 Integration with LinkedIn/GitHub for live profile analysis
- 🧮 Improved Fit Score algorithm using BERT-based embeddings
- 🌐 React Frontend migration for a modern SPA interface

---

## 🤝 Contributions

Contributions, issues, and feature requests are welcome!
Feel free to fork this repo and submit pull requests.

---

## 🪪 License

This project is licensed under the **MIT License** — see the [LICENSE](./LICENSE) file for details.

---

## ✨ Author

**S. Hisham**  
💼 Backend Engineer | Java & NLP Enthusiast  
🔗 [LinkedIn](https://www.linkedin.com/in/syedhisham41) | [GitHub](https://github.com/syedhisham41)
