import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PerfilUsuarioComponent } from './perfil-usuario';

describe('PerfilUsuarioComponent', () => {
  let component: PerfilUsuarioComponent;
  let fixture: ComponentFixture<PerfilUsuarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerfilUsuarioComponent], // si es standalone
    }).compileComponents();

    fixture = TestBed.createComponent(PerfilUsuarioComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});