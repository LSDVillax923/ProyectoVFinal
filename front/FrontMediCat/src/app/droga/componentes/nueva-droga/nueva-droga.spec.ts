import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NuevaDroga } from './nueva-droga';

describe('NuevaDroga', () => {
  let component: NuevaDroga;
  let fixture: ComponentFixture<NuevaDroga>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NuevaDroga],
    }).compileComponents();

    fixture = TestBed.createComponent(NuevaDroga);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
