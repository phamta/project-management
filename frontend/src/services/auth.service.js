import axiosInstance from "@/services/axios"
import { API } from "@/services/config"

export const authService = {
  /**
   * Đăng nhập
   * @param {{ email: string, password: string }} payload
   * @returns {Promise<{ accessToken: string, user: object }>}
   */
  async login(payload) {
    const { data } = await axiosInstance.post(API.AUTH.LOGIN, payload)
    return data
    // Backend nên trả về:
    // { accessToken: "...", user: {...} }
    // và set refreshToken vào HttpOnly cookie
  },

  /**
   * Đăng ký
   */
  async register(payload) {
    const { data } = await axiosInstance.post(API.AUTH.REGISTER, payload)
    return data
  },

  /**
   * Đăng xuất — backend sẽ xóa refresh cookie
   */
  async logout() {
    const { data } = await axiosInstance.post(API.AUTH.LOGOUT)
    return data
  },

  /**
   * Lấy thông tin user hiện tại (dùng access token)
   */
  async getMe() {
    const { data } = await axiosInstance.get(API.AUTH.ME)
    return data
  },

  /**
   * Refresh access token (dùng refresh cookie)
   * Thường được gọi tự động bởi interceptor, ít khi gọi tay
   */
  async refresh() {
    const { data } = await axiosInstance.post(API.AUTH.REFRESH)
    return data
  },

  async forgotPassword(email) {
    const { data } = await axiosInstance.post(API.AUTH.FORGOT_PASSWORD, { email })
    return data
  },

  async resetPassword(payload) {
    const { data } = await axiosInstance.post(API.AUTH.RESET_PASSWORD, payload)
    return data
  },
}

export default authService