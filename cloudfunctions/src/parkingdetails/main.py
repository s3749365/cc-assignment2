# Imports
import sqlalchemy
from sqlalchemy import text, create_engine
from flask import Response
import requests

project_id="cca2"

# Cloud SQL Database
db_user="root"
db_pass="root"
db_name="parking"
db_instance="parkinginfo"
cloud_sql_connection_name="cca2-277004:australia-southeast1:parkinginfo"
database = sqlalchemy.create_engine(
    sqlalchemy.engine.url.URL(
        drivername="mysql+pymysql",
        username=db_user,
        password=db_pass,
        database=db_name,
        query={
            "unix_socket": "/cloudsql/{}".format(cloud_sql_connection_name),
            "local_infile": 1
        },
    ),
)

### LOGICS
def validate_table():
    with database.connect() as conn:
        conn.execute(
            "CREATE TABLE IF NOT EXISTS parking.detail "
            "( id VARCHAR(40) NOT NULL, streetname TEXT, areaname TEXT, sign TEXT, "
            "PRIMARY KEY (id) );"
        )

# RUNTIME Process
def update(request):
    validate_table()