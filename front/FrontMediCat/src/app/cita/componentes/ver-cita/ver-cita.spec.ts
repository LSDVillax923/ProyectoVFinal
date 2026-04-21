import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerCita } from './ver-cita';

describe('VerCita', () => {
  let component: VerCita;
  let fixture: ComponentFixture<VerCita>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerCita],
    }).compileComponents();

    fixture = TestBed.createComponent(VerCita);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
