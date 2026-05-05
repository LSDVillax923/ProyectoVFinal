import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';
import { roleGuard, adminGuard } from './role-guard.guard';

describe('Guards', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('roleGuard should be defined', () => {
    const executeGuard: CanActivateFn = (...guardParameters) =>
      TestBed.runInInjectionContext(() => roleGuard(...guardParameters));

    expect(executeGuard).toBeTruthy();
  });

  it('adminGuard should be defined', () => {
    const executeGuard: CanActivateFn = (...guardParameters) =>
      TestBed.runInInjectionContext(() => adminGuard(...guardParameters));

    expect(executeGuard).toBeTruthy();
  });

});