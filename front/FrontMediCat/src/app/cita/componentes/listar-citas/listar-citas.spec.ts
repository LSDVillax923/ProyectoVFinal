import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListarCitasComponent } from './listar-citas';

describe('ListarCitasComponent', () => {
  let component: ListarCitasComponent;
  let fixture: ComponentFixture<ListarCitasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListarCitasComponent], // si es standalone
    }).compileComponents();

    fixture = TestBed.createComponent(ListarCitasComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});