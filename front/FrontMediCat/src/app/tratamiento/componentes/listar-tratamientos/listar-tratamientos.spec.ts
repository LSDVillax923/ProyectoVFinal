import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListarTratamientos } from './listar-tratamientos';

describe('ListarTratamientos', () => {
  let component: ListarTratamientos;
  let fixture: ComponentFixture<ListarTratamientos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListarTratamientos],
    }).compileComponents();

    fixture = TestBed.createComponent(ListarTratamientos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
