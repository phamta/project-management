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
    MEMBERS: (workspaceId) => `/workspaces/${workspaceId}/members`,
  },
  PROJECTS: {
    LIST: (id) => `/projects/workspace/${id}`,
    MEMBERS: (projectId) => `/projects/${projectId}/members`,
    CREATE: (workspaceId) => `/projects/workspace/${workspaceId}`,
    BY_ID: (id) => `/projects/${id}`,
  },
  TASKS: {
    BY_PROJECT: (projectId) => `/tasks/project/${projectId}`,
    CREATE: "/tasks",
    UPDATE: (id) => `/tasks/${id}`,
    UPDATE_STATUS: (id) => `/tasks/${id}/status`,
    DELETE: (id) => `/tasks/${id}`,
    BY_ID: (id) => `/tasks/${id}`,
    COMMENTS: (taskId) => `/tasks/${taskId}/comments`,
  },
  NOTIFICATIONS: {
    LIST: "/notifications",
    UNREAD: "/notifications/unread/count",
    MARK_AS_READ: (id) => `/notifications/${id}/read`,
    MARK_ALL_AS_READ: "/notifications/read-all",
  },
  COMMENTS: {
    CREATE: "/comments",
    BY_TASK: (taskId) => `/comments/task/${taskId}`,
  }
}