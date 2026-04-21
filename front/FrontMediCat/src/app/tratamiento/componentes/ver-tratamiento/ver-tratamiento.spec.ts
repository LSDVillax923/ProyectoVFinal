import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerTratamiento } from './ver-tratamiento';

describe('VerTratamiento', () => {
  let component: VerTratamiento;
  let fixture: ComponentFixture<VerTratamiento>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerTratamiento],
    }).compileComponents();

    fixture = TestBed.createComponent(VerTratamiento);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
