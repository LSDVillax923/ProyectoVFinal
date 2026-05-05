import { TestBed } from '@angular/core/testing';
import { DrogaRestService } from './droga.service';

describe('DrogaRestService', () => {
  let service: DrogaRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DrogaRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});