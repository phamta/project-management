import axiosInstance from "@/services/axios"
import { API } from "@/services/config"

const commentService = {
  getComments: async (taskId) => {
    const response = await axiosInstance.get(API.COMMENTS.BY_TASK(taskId))
    return response.data
  },
  createComment: async (payload) => {
    const response = await axiosInstance.post(API.COMMENTS.CREATE, payload)
    return response.data
  }
}

export default commentService