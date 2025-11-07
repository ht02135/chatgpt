
# /simple-python-tutorial/src/etl_pipeline2/etl_pipeline.py
# python -m pip install -r requirements.txt
# python -m pip list

import logging
import pandas as pd
from sqlalchemy import create_engine, text
from dotenv import load_dotenv
from typing import Optional
import os

# ------------------------------------------------------------
# Logging setup
# ------------------------------------------------------------
logger = logging.getLogger("UserManagementListServiceImpl")
logger.setLevel(logging.DEBUG)
handler = logging.StreamHandler()
formatter = logging.Formatter("%(asctime)s [%(levelname)s] %(message)s")
handler.setFormatter(formatter)
logger.addHandler(handler)

# ------------------------------------------------------------
# Global Paths (for standalone run)
# ------------------------------------------------------------
BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
ENV_PATH = os.path.join(BASE_DIR, "configs", "db_connections.env")
CSV_PATH = os.path.join(BASE_DIR, "data", "sales_data.csv")

logger.debug("Global BASE_DIR=%s", BASE_DIR)
logger.debug("Global ENV_PATH=%s", ENV_PATH)
logger.debug("Global CSV_PATH=%s", CSV_PATH)

# Load environment file globally
if not os.path.exists(ENV_PATH):
    logger.error("Environment file not found at %s", ENV_PATH)
else:
    load_dotenv(ENV_PATH)
    logger.debug("Environment file loaded successfully")

# ------------------------------------------------------------
# Helper: load DB config from .env
# ------------------------------------------------------------
def load_db_config(env_path: str) -> dict:
    logger.debug("load_db_config called")
    logger.debug("load_db_config env_path=%s", env_path)

    if not os.path.exists(env_path):
        logger.error("Environment file not found at %s", env_path)
        raise FileNotFoundError(f"Missing env file: {env_path}")

    # jdbc.driver=com.mysql.cj.jdbc.Driver
    # jdbc.url=jdbc:mysql://localhost:3306/chatgpt_db?useSSL=false
    # db.hostname=localhost
    # db.port=3306
    # db.name=chatgpt_db
    # db.username=root
    # db.password=ZAQ!zaq1
    
    load_dotenv(env_path)
    cfg = {
        "DB_DIALECT": os.getenv("DB_DIALECT", "mysql+pymysql"),
        "DB_HOST": os.getenv("DB_HOST", "localhost"),
        "DB_PORT": os.getenv("DB_PORT", "3306"),
        "DB_NAME": os.getenv("DB_NAME", "chatgpt_db"),
        "DB_USER": os.getenv("DB_USER", "root"),
        "DB_PASS": os.getenv("DB_PASS", "ZAQ!zaq1"),
    }

    for k, v in cfg.items():
        masked = "******" if "PASS" in k else v
        logger.debug("load_db_config %s=%s", k, masked)

    return cfg

# ------------------------------------------------------------
# Build DB URL
# ------------------------------------------------------------
def build_db_url(cfg: dict) -> str:
    logger.debug("build_db_url called")
    logger.debug("build_db_url cfg=%s", cfg)
    db_url = f"{cfg['DB_DIALECT']}://{cfg['DB_USER']}:{cfg['DB_PASS']}@{cfg['DB_HOST']}:{cfg['DB_PORT']}/{cfg['DB_NAME']}"
    logger.debug("build_db_url db_url=%s", db_url)
    return db_url

# ------------------------------------------------------------
# Test DB Connection
# ------------------------------------------------------------
def test_connection(db_url: str):
    logger.debug("test_connection called")
    logger.debug("test_connection db_url=%s", db_url)
    engine = create_engine(db_url)
    with engine.connect() as conn:
        result = conn.execute(text("SELECT VERSION()"))
        version = result.scalar()
        logger.info("Connected to MySQL version: %s", version)

# ------------------------------------------------------------
# Extract
# ------------------------------------------------------------
def extract_data(file_path: str) -> pd.DataFrame:
    logger.debug("extract_data called")
    logger.debug("extract_data file_path=%s", file_path)
    df = pd.read_csv(file_path)
    logger.debug("extract_data dataframe head:\n%s", df.head().to_string(index=False))
    return df

# ------------------------------------------------------------
# Transform
# ------------------------------------------------------------
def transform_data(df: pd.DataFrame, min_quantity: Optional[int] = None) -> pd.DataFrame:
    logger.debug("transform_data called")
    logger.debug("transform_data df (rows)=%s", len(df))
    logger.debug("transform_data min_quantity=%s", min_quantity)

    df = df.copy()  # avoid SettingWithCopyWarning
    df['date'] = pd.to_datetime(df['date'], errors='coerce')
    df = df.dropna(subset=['quantity', 'unit_price'])
    df.loc[:, 'quantity'] = df['quantity'].astype(int)
    df.loc[:, 'unit_price'] = df['unit_price'].astype(float)
    df.loc[:, 'total_revenue'] = df['quantity'] * df['unit_price']

    if min_quantity is not None:
        df = df[df['quantity'] >= min_quantity]

    logger.debug("transform_data result head:\n%s", df.head().to_string(index=False))
    return df

# ------------------------------------------------------------
# Load
# ------------------------------------------------------------
def load_to_mysql(df: pd.DataFrame, db_url: str, table_name: str = "sales_data", if_exists: str = "replace"):
    logger.debug("load_to_mysql called")
    logger.debug("load_to_mysql df rows=%s", len(df))
    logger.debug("load_to_mysql db_url=%s", db_url)
    logger.debug("load_to_mysql table_name=%s", table_name)
    logger.debug("load_to_mysql if_exists=%s", if_exists)

    engine = create_engine(db_url)
    df.to_sql(table_name, engine, if_exists=if_exists, index=False)
    logger.info("load_to_mysql finished: %d rows -> %s.%s", len(df), engine.url.database, table_name)

# ------------------------------------------------------------
# Orchestration wrapper
# ------------------------------------------------------------
def run_etl_local(csv_path: str, env_path: str, min_quantity: Optional[int] = None):
    logger.debug("run_etl_local called")
    logger.debug("run_etl_local csv_path=%s", csv_path)
    logger.debug("run_etl_local env_path=%s", env_path)
    logger.debug("run_etl_local min_quantity=%s", min_quantity)

    try:
        cfg = load_db_config(env_path)
        db_url = build_db_url(cfg)
        test_connection(db_url)
        df = extract_data(csv_path)
        df_t = transform_data(df, min_quantity=min_quantity)
        load_to_mysql(df_t, db_url)
        logger.info("run_etl_local completed successfully")
    except Exception as e:
        logger.exception("Error during ETL run: %s", e)

# ------------------------------------------------------------
# Entry point (standalone)
# ------------------------------------------------------------
if __name__ == "__main__":
    logger.debug("Main started")
    logger.debug("Main BASE_DIR=%s", BASE_DIR)
    logger.debug("Main ENV_PATH=%s", ENV_PATH)
    logger.debug("Main CSV_PATH=%s", CSV_PATH)

    run_etl_local(CSV_PATH, ENV_PATH)
