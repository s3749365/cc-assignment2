package edu.rmit.cc.a2web.finder.core;


import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryJobConfiguration;
import edu.rmit.cc.a2web.model.Parking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParkingSpaceFinderService {

    @Autowired
    private BigQuery bigQuery;


    public List<Parking> findParkingSpaces(double lng, double lat, Integer dist) throws InterruptedException {

        String template = "WITH parkings AS \n" +
                "( \n" +
                "       SELECT id, \n" +
                "              occupied, \n" +
                "              lat, \n" +
                "              lon, \n" +
                "              St_geogpoint(Cast(lon AS FLOAT64), Cast(lat AS FLOAT64))                                    AS loc,\n" +
                "              St_geogpoint(Cast(%s AS FLOAT64), Cast(%s AS FLOAT64)) AS currentloc\n" +
                "       FROM   `cc-assignment-2-parking-space.parking.info` ),\n" +
                "result AS ( \n" +
                "       SELECT id, \n" +
                "              occupied, \n" +
                "              lat, \n" +
                "              lon, \n" +
                "              st_distance(loc,currentloc) AS dist \n" +
                "       FROM   parkings \n" +
                "       WHERE  st_dwithin(loc,currentloc, %s)) \n" +
                "       \n" +
                "       \n" +
                "SELECT   * \n" +
                "FROM     result \n" +
                "ORDER BY dist " +
                "limit 5";

        String query = String.format(template, lng, lat, dist == null ? 500 : dist);

        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();

        ArrayList<Parking> result = new ArrayList<>();


        for (FieldValueList row : bigQuery.query(queryConfig).iterateAll()) {
            Parking p = new Parking();
            p.setId(row.get("id").getStringValue());
            p.setOccupied(row.get("occupied").getBooleanValue());
            p.setLat(row.get("lat").getDoubleValue());
            p.setLng(row.get("lon").getDoubleValue());
            result.add(p);
        }

        return result;

    }
}
