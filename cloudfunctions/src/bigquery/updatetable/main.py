# Imports
import json
from sodapy import Socrata
from google.cloud import storage, bigquery

# Const Cloud Function ENV Variables
FILENAME = '/tmp/info.json'
STORAGEFILENAME = 'info.json'

# Const Parking Space Statuses
PRESENT = 'Present'
Unoccupied = 'Unoccupied'

# Const Database table field names
TABLENAME = 'parkinginfo'
PRIMARYKEY = 'id'
STATUS = 'occupied'
LAT = 'lat'
LON = 'lon'

# SODA V2 Client
sodaClient = Socrata("data.melbourne.vic.gov.au",None)
# JSON Data Retrieved from City Of Melbourne Datasets, remove limit of 5 on production
rows = sodaClient.get("vh2v-4nfs", limit=3800)

# Storage Client
storageClient = storage.Client()
bucket = storageClient.get_bucket("parking_data_csv_bucket")
blob = bucket.blob(STORAGEFILENAME)
storageFileURI = "gs://parking_data_csv_bucket/{}".format(STORAGEFILENAME)

# BigQuery Client
bigqueryClient = bigquery.Client()
dataset_id = 'parking'
dataset_table = 'info'

#______________________________________________________________________________________

# Converts VARCHAR of original Dataset into a boolean
def process_status(status):
    if status == PRESENT:
        return "true"
    return "false"

# Convert JSON into Table Data
def importToBigQuery():

    dataset_ref = bigqueryClient.dataset(dataset_id)
    job_config = bigquery.LoadJobConfig()
    job_config.write_disposition = bigquery.WriteDisposition.WRITE_TRUNCATE
    job_config.source_format = bigquery.SourceFormat.NEWLINE_DELIMITED_JSON
    uri = storageFileURI

    load_job = bigqueryClient.load_table_from_uri(
        uri,
        dataset_ref.table(dataset_table),
        # location="australia-southeast1",
        job_config=job_config,
    )
    load_job.result()

# RUNTIME Process
def start(request):
    # Store into tmp
    with open(FILENAME, 'w') as f:
        for row in rows:
            bayID = row["bay_id"]
            bayStatus = process_status(row["status"])
            bayLat = str(row["lat"])
            bayLon = str(row["lon"])
            pythonDic = {
                PRIMARYKEY: bayID,
                STATUS: bayStatus,
                LAT: bayLat,
                LON: bayLon
            }
            f.write(json.dumps(pythonDic))
            f.write("\n")
    
    # Upload the file into Cloud Storage
    blob.upload_from_filename(FILENAME)
    # Upload file data
    importToBigQuery()