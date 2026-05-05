import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { PerfilUsuarioComponent } from './perfil-usuario';
import { AuthRestService } from '../../services/auth-rest.service';
import { ClienteRestService } from '../../../cliente/services/cliente.service';
import { VeterinarioRestService } from '../../../veterinario/services/veterinario-rest.service';
import { AdminRestService } from '../../../admin/services/admin-rest.service';

describe('PerfilUsuarioComponent', () => {
  let component: PerfilUsuarioComponent;
  let fixture: ComponentFixture<PerfilUsuarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerfilUsuarioComponent], // si es standalone
      providers: [
        provideRouter([]),
        {
          provide: AuthRestService,
          useValue: {
            getSesion: () => ({ id: 1, rol: 'CLIENTE', nombre: 'Ana', correo: 'ana@test.com' }),
          },
        },
        {
          provide: ClienteRestService,
          useValue: {
            findById: () => of({ id: 1, nombre: 'Ana', correo: 'ana@test.com' }),
            update: () => of({}),
          },
        },
        { provide: VeterinarioRestService, useValue: { findById: () => of({}), update: () => of({}) } },
        { provide: AdminRestService, useValue: { findById: () => of({}), update: () => of({}) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PerfilUsuarioComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});