import { useQuery, useMutation } from '@tanstack/react-query';
import { notificationApi } from './notificationApi';
import { NotificationRequest } from '../../../types/notification';
import { useToast } from '../../../context/ToastContext';

export function useSendNotificationMutation() {
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: (payload: NotificationRequest) => notificationApi.sendNotification(payload),
    onSuccess: (data) => {
      showSuccess(`Notification dispatched via ${data.channel} (ID: ${data.notificationId})`);
    },
    onError: (err: any) => {
      showError(err.message || 'Notification dispatch failed.');
    },
  });
}

export function useNotificationStatusQuery(notificationId: string | undefined) {
  return useQuery({
    queryKey: ['notification-status', notificationId],
    queryFn: () => notificationApi.getNotificationStatus(notificationId!),
    enabled: !!notificationId,
    refetchInterval: (query) => {
      const status = query.state.data?.status;
      if (status === 'QUEUED') return 2000;
      return false;
    },
  });
}
