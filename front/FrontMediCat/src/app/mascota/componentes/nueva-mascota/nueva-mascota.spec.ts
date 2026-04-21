import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NuevaMascota } from './nueva-mascota';

describe('NuevaMascota', () => {
  let component: NuevaMascota;
  let fixture: ComponentFixture<NuevaMascota>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NuevaMascota],
    }).compileComponents();

    fixture = TestBed.createComponent(NuevaMascota);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
