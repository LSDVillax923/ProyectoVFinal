import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerfilVeterinario } from './perfil-veterinario';

describe('PerfilVeterinario', () => {
  let component: PerfilVeterinario;
  let fixture: ComponentFixture<PerfilVeterinario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerfilVeterinario],
    }).compileComponents();

    fixture = TestBed.createComponent(PerfilVeterinario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
