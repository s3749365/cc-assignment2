package edu.rmit.cc.a2web.finder.core;


import edu.rmit.cc.a2web.core.config.ApplicationDB;
import edu.rmit.cc.a2web.model.Parking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParkingSpaceFinderService {

    @Autowired
    private ApplicationDB db;


    private static String PARKING_QUERY = "SELECT * FROM\n" +
            "    (SELECT \n" +
            "        info.id,\n" +
            "            info.occupied,\n" +
            "            info.lat,\n" +
            "            info.lon,\n" +
            "            detail.streetname,\n" +
            "            detail.areaname,\n" +
            "            detail.sign,\n" +
            "            ST_DISTANCE_SPHERE(POINT(%s, %s), POINT(lon, lat)) AS dist\n" +
            "    FROM\n" +
            "        parking.info AS info\n" +
            "    JOIN parking.detail AS detail ON parking.info.id = parking.detail.id) as temp\n"+
            "WHERE dist <= %s && occupied=0\n"+
            "ORDER BY dist ASC";

    public List<Parking> findParkingSpaces(double lng, double lat, Integer dist) {

        Connection conn = db.getConnection();

        String query = String.format(PARKING_QUERY, lng, lat, dist);

        List<Parking> result = new ArrayList<>();

        try {

            PreparedStatement statement = conn.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {


                Parking parking = new Parking();

                parking.setId(resultSet.getString("id"));
                parking.setLat(resultSet.getDouble("lat"));
                parking.setLng(resultSet.getDouble("lon"));
                parking.setStreetName(resultSet.getString("streetname"));
                parking.setAreaName(resultSet.getString("areaname"));
                parking.setSign(resultSet.getString("sign"));
                parking.setDistance(resultSet.getInt("dist"));

                result.add(parking);
            }

            conn.close();

        } catch (Exception ex) {

            throw new RuntimeException(ex);

        }


        return result;

    }
}
