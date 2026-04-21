import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditarDroga } from './editar-droga';

describe('EditarDroga', () => {
  let component: EditarDroga;
  let fixture: ComponentFixture<EditarDroga>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditarDroga],
    }).compileComponents();

    fixture = TestBed.createComponent(EditarDroga);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
