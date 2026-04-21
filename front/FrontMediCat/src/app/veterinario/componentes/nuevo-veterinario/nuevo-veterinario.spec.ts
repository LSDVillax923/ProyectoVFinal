import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NuevoVeterinario } from './nuevo-veterinario';

describe('NuevoVeterinario', () => {
  let component: NuevoVeterinario;
  let fixture: ComponentFixture<NuevoVeterinario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NuevoVeterinario],
    }).compileComponents();

    fixture = TestBed.createComponent(NuevoVeterinario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
