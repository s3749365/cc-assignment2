import { TestBed } from '@angular/core/testing';

import { ParkingLocatorService } from './parking-locator.service';

describe('ParkingLocatorService', () => {
  let service: ParkingLocatorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ParkingLocatorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
