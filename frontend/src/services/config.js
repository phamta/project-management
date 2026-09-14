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
    LIST: "/workspaces",
    CREATE: "/workspaces",
    UPDATE: (id) => `/workspaces/${id}`,
    DELETE: (id) => `/workspaces/${id}`,
    BY_ID: (id) => `/workspaces/${id}`,
  },
  PROJECTS: {
    LIST: (id) => `/workspaces/${id}`,
  },
  TASKS: {
    BASE: "/tasks",
    BY_ID: (id) => `/tasks/${id}`,
    COMMENTS: (taskId) => `/tasks/${taskId}/comments`,
  },
}