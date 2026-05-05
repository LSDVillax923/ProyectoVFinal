import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { NuevoVeterinario } from './nuevo-veterinario';
import { VeterinarioRestService } from '../../services/veterinario-rest.service';

describe('NuevoVeterinario', () => {
  let component: NuevoVeterinario;
  let fixture: ComponentFixture<NuevoVeterinario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NuevoVeterinario],
      providers: [
        provideRouter([]),
        {
          provide: VeterinarioRestService,
          useValue: {
            create: () => of(void 0),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NuevoVeterinario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});