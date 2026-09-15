import axiosInstance from "./axios";
import { API } from "@/services/config";

export const taskService = {
  async getTasksByProject(projectId, params = {}) {
    const { data } = await axiosInstance.get(API.TASKS.BY_PROJECT(projectId), {
      params,
    });
    return data;
  },

  async createTask(projectId, payload) {
    const { data } = await axiosInstance.post(API.TASKS.CREATE, {
      ...payload,
      projectId,
    });
    return data;
  },

  async updateTask(id, payload) {
    const { data } = await axiosInstance.patch(API.TASKS.BY_ID(id), payload);
    return data;
  },

  async updateTaskStatus(id, status) {
    const { data } = await axiosInstance.patch(
      `${API.TASKS.BY_ID(id)}/status`,
      { status },
    );
    return data;
  },

  async deleteTask(id) {
    const { data } = await axiosInstance.delete(API.TASKS.BY_ID(id));
    return data;
  },
};

export default taskService;
