export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp?: string;
  correlationId?: string;
}

export interface ApiErrorPayload {
  success: false;
  message: string;
  errorCode?: string;
  status?: number;
  timestamp?: string;
  errors?: Record<string, string>;
}

export class ApiError extends Error {
  public errorCode: string;
  public status: number;
  public details?: Record<string, string>;

  constructor(message: string, status: number = 500, errorCode: string = 'UNKNOWN_ERROR', details?: Record<string, string>) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.errorCode = errorCode;
    this.details = details;
  }
}

export interface PaginatedResult<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
