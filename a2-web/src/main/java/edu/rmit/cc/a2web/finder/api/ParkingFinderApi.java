package edu.rmit.cc.a2web.finder.api;

import com.google.auth.Credentials;
import com.google.cloud.bigquery.*;
import edu.rmit.cc.a2web.core.api.BaseApiController;
import edu.rmit.cc.a2web.finder.core.ParkingSpaceFinderService;
import edu.rmit.cc.a2web.model.Parking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@RestController
@RequestMapping(value = "")
public class ParkingFinderApi extends BaseApiController {


    @Autowired
    private ParkingSpaceFinderService parkingSpaceFinderService;

    @GetMapping("/find")
    public ResponseEntity find(double lng, double lat, Integer dist) throws InterruptedException {

        List<Parking> result = parkingSpaceFinderService.findParkingSpaces(lng, lat, dist);

        return ok(result);
    }


    @GetMapping("/test")
    public String test() {
        return "Success";
    }
}
