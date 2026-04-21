import { TestBed } from '@angular/core/testing';

import { CitaRestService } from './cita-rest.service';

describe('CitaRestService', () => {
  let service: CitaRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CitaRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
