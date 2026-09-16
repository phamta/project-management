import axiosInstance from "./axios";
import { API } from "@/services/config"

export const notificationService = {
    async getNotifications() {
        const { data } = await axiosInstance.get(API.NOTIFICATIONS.LIST)
        return data;
    },

    async getUnreadNotifications() {
        const { data } = await axiosInstance.get(API.NOTIFICATIONS.UNREAD)
        return data;
    }
}

export default notificationService;