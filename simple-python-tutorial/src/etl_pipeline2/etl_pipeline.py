
# /simple-python-tutorial/src/etl_pipeline2/etl_pipeline.py
# python -m pip install -r requirements.txt
# python -m pip list

import logging
import pandas as pd
from sqlalchemy import create_engine
from typing import Optional
import os

# -------------------------
# Logging setup (always present)
# -------------------------
logger = logging.getLogger("UserManagementListServiceImpl")
logger.setLevel(logging.DEBUG)
handler = logging.StreamHandler()
formatter = logging.Formatter("%(asctime)s [%(levelname)s] %(message)s")
handler.setFormatter(formatter)
logger.addHandler(handler)

# -------------------------
# Helper: load DB connection from env file
# -------------------------
def load_db_config(env_path: str) -> dict:
    logger.debug("load_db_config called")
    logger.debug("load_db_config env_path=%s", env_path)
    config = {}
    try:
        with open(env_path, "r") as f:
            for line in f:
                line = line.strip()
                if not line or line.startswith("#"):
                    continue
                key, value = line.split("=", 1)
                config[key.strip()] = value.strip()
        logger.debug("load_db_config result=%s", config)
    except Exception as e:
        logger.exception("Error reading env file: %s", e)
        raise
    return config

# -------------------------
# Build DB URL from config
# -------------------------
def build_db_url(cfg: dict) -> str:
    logger.debug("build_db_url called")
    logger.debug("build_db_url cfg=%s", cfg)
    try:
        db_url = f"{cfg['DB_DIALECT']}://{cfg['DB_USER']}:{cfg['DB_PASS']}@{cfg['DB_HOST']}:{cfg['DB_PORT']}/{cfg['DB_NAME']}"
        logger.debug("build_db_url db_url=%s", db_url)
        return db_url
    except KeyError as e:
        logger.exception("Missing key in DB config: %s", e)
        raise

# -------------------------
# EXTRACT
# -------------------------
def extract_data(file_path: str) -> pd.DataFrame:
    logger.debug("extract_data called")
    logger.debug("extract_data file_path=%s", file_path)
    df = pd.read_csv(file_path)
    logger.debug("extract_data dataframe head:\n%s", df.head().to_string(index=False))
    return df

# -------------------------
# TRANSFORM
# -------------------------
def transform_data(df: pd.DataFrame, min_quantity: Optional[int] = None) -> pd.DataFrame:
    logger.debug("transform_data called")
    logger.debug("transform_data df (rows)=%s", len(df))
    logger.debug("transform_data min_quantity=%s", min_quantity)

    df['date'] = pd.to_datetime(df['date'], errors='coerce')
    df = df.dropna(subset=['quantity', 'unit_price'])
    df['quantity'] = df['quantity'].astype(int)
    df['unit_price'] = df['unit_price'].astype(float)
    df['total_revenue'] = df['quantity'] * df['unit_price']

    if min_quantity is not None:
        df = df[df['quantity'] >= min_quantity]

    logger.debug("transform_data result head:\n%s", df.head().to_string(index=False))
    return df

# -------------------------
# LOAD
# -------------------------
def load_to_mysql(df: pd.DataFrame, db_url: str, table_name: str = "sales_data", if_exists: str = "replace"):
    logger.debug("load_to_mysql called")
    logger.debug("load_to_mysql df rows=%s", len(df))
    logger.debug("load_to_mysql db_url=%s", db_url)
    logger.debug("load_to_mysql table_name=%s", table_name)
    logger.debug("load_to_mysql if_exists=%s", if_exists)

    engine = create_engine(db_url)
    logger.debug("load_to_mysql engine=%s", engine)
    df.to_sql(table_name, engine, if_exists=if_exists, index=False)
    logger.info("load_to_mysql finished: %d rows -> %s.%s", len(df), engine.url.database, table_name)

# -------------------------
# Orchestration wrapper (local)
# -------------------------
def run_etl_local(csv_path: str, env_path: str, min_quantity: Optional[int] = None):
    logger.debug("run_etl_local called")
    logger.debug("run_etl_local csv_path=%s", csv_path)
    logger.debug("run_etl_local env_path=%s", env_path)
    logger.debug("run_etl_local min_quantity=%s", min_quantity)

    try:
        cfg = load_db_config(env_path)
        db_url = build_db_url(cfg)
        df = extract_data(csv_path)
        df_t = transform_data(df, min_quantity=min_quantity)
        load_to_mysql(df_t, db_url)
        logger.info("run_etl_local completed successfully")
    except Exception as e:
        logger.exception("Error during ETL run: %s", e)

# -------------------------
# Example entry-point
# -------------------------
if __name__ == "__main__":
    # Paths
    ENV_PATH = os.path.join("configs", "db_connections.env")
    CSV_PATH = os.path.join("data", "sales_data.csv")

    run_etl_local(CSV_PATH, ENV_PATH)
