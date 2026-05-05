import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EditarClienteComponent } from './editar-cliente';

describe('EditarClienteComponent', () => {
  let component: EditarClienteComponent;
  let fixture: ComponentFixture<EditarClienteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditarClienteComponent], // si es standalone
    }).compileComponents();

    fixture = TestBed.createComponent(EditarClienteComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});