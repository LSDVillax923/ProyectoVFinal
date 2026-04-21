import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListarCitas } from './listar-citas';

describe('ListarCitas', () => {
  let component: ListarCitas;
  let fixture: ComponentFixture<ListarCitas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListarCitas],
    }).compileComponents();

    fixture = TestBed.createComponent(ListarCitas);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
