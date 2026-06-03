import { Component, OnInit } from '@angular/core';
import { TitleCasePipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TaskService } from '../../../services/task.service';
import { Task, TaskPriority, TaskRequest, TaskStatus } from '../../../models/task.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule, TitleCasePipe, DatePipe],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  activeTasks: Task[] = [];
  completedTasks: Task[] = [];

  taskForm: FormGroup;

  priorities = Object.values(TaskPriority);

  loading = {
    activeTasks: true,
    completedTasks: true,
    taskForm: false
  };

  error = {
    activeTasks: '',
    completedTasks: '',
    taskForm: ''
  };

  constructor(
    private taskService: TaskService,
    private fb: FormBuilder
  ) {
    this.taskForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', Validators.maxLength(500)],
      taskPriority: [TaskPriority.MEDIUM, Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadActiveTasks();
    this.loadCompletedTasks();
  }

  loadActiveTasks(): void {
    this.loading.activeTasks = true;
    this.error.activeTasks = '';

    this.taskService.getTasksByStatus(TaskStatus.IN_PROGRESS).subscribe({
      next: (tasks) => {
        this.activeTasks = tasks;
        this.loading.activeTasks = false;
      },
      error: (err) => {
        console.error('Failed to load active tasks', err);
        this.error.activeTasks = 'Failed to load tasks. Please try again.';
        this.loading.activeTasks = false;
      }
    });
  }

  loadCompletedTasks(): void {
    this.loading.completedTasks = true;
    this.error.completedTasks = '';

    this.taskService.getTasksByStatus(TaskStatus.COMPLETED).subscribe({
      next: (tasks) => {
        this.completedTasks = tasks;
        this.loading.completedTasks = false;
      },
      error: (err) => {
        console.error('Failed to load completed tasks', err);
        this.error.completedTasks = 'Failed to load completed tasks. Please try again.';
        this.loading.completedTasks = false;
      }
    });
  }

  onCreateTask(): void {
    if (this.taskForm.invalid || this.loading.taskForm) {
      return;
    }

    this.loading.taskForm = true;
    this.error.taskForm = '';

    const taskRequest: TaskRequest = this.taskForm.value;

    this.taskService.createTask(taskRequest).subscribe({
      next: (newTask) => {
        // Add the new task to the active tasks list
        this.activeTasks = [newTask, ...this.activeTasks];

        // Reset the form
        this.taskForm.reset({
          taskPriority: TaskPriority.MEDIUM
        });

        this.loading.taskForm = false;
      },
      error: (err) => {
        console.error('Failed to create task', err);
        this.error.taskForm = 'Failed to create task. Please try again.';
        this.loading.taskForm = false;
      }
    });
  }

  completeTask(taskId: number): void {
    this.taskService.completeTask(taskId).subscribe({
      next: (completedTask) => {
        // Remove from active tasks
        this.activeTasks = this.activeTasks.filter(task => task.id !== taskId);

        // Add to completed tasks
        this.completedTasks = [completedTask, ...this.completedTasks];
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
          // Remove from the appropriate list
          if (isCompleted) {
            this.completedTasks = this.completedTasks.filter(task => task.id !== taskId);
          } else {
            this.activeTasks = this.activeTasks.filter(task => task.id !== taskId);
          }
        },
        error: (err) => {
          console.error('Failed to delete task', err);
          // Show an error notification or message
        }
      });
    }
  }

  getPriorityClass(priority: TaskPriority): string {
    switch (priority) {
      case TaskPriority.HIGH:
        return 'priority-high';
      case TaskPriority.MEDIUM:
        return 'priority-medium';
      case TaskPriority.LOW:
        return 'priority-low';
      default:
        return '';
    }
  }
}
