package com.talnova.tesp.common.context;

public class ProjectContextHolder {

    private static final ThreadLocal<String> PROJECT_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> CORRELATION_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_ID_HOLDER = new ThreadLocal<>();

    public static void setProjectId(String projectId) {
        PROJECT_ID_HOLDER.set(projectId);
    }

    public static String getProjectId() {
        return PROJECT_ID_HOLDER.get();
    }

    public static void setCorrelationId(String correlationId) {
        CORRELATION_ID_HOLDER.set(correlationId);
    }

    public static String getCorrelationId() {
        return CORRELATION_ID_HOLDER.get();
    }

    public static void setUserId(String userId) {
        USER_ID_HOLDER.set(userId);
    }

    public static String getUserId() {
        return USER_ID_HOLDER.get();
    }

    public static void clear() {
        PROJECT_ID_HOLDER.remove();
        CORRELATION_ID_HOLDER.remove();
        USER_ID_HOLDER.remove();
    }
}
