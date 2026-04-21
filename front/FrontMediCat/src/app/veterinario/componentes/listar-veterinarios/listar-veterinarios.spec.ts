import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListarVeterinarios } from './listar-veterinarios';

describe('ListarVeterinarios', () => {
  let component: ListarVeterinarios;
  let fixture: ComponentFixture<ListarVeterinarios>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListarVeterinarios],
    }).compileComponents();

    fixture = TestBed.createComponent(ListarVeterinarios);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
