.DEFAULT_GOAL:

cloudfunction_update_bigquery_table: ## Execute Cloud Function that updates the BigQuery table with new parking data
	gcloud functions call populate_db