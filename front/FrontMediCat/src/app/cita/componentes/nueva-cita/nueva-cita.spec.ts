import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NuevaCitaComponent } from './nueva-cita';

describe('NuevaCitaComponent', () => {
  let component: NuevaCitaComponent;
  let fixture: ComponentFixture<NuevaCitaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NuevaCitaComponent], // si es standalone
    }).compileComponents();

    fixture = TestBed.createComponent(NuevaCitaComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});