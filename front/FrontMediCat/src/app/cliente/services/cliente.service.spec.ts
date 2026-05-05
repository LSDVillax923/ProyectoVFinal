import { TestBed } from '@angular/core/testing';
import { ClienteRestService } from './cliente.service';

describe('ClienteRestService', () => {
  let service: ClienteRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClienteRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});