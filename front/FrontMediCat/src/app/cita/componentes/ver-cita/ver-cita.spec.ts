import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VerCitaComponent } from './ver-cita';

describe('VerCitaComponent', () => {
  let component: VerCitaComponent;
  let fixture: ComponentFixture<VerCitaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerCitaComponent], // si es standalone
    }).compileComponents();

    fixture = TestBed.createComponent(VerCitaComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});