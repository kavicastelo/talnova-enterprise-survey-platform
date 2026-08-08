export type NotificationChannel = 'EMAIL' | 'SMS' | 'TEAMS' | 'SLACK' | 'KIOSK_PIN' | 'QR_CODE';

export type NotificationStatus = 'QUEUED' | 'SENT' | 'DELIVERED' | 'FAILED';

export interface NotificationRequest {
  projectId?: string;
  recipient: string;
  channel: NotificationChannel;
  subject?: string;
  messageBody: string;
  metadata?: Record<string, string>;
}

export interface NotificationResponse {
  notificationId: string;
  projectId: string;
  recipient: string;
  channel: NotificationChannel;
  status: NotificationStatus;
  sentAt?: string;
  errorMessage?: string;
}
