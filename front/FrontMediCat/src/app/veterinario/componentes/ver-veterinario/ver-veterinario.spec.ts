import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerVeterinario } from './ver-veterinario';

describe('VerVeterinario', () => {
  let component: VerVeterinario;
  let fixture: ComponentFixture<VerVeterinario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerVeterinario],
    }).compileComponents();

    fixture = TestBed.createComponent(VerVeterinario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
