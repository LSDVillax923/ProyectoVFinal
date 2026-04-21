import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NuevoTratamiento } from './nuevo-tratamiento';

describe('NuevoTratamiento', () => {
  let component: NuevoTratamiento;
  let fixture: ComponentFixture<NuevoTratamiento>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NuevoTratamiento],
    }).compileComponents();

    fixture = TestBed.createComponent(NuevoTratamiento);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
