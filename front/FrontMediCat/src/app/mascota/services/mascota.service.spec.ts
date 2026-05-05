import { TestBed } from '@angular/core/testing';
import { MascotaRestService } from './mascota.service';

describe('MascotaRestService', () => {
  let service: MascotaRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MascotaRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});