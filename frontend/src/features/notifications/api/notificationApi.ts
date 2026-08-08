import { apiClient } from '../../../services/api/client';
import { ApiResponse } from '../../../types/api';
import { NotificationRequest, NotificationResponse } from '../../../types/notification';

const BASE_URL = '/notifications';

export const notificationApi = {
  async sendNotification(payload: NotificationRequest): Promise<NotificationResponse> {
    const res = await apiClient.post<NotificationResponse | ApiResponse<NotificationResponse>>(
      `${BASE_URL}/send`,
      payload
    );
    return (res as any).data || res;
  },

  async getNotificationStatus(notificationId: string): Promise<NotificationResponse> {
    const res = await apiClient.get<NotificationResponse | ApiResponse<NotificationResponse>>(
      `${BASE_URL}/status/${notificationId}`
    );
    return (res as any).data || res;
  },
};
