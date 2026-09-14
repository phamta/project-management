import axiosInstance from "./axios";
import { API } from "@/services/config"

export const projectService = {
    async getProjectInWorkspace(id) {
        const {data} = await axiosInstance.get(API.PROJECTS.LIST(id))
        return data;
    }
}

export default projectService;