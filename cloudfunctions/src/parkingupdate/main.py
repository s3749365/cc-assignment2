# Imports
import csv
import logging
import sqlalchemy
from sqlalchemy import text, create_engine
from sodapy import Socrata
from flask import Response
import requests

project_id="cc-assignment-2-parking-space"

### LOGS
logger =logging.getLogger()

### CLIENTS
# SODA V2 Client
sodaClient=Socrata("data.melbourne.vic.gov.au",None)
rows=sodaClient.get("vh2v-4nfs")

# Cloud SQL Database
db_user="root"
db_pass="root"
db_name="parking"
db_instance="parkinginfo"
cloud_sql_connection_name="cc-assignment-2-parking-space:australia-southeast1:parkinginfo"
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
PRESENT='Present'
Unoccupied='Unoccupied'

def process_status(status):
    if status==PRESENT:
        return 1
    return 0

# lat is 17 digits long while lon can be 18 digits long, both have 15 decimal places max
def validate_table():
    with database.connect() as conn:
        conn.execute(
            "CREATE TABLE IF NOT EXISTS parking.info "
            "( id VARCHAR(40) NOT NULL, occupied BOOL NOT NULL, "
            "lat DECIMAL(17,15) NOT NULL, lon DECIMAL(18,15) NOT NULL, PRIMARY KEY (id) );"
        )

# RUNTIME Process
def update(request):
    logging.info("Validating Database Table")
    validate_table()

    try:
        with open(filename, 'w') as f:
            wr = csv.writer(f)
            for row in rows:
                id=row["bay_id"]
                occupied=process_status(row["status"])
                lat=row["lat"]
                lon=row["lon"]
                wr.writerow([id, occupied, lat, lon])

        query = text("""
        LOAD DATA LOCAL INFILE '/tmp/sql_parking.csv'
        REPLACE INTO TABLE info
        FIELDS TERMINATED BY ','
        """)
        with database.connect() as conn:
            conn.execute(query)

    except Exception as err:
        logger.exception(err)
        return Response(
            status=500,
            response=err,
        )