export const API = {
  AUTH: {
    LOGIN: "/auth/login",
    REGISTER: "/auth/register",
    LOGOUT: "/auth/logout",
    REFRESH: "/auth/refresh",
    ME: "/auth/me",
    FORGOT_PASSWORD: "/auth/forgot-password",
    RESET_PASSWORD: "/auth/reset-password",
  },
  USERS: {
    BASE: "/users",
    ME: "/users/me",
    BY_ID: (id) => `/users/${id}`,
  },
  WORKSPACES: {
    BASE: "/workspaces",
    BY_ID: (id) => `/workspaces/${id}`,
  },
  TASKS: {
    BASE: "/tasks",
    BY_ID: (id) => `/tasks/${id}`,
    COMMENTS: (taskId) => `/tasks/${taskId}/comments`,
  },
}