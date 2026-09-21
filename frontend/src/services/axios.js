import axios from "axios"

const BASE_URL = import.meta.env.VITE_BASE_URL || "http://localhost:8080/api"

// ============ Token storage (in-memory) ============
// Access token lưu trong memory (React state) — KHÔNG localStorage
// để tránh XSS. Refresh token lưu trong HttpOnly cookie do backend set.
let accessToken = null
let onUnauthorized = null

export const setAccessToken = (token) => {
  accessToken = token
}

export const getAccessToken = () => accessToken

export const clearAccessToken = () => {
  accessToken = null
}

// Callback để AuthContext đăng ký — gọi khi refresh fail
export const setOnUnauthorized = (cb) => {
  onUnauthorized = cb
}

// ============ Axios instance ============
const axiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
  withCredentials: true, // ⭐ Quan trọng: gửi kèm HttpOnly cookie (refresh token)
  headers: {
    "Content-Type": "application/json",
  },
})

// ============ Request interceptor ============
axiosInstance.interceptors.request.use(
  (config) => {
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// ============ Response interceptor ============
let isRefreshing = false
let failedQueue = []

const processQueue = (error, token = null) => {
  failedQueue.forEach((prom) => {
    if (error) prom.reject(error)
    else prom.resolve(token)
  })
  failedQueue = []
}

axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    // Không phải 401, hoặc đã retry rồi, hoặc là request refresh → trả lỗi luôn
    if (
      error.response?.status !== 401 ||
      originalRequest._retry ||
      originalRequest.url?.includes("/auth/refresh") ||
      originalRequest.url?.includes("/auth/login")
    ) {
      return Promise.reject(error)
    }

    // Nếu đang refresh → đợi refresh xong
    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        failedQueue.push({ resolve, reject })
      })
        .then((token) => {
          originalRequest.headers.Authorization = `Bearer ${token}`
          return axiosInstance(originalRequest)
        })
        .catch((err) => Promise.reject(err))
    }

    originalRequest._retry = true
    isRefreshing = true

    try {
      // Gọi refresh — refresh token nằm trong HttpOnly cookie,
      // nên không cần truyền gì trong body
      const { data } = await axios.post(
        `${BASE_URL}/auth/refresh`,
        {},
        { withCredentials: true }
      )

      const newToken = data.accessToken || data.token
      setAccessToken(newToken)
      processQueue(null, newToken)

      originalRequest.headers.Authorization = `Bearer ${newToken}`
      return axiosInstance(originalRequest)
    } catch (refreshError) {
      processQueue(refreshError, null)
      clearAccessToken()
      onUnauthorized?.() // Báo cho AuthContext logout
      return Promise.reject(refreshError)
    } finally {
      isRefreshing = false
    }
  }
)

export default axiosInstance