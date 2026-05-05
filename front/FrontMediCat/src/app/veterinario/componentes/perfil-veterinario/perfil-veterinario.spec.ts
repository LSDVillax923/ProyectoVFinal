import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { PerfilVeterinario } from './perfil-veterinario';
import { AuthService } from '../../../user/services/auth.service';
import { VeterinarioService } from '../../services/veterinario.service';

describe('PerfilVeterinario', () => {
  let component: PerfilVeterinario;
  let fixture: ComponentFixture<PerfilVeterinario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerfilVeterinario],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            getSesion: () => ({ id: 1, nombre: 'Vet', correo: 'vet@test.com', rol: 'VETERINARIO' }),
            setSesion: () => undefined,
          },
        },
        {
          provide: VeterinarioService,
          useValue: {
            getById: () => ({
              id: 1,
              nombre: 'Vet',
              cedula: '123',
              correo: 'vet@test.com',
              celular: '300',
              contrasenia: 'abc123',
              especialidad: 'Medicina General',
            }),
            update: () => undefined,
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PerfilVeterinario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});