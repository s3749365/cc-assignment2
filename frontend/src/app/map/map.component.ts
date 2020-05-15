import { Component, ViewChild, ElementRef, ChangeDetectorRef } from '@angular/core';
import { ParkingLocatorService } from '../services/parking-locator.service';
import { LocationService } from '../services/location-service';
import { Parking } from '../model/parking';




@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent {

  private map: google.maps.Map;
  private autocomplete: google.maps.places.Autocomplete;
  private directionsService = new google.maps.DirectionsService();
  private directionsRenderer = new google.maps.DirectionsRenderer();
  private lastPosition;
  private icons;
  private currentLocationMarker: google.maps.Marker;
  private parkingMarkers: google.maps.Marker[] = [];




  public parkings: Parking[] = [];
  public isLoadingParkings: boolean = true;
  public parkingsBasedOnCurrentLocation: boolean = true;
  public sidebar: boolean = true;
  public parkingConfig = {
    distance: 500
  };

  @ViewChild('mapContainer', { static: false }) gmap: ElementRef;
  @ViewChild('addressTxt', { static: false }) searchTxt: ElementRef;


  constructor(private cd: ChangeDetectorRef, private parkingService: ParkingLocatorService, private locationService: LocationService) {

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


  public toggleSidebar() {
    this.sidebar = !this.sidebar;
  }

  ngAfterViewInit() {

    this.initiliseMap();

  }

  initiliseMap() {



    this.locationService.getCurrentLocation().subscribe(pos => {

      this.lastPosition = pos;

      const coordinates = this.getCoordinates(pos);


      if (!this.map) {
        this.createMap(coordinates)
      }

      this.updateMap(coordinates, pos.coords.accuracy);

      this.showParkings(coordinates);

    });


  }


  private updateMap(coordinates: google.maps.LatLng, accuracy) {

    var circle = new google.maps.Circle(
      { center: coordinates, radius: accuracy });


    this.autocomplete.setBounds(circle.getBounds());

    this.map.setCenter(coordinates);

    this.map.panTo(coordinates);

    this.currentLocationMarker.setMap(null);

    this.currentLocationMarker = this.getMarker(coordinates, "car");

  }

  private createMap(coordinates: google.maps.LatLng) {


    this.autocomplete = new google.maps.places.Autocomplete(this.searchTxt.nativeElement);

    const self = this;

    this.autocomplete.addListener("place_changed", () => {

      self.showParkingsBasedOnSelectedPlace();

    });

    const mapOptions: google.maps.MapOptions = {
      zoom: 17,
      mapTypeControl: false,
    };

    this.map = new google.maps.Map(this.gmap.nativeElement, mapOptions);

    this.directionsRenderer.setMap(this.map);

    this.currentLocationMarker = this.getMarker(coordinates, "car");

  }

  private showParkingsBasedOnSelectedPlace() {

    const place = this.autocomplete.getPlace();

    this.parkingsBasedOnCurrentLocation = false;

    this.showParkings(place.geometry.location)

  }

  public refresh() {

    if (this.parkingsBasedOnCurrentLocation) {

      this.useCurrentLocation();

    } else {

      this.showParkingsBasedOnSelectedPlace();

    }


  }


  public useCurrentLocation() {

    this.parkingsBasedOnCurrentLocation = true;

    const coordinates = this.getCoordinates(this.lastPosition);

    this.showParkings(coordinates)

  }

  private showParkings(coordinates: google.maps.LatLng) {

    this.map.setCenter(coordinates);

    this.parkingService.getParkings(coordinates.lat(), coordinates.lng(), this.parkingConfig.distance)
      .subscribe(resp => {

        this.parkings.splice(0, this.parkings.length);
        this.removeMarkers();

        resp.forEach(parking => {
          var coordinates = new google.maps.LatLng(parking.lat, parking.lng);
          var marker = this.getMarker(coordinates, "parking")
          this.parkingMarkers.push(marker);
          this.parkings.push(parking);
        });

        this.isLoadingParkings = false;

        this.cd.detectChanges();


      }, () => {

        this.parkings.splice(0, this.parkings.length);
        this.isLoadingParkings = false;

      });



  }

  public directUser(parking: Parking) {
    var start = this.getCoordinates(this.lastPosition);
    var end = new google.maps.LatLng(parking.lat, parking.lng);
    this.calcRoute(start, end);
    this.toggleSidebar();
  }

  private calcRoute(start: google.maps.LatLng, end: google.maps.LatLng) {


    var request = {
      origin: start,
      destination: end,
      travelMode: google.maps.TravelMode.DRIVING

    };

    const self = this;

    this.directionsService.route(request, function (response, status) {
      if (status == 'OK') {
        self.directionsRenderer.setDirections(response);
      }
    });
  }

  private getCoordinates(position: Position): google.maps.LatLng {
    return new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
  }

  private getMarker(c: google.maps.LatLng, icon: string) {

    let m = new google.maps.Marker({
      position: c,
      map: this.map,
      icon: this.icons[icon].url
    });

    m.setMap(this.map);

    return m;

  }


  private removeMarkers() {

    this.parkingMarkers.forEach(m => m.setMap(null));

    this.parkingMarkers.splice(0, this.parkingMarkers.length);

  }



}
