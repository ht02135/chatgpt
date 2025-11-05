
# https://www.w3schools.com/python/python_mysql_getstarted.asp
# Install MySQL Driver
# Python needs a MySQL driver to access the MySQL database.

# C:\Users\ht021\AppData\Local\Programs\Python\Python314\Scripts>pip --version
# pip install mysql-connector-python 

# C:\Users\ht021>python --version
# Python 3.14.0
# python -m pip install mysql-connector-python 
# C:\Users\ht021>python -m pip list
# Package                Version
# ---------------------- -----------
# camelcase              0.2
# contourpy              1.3.3
# cycler                 0.12.1
# fonttools              4.60.1
# kiwisolver             1.4.9
# matplotlib             3.10.7
# mysql-connector-python 9.5.0
# numpy                  2.3.4
# packaging              25.0
# pillow                 12.0.0
# pip                    25.2
# pyparsing              3.2.5
# python-dateutil        2.9.0.post0
# six                    1.17.0

# Test MySQL Connector
# https://www.w3schools.com/python/python_mysql_create_table.asp

import mysql.connector
import logging

# =====================================================
# setup logger (similar to Java's LogManager.getLogger)
# =====================================================
logger = logging.getLogger("UserManagementListServiceImpl")
logging.basicConfig(
    level=logging.DEBUG,
    format="%(asctime)s - %(levelname)s - %(message)s"
)

# =====================================================
# Database connection
# =====================================================
def connect_db():
    logger.debug("connect_db called")
    db_config = {
        "host": "localhost",
        "user": "root",
        "password": "ZAQ!zaq1",
        "database": "chatgpt_db"
    }
    logger.debug("connect_db db_config=%s", db_config)

    # **db_config unpacks the dictionary into keyword arguments
    # equivalent to:
    # conn = mysql.connector.connect(
    #     host="localhost",
    #     user="root",
    #     password="ZAQ!zaq1",
    #     database="chatgpt_db"
    # )
    conn = mysql.connector.connect(**db_config)
    logger.debug("connect_db connection established")
    return conn


# =====================================================
# Create table
# =====================================================
def create_table(cursor):
    logger.debug("create_table called")
    logger.debug("create_table cursor=%s", cursor)

    # Check if table exists
    cursor.execute("SHOW TABLES LIKE 'python_customers'")
    table_exists = cursor.fetchone()
    logger.debug("create_table table_exists=%s", table_exists)

    if table_exists:
        logger.debug("create_table skipped: python_customers already exists")
    else:
        cursor.execute("""
            CREATE TABLE python_customers (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(255),
                address VARCHAR(255)
            )
        """)
        logger.debug("create_table python_customers created successfully")


# =====================================================
# Insert records
# =====================================================
def insert_data(cursor, db):
    logger.debug("insert_data called")
    logger.debug("insert_data cursor=%s", cursor)
    logger.debug("insert_data db=%s", db)

    sql = "INSERT INTO python_customers (name, address) VALUES (%s, %s)"
    data = [
        ("John", "Highway 21"),
        ("Mary", "Park Lane 38"),
        ("Peter", "Valley 345")
    ]

    for val in data:
        logger.debug("insert_data val=%s", val)
        cursor.execute(sql, val)

    db.commit()
    logger.debug("insert_data committed %s record(s)", cursor.rowcount)


# =====================================================
# Select examples
# =====================================================
def select_examples(cursor):
    logger.debug("select_examples called")
    logger.debug("select_examples cursor=%s", cursor)

    # select all
    cursor.execute("SELECT * FROM python_customers")
    for row in cursor.fetchall():
        logger.debug("select_examples all row=%s", row)

    # select filtered
    cursor.execute("SELECT * FROM python_customers WHERE address = 'Park Lane 38'")
    for row in cursor.fetchall():
        logger.debug("select_examples filtered row=%s", row)

    # ordered select
    cursor.execute("SELECT * FROM python_customers ORDER BY name DESC")
    for row in cursor.fetchall():
        logger.debug("select_examples ordered row=%s", row)


# =====================================================
# Main execution
# =====================================================
def main():
    logger.debug("main called")
    db = connect_db()
    cursor = db.cursor()

    create_table(cursor)
    insert_data(cursor, db)
    select_examples(cursor)

    cursor.close()
    db.close()
    logger.debug("main completed, connection closed")


if __name__ == "__main__":
    main()
