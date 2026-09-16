import axiosInstance from "./axios";
import { API } from "@/services/config"

export const notificationService = {
    async getUnreadNotifications() {
        const { data } = await axiosInstance.get(API.NOTIFICATIONS.UNREAD)
        return data;
    },

    async getMyNotifications()  {
        const res = await axios.get(API.NOTIFICATIONS.LIST);
        return res.data; // ApiResponse<PageResponse<NotificationResponse>>
    },

    async markAsRead(id){
        const res = await axios.patch(API.NOTIFICATIONS.MARK_AS_READ(id));
        return res.data;
    },

    async markAllAsRead() {
        const res = await axios.patch(API.NOTIFICATIONS.MARK_ALL_AS_READ);
        return res.data;
    },
}

export default notificationService;