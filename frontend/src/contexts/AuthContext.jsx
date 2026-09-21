import { createContext, useContext, useEffect, useState, useCallback } from "react"
import authService from "@/services/auth.service"
import {
  setAccessToken,
  clearAccessToken,
  setOnUnauthorized,
} from "@/services/axios"

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  // ⭐ Token lưu trong React state (memory), KHÔNG localStorage
  const [user, setUser] = useState(null)
  const [accessToken, setAccessTokenState] = useState(null)
  const [loading, setLoading] = useState(true) // Đang check auth lúc mount
  const [error, setError] = useState(null)

  // Đồng bộ state token → axios module
  const updateToken = useCallback((token) => {
    setAccessTokenState(token)
    setAccessToken(token) // cho axios interceptor dùng
  }, [])

  const clearAuth = useCallback(() => {
    setUser(null)
    setAccessTokenState(null)
    clearAccessToken()
  }, [])

  // ============ Login ============
  const login = useCallback(
    async (credentials) => {
      setError(null)
      try {
        const data = await authService.login(credentials)
        // data: { accessToken, user }
        updateToken(data.accessToken || data.token)
        setUser(data.user)
        return data
      } catch (err) {
        const message =
          err.response?.data?.message ||
          err.message ||
          "Đăng nhập thất bại"
        setError(message)
        throw new Error(message)
      }
    },
    [updateToken]
  )

  // ============ Register ============
  const register = useCallback(
    async (payload) => {
      setError(null)
      try {
        const data = await authService.register(payload)
        return data
      } catch (err) {
        const message =
          err.response?.data?.message || err.message || "Đăng ký thất bại"
        setError(message)
        throw new Error(message)
      }
    },
    []
  )

  // ============ Logout ============
  const logout = useCallback(async () => {
    try {
      await authService.logout()
    } catch (err) {
      // Dù API fail vẫn clear local state
      console.warn("Logout API failed:", err)
    } finally {
      clearAuth()
    }
  }, [clearAuth])

  // ============ Bootstrap: check auth khi mount ============
  // Vì accessToken nằm trong memory → mất khi F5.
  // Refresh token nằm trong cookie → thử gọi /auth/refresh để lấy lại.
  useEffect(() => {
    let mounted = true

    const bootstrap = async () => {
      try {
        // Thử refresh để lấy access token mới từ cookie
        const data = await authService.refresh()
        if (!mounted) return

        updateToken(data.accessToken || data.token)

        // Có token rồi → lấy user
        const me = await authService.getMe()
        if (!mounted) return
        setUser(me.user || me)
      } catch (err) {
        // Không có refresh cookie hợp lệ → coi như chưa login
        if (mounted) clearAuth()
      } finally {
        if (mounted) setLoading(false)
      }
    }

    bootstrap()
    return () => {
      mounted = false
    }
  }, [updateToken, clearAuth])

  // ============ Đăng ký callback khi interceptor báo 401 hết hạn ============
  useEffect(() => {
    setOnUnauthorized(() => {
      clearAuth()
    })
  }, [clearAuth])

  const value = {
    user,
    accessToken,
    isAuthenticated: !!user,
    loading,
    error,
    login,
    register,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// ============ Hook ============
export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error("useAuth must be used within <AuthProvider>")
  }
  return ctx
}

export default AuthContext