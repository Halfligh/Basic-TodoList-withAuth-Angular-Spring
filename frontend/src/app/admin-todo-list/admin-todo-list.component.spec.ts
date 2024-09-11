import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminTodoListComponent } from './admin-todo-list.component';

describe('AdminTodoListComponent', () => {
  let component: AdminTodoListComponent;
  let fixture: ComponentFixture<AdminTodoListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTodoListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminTodoListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
