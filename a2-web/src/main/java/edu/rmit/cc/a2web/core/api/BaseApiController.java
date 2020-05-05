package edu.rmit.cc.a2web.core.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseApiController {



    public <R> ResponseEntity<R> ok(R response) {

        return new ResponseEntity(response, HttpStatus.OK);
    }


}
