import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { ListarVeterinarios } from './listar-veterinarios';
import { VeterinarioRestService } from '../../services/veterinario-rest.service';

describe('ListarVeterinarios', () => {
  let component: ListarVeterinarios;
  let fixture: ComponentFixture<ListarVeterinarios>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListarVeterinarios],
      providers: [
        provideRouter([]),
        {
          provide: VeterinarioRestService,
          useValue: {
            getAll: () => of([]),
            delete: () => of(void 0),
            cambiarEstado: () => of(void 0),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListarVeterinarios);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});