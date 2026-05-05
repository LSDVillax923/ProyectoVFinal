import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EditarCitaComponent } from './editar-cita';

describe('EditarCitaComponent', () => {
  let component: EditarCitaComponent;
  let fixture: ComponentFixture<EditarCitaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditarCitaComponent], // si es standalone
    }).compileComponents();

    fixture = TestBed.createComponent(EditarCitaComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});