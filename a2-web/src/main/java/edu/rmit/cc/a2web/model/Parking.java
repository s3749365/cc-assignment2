package edu.rmit.cc.a2web.model;

public class Parking {


    private String id;
    private boolean occupied;
    private double lat;
    private double lng;
    private String streetName;
    private String areaName;
    private String sign;
    private int distance;



    public Parking() {
    }

    public Parking(String id, boolean occupied, double lat, double lng) {
        this.id = id;
        this.occupied = occupied;
        this.lat = lat;
        this.lng = lng;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }



}
