import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';
import { adminGuard, veterinarioGuard } from './role-guard.guard';

describe('Guards', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('veterinarioGuard should be defined', () => {
    const executeGuard: CanActivateFn = (...guardParameters) =>
      TestBed.runInInjectionContext(() => veterinarioGuard(...guardParameters));

    expect(executeGuard).toBeTruthy();
  });

  it('adminGuard should be defined', () => {
    const executeGuard: CanActivateFn = (...guardParameters) =>
      TestBed.runInInjectionContext(() => adminGuard(...guardParameters));

    expect(executeGuard).toBeTruthy();
  });

});