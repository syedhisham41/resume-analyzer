FROM python:3.10-slim

WORKDIR /app

COPY requirements.txt .
COPY clean_jd_text.py .
COPY original_text.txt .
COPY app app

# RUN pip install --no-cache-dir -r requirements.txt
RUN pip install --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt && \
    apt-get clean && rm -rf /var/lib/apt/lists/* /root/.cache

EXPOSE 8000

ENTRYPOINT ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]