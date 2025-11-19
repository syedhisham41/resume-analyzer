CREATE TABLE IF NOT EXISTS users (
  userid INTEGER PRIMARY KEY,
  name TEXT NOT NULL,                     
  email TEXT UNIQUE NOT NULL,                     
  password_hash TEXT NOT NULL,                    
  last_login_at TIMESTAMP,                   
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  salt TEXT NOT NULL
);