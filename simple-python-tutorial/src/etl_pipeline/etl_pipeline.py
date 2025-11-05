
# etl_pipeline.py
# python -m pip install pandas
# python -m pip install sqlalchemy pymysql python-dotenv pandas
# python -m pip list

import pandas as pd
import logging
from time import sleep
from sqlalchemy import create_engine, text
from dotenv import load_dotenv
import os

# ------------------------------------------------------------
# Step 1: Setup Logging
# ------------------------------------------------------------
logging.basicConfig(
    level=logging.DEBUG,
    format="%(asctime)s [%(levelname)s] %(message)s",
    handlers=[
        # output to simple-python-tutorial/etl_pipeline.log
        logging.FileHandler("etl_pipeline.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

# ------------------------------------------------------------
# Step 2: Load Environment Variables
# ------------------------------------------------------------
# Ensure we explicitly load the correct .env file
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
ENV_PATH = os.path.join(BASE_DIR, "configs", "db_connections.env")

logger.debug("Loading environment from %s", ENV_PATH)
load_dotenv(ENV_PATH)

# ------------------------------------------------------------
# Step 3: Build DB URL from .env
# ------------------------------------------------------------
def build_db_url_from_env():
    logger.debug("build_db_url_from_env called")

# jdbc.driver=com.mysql.cj.jdbc.Driver
# jdbc.url=jdbc:mysql://localhost:3306/chatgpt_db?useSSL=false
# db.hostname=localhost
# db.port=3306
# db.name=chatgpt_db
# db.username=root
# db.password=ZAQ!zaq1

    dialect = os.getenv("DB_DIALECT", "mysql+pymysql")
    host = os.getenv("DB_HOST", "localhost")
    port = os.getenv("DB_PORT", "3306")
    name = os.getenv("DB_NAME", "chatgpt_db")
    user = os.getenv("DB_USER", "root")
    pw = os.getenv("DB_PASS", "")

    logger.debug("build_db_url_from_env dialect=%s", dialect)
    logger.debug("build_db_url_from_env host=%s", host)
    logger.debug("build_db_url_from_env port=%s", port)
    logger.debug("build_db_url_from_env name=%s", name)
    logger.debug("build_db_url_from_env user=%s", user)

    return f"{dialect}://{user}:{pw}@{host}:{port}/{name}"

# ------------------------------------------------------------
# Step 4: Test DB Connection
# ------------------------------------------------------------
def test_connection(db_url):
    logger.debug("test_connection called")
    logger.debug("test_connection db_url=%s", db_url)

    # engine comes from SQLAlchemy, which is a Python library for working with databases.
    # create_engine(db_url) does not connect to the database immediately.
    # It creates a “factory” object that knows how to talk to the database when needed.
    engine = create_engine(db_url)
    with engine.connect() as conn:
        result = conn.execute(text("SELECT VERSION()"))
        version = result.scalar()
        logger.info("Connected to MySQL version: %s", version)

# ------------------------------------------------------------
# Step 5: Extract
# ------------------------------------------------------------
def extract_data(file_path):
    logger.debug("extract_data called")
    logger.debug("extract_data file_path=%s", file_path)

    data = pd.read_csv(file_path)
    logger.debug("extract_data result (head)=%s", data.head())
    return data

# ------------------------------------------------------------
# Step 6: Transform
# ------------------------------------------------------------
def transform_data(data):
    logger.debug("transform_data called")
    logger.debug("transform_data data before transform:\n%s", data.head())

    data = data.dropna()
    data = data[data['age'] > 18]

    logger.debug("transform_data data after transform:\n%s", data.head())
    
    # So data is a Pandas DataFrame, which is basically a table in memory — like an Excel sheet or SQL table
    return data

# ------------------------------------------------------------
# Step 7: Load (MySQL)
# ------------------------------------------------------------
def load_data_mysql(data, db_url):
    logger.debug("load_data_mysql called")
    logger.debug("load_data_mysql db_url=%s", db_url)
    logger.debug("load_data_mysql data to load:\n%s", data.head())

    engine = create_engine(db_url)
    logger.debug("load_data_mysql engine created")

    # Create or replace table
    # DataFrame.to_sql() is a Pandas method that writes a DataFrame to a database table.
    # | Argument              | Meaning                                                                                                                                 |
    # | --------------------- | --------------------------------------------------------------------------------------------------------------------------------------- |
    # | `'python_users'`      | The **name of the table** in the database. If it doesn’t exist, it will be created.                                                     |
    # | `engine`              | SQLAlchemy **engine object** — it knows how to connect to your MySQL database.                                                          |
    # | `if_exists='replace'` | If the table **already exists**, it will **drop it and recreate it**. Alternatives: `'append'` to add rows, `'fail'` to throw an error. |
    # | `index=False`         | Do **not** write the DataFrame index as a column in the database table.                                                                 |
    data.to_sql('python_users', engine, if_exists='replace', index=False)
    logger.debug("load_data_mysql completed successfully")

# ------------------------------------------------------------
# Step 8: Orchestrate ETL
# ------------------------------------------------------------
def run_etl_pipeline():
    logger.debug("run_etl_pipeline called")

    try:
        db_url = build_db_url_from_env()
        test_connection(db_url)

        data = extract_data('data/source_data.csv')
        transformed = transform_data(data)
        load_data_mysql(transformed, db_url)

        logger.info("ETL pipeline completed successfully!")

    except Exception as e:
        logger.error("Error occurred in ETL pipeline: %s", e, exc_info=True)

# ------------------------------------------------------------
# Step 9: Scheduler (Optional)
# ------------------------------------------------------------
if __name__ == "__main__":
    while True:
        run_etl_pipeline()
        logger.info("Sleeping for 24 hours before next run...")
        sleep(86400)
