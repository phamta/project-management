import axiosInstance from "./axios";
import { API } from "@/services/config"

export const projectService = {
    async getProjectInWorkspace(id) {
        const {data} = await axiosInstance.get(API.PROJECTS.LIST(id))
        return data;
    },
        /**
     * Tạo project mới trong workspace
     */
    async createProject(workspaceId, payload) {
        // payload: { name, description, status? }
        const { data } = await axiosInstance.post(
        API.PROJECTS.CREATE(workspaceId),
        payload
        )
        return data
    },

    async getProjectById(id) {
        const { data } = await axiosInstance.get(API.PROJECTS.BY_ID(id))
        return data
    },

    async deleteProject(id) {
        const { data } = await axiosInstance.delete(API.PROJECTS.BY_ID(id))
        return data
    },

    async getProjectMembers(projectId) {
        const { data } = await axiosInstance.get(API.PROJECTS.MEMBERS(projectId))
        return data
    },

    async addProjectMember(projectId, userId) {
        const { data } = await axiosInstance.post(
        API.PROJECTS.MEMBERS(projectId),
        { userId }
        )
        return data
    },

    async removeProjectMember(projectId, userId) {
        const { data } = await axiosInstance.delete(
        `${API.PROJECTS.MEMBERS(projectId)}?userId=${userId}`
        )
        return data
    }
}

export default projectService;