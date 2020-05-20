import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Parking } from '../model/parking';


const API_PATH = "/api";


@Injectable({
  providedIn: 'root'
})
export class ParkingLocatorService {


  constructor(private http: HttpClient) { }


  public getParkings(lat: number, lng: number, dist: number) {
    return this.http.get<Parking[]>(API_PATH + "/find", {
      params: { lat: lat.toString(), lng: lng.toString(), dist: dist.toString() }
    });
  }
}
