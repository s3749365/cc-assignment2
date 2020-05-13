import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { debounceTime, switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  constructor() { }


  public getCurrentLocation(): Observable<Position> {

    const getCurrentPosition = Observable.create(observer => {
      navigator.geolocation.watchPosition(
        position => {
          observer.next(position);
          observer.complete();
        },
        observer.error.bind(observer)
      );
    });

   return getCurrentPosition.pipe(
      debounceTime(1500),
    );
    
  }

}
