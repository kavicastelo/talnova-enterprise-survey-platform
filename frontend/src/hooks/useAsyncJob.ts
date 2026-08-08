import { useState, useEffect } from 'react';

export type JobStatus = 'IDLE' | 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';

export interface JobStatusResponse<T> {
  status: JobStatus;
  progress?: number;
  data?: T;
  error?: string;
}

export interface UseAsyncJobOptions<T> {
  jobId: string | null;
  fetchStatusFn: (jobId: string) => Promise<JobStatusResponse<T>>;
  pollIntervalMs?: number;
  onSuccess?: (data: T) => void;
  onError?: (error: string) => void;
}

export function useAsyncJob<T>({
  jobId,
  fetchStatusFn,
  pollIntervalMs = 2000,
  onSuccess,
  onError,
}: UseAsyncJobOptions<T>) {
  const [status, setStatus] = useState<JobStatus>('IDLE');
  const [progress, setProgress] = useState<number>(0);
  const [result, setResult] = useState<T | null>(null);
  const [errorDetails, setErrorDetails] = useState<string | null>(null);

  useEffect(() => {
    if (!jobId) {
      setStatus('IDLE');
      setProgress(0);
      setResult(null);
      setErrorDetails(null);
      return;
    }

    setStatus('PENDING');

    const interval = setInterval(async () => {
      try {
        const res = await fetchStatusFn(jobId);
        setStatus(res.status);
        if (res.progress !== undefined) setProgress(res.progress);

        if (res.status === 'COMPLETED') {
          clearInterval(interval);
          setResult(res.data || null);
          if (onSuccess && res.data) onSuccess(res.data);
        } else if (res.status === 'FAILED') {
          clearInterval(interval);
          setErrorDetails(res.error || 'Async job execution failed');
          if (onError) onError(res.error || 'Async job execution failed');
        }
      } catch (err: any) {
        clearInterval(interval);
        setStatus('FAILED');
        setErrorDetails(err.message || 'Failed to query background job status');
      }
    }, pollIntervalMs);

    return () => clearInterval(interval);
  }, [jobId]);

  return { status, progress, result, errorDetails };
}
