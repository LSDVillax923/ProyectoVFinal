import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListarClienteComponent } from './listar-cliente';

describe('ListarClienteComponent', () => {
  let component: ListarClienteComponent;
  let fixture: ComponentFixture<ListarClienteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListarClienteComponent], // si es standalone
    }).compileComponents();

    fixture = TestBed.createComponent(ListarClienteComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});