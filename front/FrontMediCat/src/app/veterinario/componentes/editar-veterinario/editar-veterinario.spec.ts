import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditarVeterinario } from './editar-veterinario';

describe('EditarVeterinario', () => {
  let component: EditarVeterinario;
  let fixture: ComponentFixture<EditarVeterinario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditarVeterinario],
    }).compileComponents();

    fixture = TestBed.createComponent(EditarVeterinario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
