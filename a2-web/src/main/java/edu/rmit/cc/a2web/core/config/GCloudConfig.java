package edu.rmit.cc.a2web.core.config;


import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GCloudConfig {


    @Bean
    public BigQuery bigQueryBean() {
        return BigQueryOptions.getDefaultInstance().getService();
    }

}
