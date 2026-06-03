
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import {filter, Observable, of} from 'rxjs';
import { catchError, startWith, switchMap } from 'rxjs/operators';
import { Task, TaskPriority, TaskStatus } from '../../../models/task.model';
import { TaskService } from '../../../services/task.service';
import { AsyncPipe, DatePipe, NgClass } from '@angular/common';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  imports: [
    NgClass,
    DatePipe,
    ReactiveFormsModule
  ],
  styleUrls: ['./search.component.scss'],
  standalone: true
})
export class SearchComponent implements OnInit {
  tasks$!: Observable<Task[]>;
  tasks: Task[] = [];
  filterForm!: FormGroup;

  TaskPriority = TaskPriority;
  TaskStatus = TaskStatus;

  priorities = [
    { label: 'High', value: TaskPriority.HIGH },
    { label: 'Medium', value: TaskPriority.MEDIUM },
    { label: 'Low', value: TaskPriority.LOW }
  ];

  statuses = [
    { label: 'In Progress', value: TaskStatus.IN_PROGRESS },
    { label: 'Completed', value: TaskStatus.COMPLETED }
  ];

  loading = true;
  error = false;

  constructor(
    private taskService: TaskService,
    private fb: FormBuilder
  ) { }

  ngOnInit(): void {
    this.filterForm = this.fb.group({
      priority: [null],
      status: [null]
    });

    // Initialize with all tasks and set up filter reactions
    this.tasks$ = this.filterForm.valueChanges.pipe(
      startWith(this.filterForm.value),
      switchMap(filters => this.fetchTasks(filters))
    );

    // Subscribe to the tasks$ observable to keep the tasks array updated
    this.tasks$.subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching tasks:', error);
        this.error = true;
        this.loading = false;
      }
    });
  }

  private fetchTasks(filters: { priority: TaskPriority | null, status: TaskStatus | null }): Observable<Task[]> {
    this.loading = true;
    this.error = false;

    let observable: Observable<Task[]>;

    // Determine which API call to make based on filters
    if (filters.priority && filters.status) {
      // Combined filters - fetch by priority and filter by status client-side
      observable = this.taskService.getTasksByPriority(filters.priority).pipe(
        switchMap(tasks => of(tasks.filter(task => task.status === filters.status)))
      );
    } else if (filters.priority) {
      // Only priority filter
      observable = this.taskService.getTasksByPriority(filters.priority);
    } else if (filters.status) {
      // Only status filter
      observable = this.taskService.getTasksByStatus(filters.status);
    } else {
      // No filters - get all tasks
      observable = this.taskService.getAllTasks();
    }

    return observable.pipe(
      catchError(error => {
        console.error('Error fetching filtered tasks:', error);
        this.error = true;
        this.loading = false;
        return of([]);
      })
    );
  }

  completeTask(taskId: number): void {
    this.taskService.completeTask(taskId).subscribe({
      next: (updatedTask) => {
        this.tasks = this.tasks.map(task =>
          task.id === taskId ? updatedTask : task
        );
      },
      error: (err) => {
        console.error('Failed to complete task', err);
        // Show an error notification or message
      }
    });
  }

  deleteTask(taskId: number, isCompleted = false): void {
    if (confirm('Are you sure you want to delete this task?')) {
      this.taskService.deleteTask(taskId).subscribe({
        next: () => {
          this.tasks = this.tasks.filter(task => task.id !== taskId);
        },
        error: (err) => {
          console.error('Failed to delete task', err);
          // Show an error notification or message
        }
      });
    }
  }

  resetFilters(): void {
    this.filterForm.reset({
      priority: null,
      status: null
    });
  }
  formatStatus(status: string): string {
    if (status === TaskStatus.IN_PROGRESS) {
      return 'In Progress';
    } else if (status === TaskStatus.COMPLETED) {
      return 'Completed';
    }
    return status.charAt(0).toUpperCase() + status.slice(1).replace('_', ' ');
  }
}
