import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { Observable, fromEvent, throwError } from 'rxjs';
import { debounceTime, retry } from 'rxjs/operators';
import { ParkingLocatorService } from './parking-locator.service';



@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit {

  private map: google.maps.Map;

  private icons;

  private markers: google.maps.Marker[] = [];


  private mapOptions: google.maps.MapOptions = {
    zoom: 17,
  };



  private coordinates: google.maps.LatLng;

  @ViewChild('mapContainer', { static: false }) gmap: ElementRef;


  constructor(private parkingService: ParkingLocatorService) {

    var iconBase = 'assets/icons/';

    this.icons = {
      parking: {
        url: iconBase + 'parking_lot_maps.png'
      },
      car: {
        url: iconBase + 'car.png'
      }
    };
  }


  ngOnInit(): void {
  }


  ngAfterViewInit() {

    this.mapInitializer();

  }

  mapInitializer() {



    navigator.geolocation.getCurrentPosition(res => {

      this.coordinates = this.getPostion(res.coords.latitude, res.coords.longitude);

      this.mapOptions.center = this.coordinates

      this.map = new google.maps.Map(this.gmap.nativeElement, this.mapOptions);

      this.map.panTo(this.coordinates);

      this.setMarker(this.coordinates, this.map, "car");

      this.showParkings(res);

      navigator.geolocation.watchPosition(res => {

        this.showParkings(res);

      });

    });



  }

  private showParkings(res: Position) {

    this.parkingService.getParkings(this.coordinates.lat(), this.coordinates.lng(), 500).pipe(
      debounceTime(10000)
    ).subscribe(resp => {

      this.removeMarkers();

      this.coordinates = this.getPostion(res.coords.latitude, res.coords.longitude);

      this.mapOptions.center = this.coordinates

      this.setMarker(this.coordinates, this.map, "car");

      resp.forEach((p) => {
        if (!p.occupied) {
          let c = this.getPostion(p.lat, p.lng);
          this.setMarker(c, this.map, "parking");
        }

      })

    });
  }

  private getPostion(lat: number, lng: number): google.maps.LatLng {
    return new google.maps.LatLng(lat, lng);
  }

  private setMarker(c: google.maps.LatLng, map: google.maps.Map, icon: string) {

    let m = new google.maps.Marker({
      position: c,
      map: map,
      icon: this.icons[icon].url
    });

    m.setMap(map);

    this.markers.push(m)

  }

  private removeMarkers() {

    this.markers.forEach(m => {
      m.setMap(null);
    })

    this.markers = [];
  }

}
