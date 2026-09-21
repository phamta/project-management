import axiosInstance from "./axios";
import { API } from "@/services/config"

export const workspaceService = {
    // Lấy danh sách workspace của user hiện tại
    async getWorkspaces() {
        const { data } = await axiosInstance.get(API.WORKSPACES.LIST);
        return data;
    },

    // ⭐ Tạo workspace mới
    async createWorkspace(payload) {
        // payload: { name, description }
        const { data } = await axiosInstance.post(API.WORKSPACES.CREATE, payload)
        return data
    },

    // (bonus) Lấy chi tiết 1 workspace
    async getWorkspaceById(id) {
        const { data } = await axiosInstance.get(API.WORKSPACES.BY_ID(id))
        return data
    },

    // (bonus) Xóa workspace
    async deleteWorkspace(id) {
        const { data } = await axiosInstance.delete(API.WORKSPACES.BY_ID(id))
        return data
    },

    async getWorkspaceMembers(workspaceId) {
        const { data } = await axiosInstance.get(API.WORKSPACES.MEMBERS(workspaceId));
        return data;
    }
}

export default workspaceService;