export enum TaskStatus {
  IN_PROGRESS = 'in_progress',
  COMPLETED = 'completed'
}

export enum TaskPriority {
  HIGH = 'high',
  MEDIUM = 'medium',
  LOW = 'low'
}

export interface Task {
  id: number;
  title: string;
  description: string;
  status: TaskStatus;
  taskPriority: TaskPriority;
  created_date: string;
}

export interface TaskRequest {
  title: string;
  description: string;
  taskPriority: TaskPriority;
  status?: TaskStatus;
}
