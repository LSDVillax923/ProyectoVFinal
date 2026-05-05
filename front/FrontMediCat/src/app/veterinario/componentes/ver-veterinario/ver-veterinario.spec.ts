import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { VerVeterinario } from './ver-veterinario';
import { VeterinarioRestService } from '../../services/veterinario-rest.service';

describe('VerVeterinario', () => {
  let component: VerVeterinario;
  let fixture: ComponentFixture<VerVeterinario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerVeterinario],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: convertToParamMap({ id: '1' }) },
          },
        },
        {
          provide: VeterinarioRestService,
          useValue: {
            findById: () => of({
              id: 1,
              nombre: 'Vet',
              cedula: '123',
              correo: 'vet@test.com',
              celular: '300',
              especialidad: 'General',
              estado: 'activo',
              imageUrl: '',
            }),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(VerVeterinario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});