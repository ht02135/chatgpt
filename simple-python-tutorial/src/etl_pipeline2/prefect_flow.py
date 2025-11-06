# /simple-python-tutorial/src/etl_pipeline2/prefect_flow.py
# python -m pip install -r requirements.txt
# prefect deployment build -n "sales-etl-flow" prefect_flow.py:sales_etl_flow

import logging
import os
from prefect import flow, task
import etl_pipeline as etl

# -------------------------
# Logging setup (always present)
# -------------------------
logger = logging.getLogger("UserManagementListServiceImpl")
logger.setLevel(logging.DEBUG)
handler = logging.StreamHandler()
formatter = logging.Formatter("%(asctime)s [%(levelname)s] %(message)s")
handler.setFormatter(formatter)
if not logger.handlers:
    logger.addHandler(handler)

# -------------------------
# Task wrappers (Preserve full logging style)
# -------------------------
@task
def extract(file_path: str):
    logger.debug("extract called")
    logger.debug("extract file_path=%s", file_path)
    df = etl.extract_data(file_path)
    logger.debug("extract completed; df shape=%s", df.shape)
    return df

@task
def transform(df):
    logger.debug("transform called")
    logger.debug("transform df rows=%s", len(df))
    df_t = etl.transform_data(df, min_quantity=1)
    logger.debug("transform completed; df_t shape=%s", df_t.shape)
    return df_t

@task
def load(df, env_path: str):
    logger.debug("load called")
    logger.debug("load df rows=%s", len(df))
    logger.debug("load env_path=%s", env_path)
    cfg = etl.load_db_config(env_path)
    db_url = etl.build_db_url(cfg)
    logger.debug("load db_url=%s", db_url)
    etl.load_to_mysql(df, db_url)
    logger.debug("load completed")

# -------------------------
# Prefect flow definition
# -------------------------
@flow(name="sales-etl-flow")
def sales_etl_flow(csv_path: str, env_path: str):
    logger.debug("sales_etl_flow called")
    logger.debug("sales_etl_flow csv_path=%s", csv_path)
    logger.debug("sales_etl_flow env_path=%s", env_path)

    df = extract(csv_path)
    df_t = transform(df)
    load(df_t, env_path)

    logger.info("sales_etl_flow finished successfully")

# -------------------------
# Entry point
# -------------------------
if __name__ == "__main__":
    logger.debug("__main__ called")

    CSV_PATH = os.path.join("data", "sales_data.csv")
    ENV_PATH = os.path.join("configs", "db_connections.env")

    logger.debug("__main__ CSV_PATH=%s", CSV_PATH)
    logger.debug("__main__ ENV_PATH=%s", ENV_PATH)

    sales_etl_flow(CSV_PATH, ENV_PATH)
