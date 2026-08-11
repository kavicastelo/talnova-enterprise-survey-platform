export interface ApiErrorPayload {
  message: string;
  code?: string;
  status?: number;
  details?: Record<string, unknown>;
  timestamp?: string;
}

export class ApiError extends Error {
  public status: number;
  public code: string;
  public details?: Record<string, unknown>;

  constructor(message: string, status: number = 500, code: string = 'INTERNAL_ERROR', details?: Record<string, unknown>) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.code = code;
    this.details = details;
  }

  public static isApiError(error: unknown): error is ApiError {
    return error instanceof ApiError;
  }

  public static fromResponse(status: number, data: any): ApiError {
    const message = data?.message || data?.error || 'An unexpected error occurred';
    const code = data?.code || `HTTP_${status}`;
    const details = data?.details || data?.errors || undefined;
    return new ApiError(message, status, code, details);
  }
}
