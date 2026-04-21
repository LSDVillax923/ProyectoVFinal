import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListarDrogas } from './listar-drogas';

describe('ListarDrogas', () => {
  let component: ListarDrogas;
  let fixture: ComponentFixture<ListarDrogas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListarDrogas],
    }).compileComponents();

    fixture = TestBed.createComponent(ListarDrogas);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
