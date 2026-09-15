import { useEffect, useState, useCallback } from "react"
import workspaceService from "@/services/workspace.service"
import { normalizeListResponse } from "@/lib/api-utils"

export function useWorkspaceMembers(workspaceId) {
  const [members, setMembers] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const fetch = useCallback(async () => {
    if (!workspaceId) {
      setMembers([])
      return
    }
    try {
      setLoading(true)
      setError(null)
      const data = await workspaceService.getWorkspaceMembers(workspaceId)
      // Backend trả: { success, message, data: [...] }
      setMembers(normalizeListResponse(data))
    } catch (err) {
      console.error("Fetch workspace members failed:", err)
      setError(
        err.response?.data?.message ||
          err.message ||
          "Không tải được danh sách thành viên"
      )
      setMembers([])
    } finally {
      setLoading(false)
    }
  }, [workspaceId])

  useEffect(() => {
    fetch()
  }, [fetch])

  return { members, loading, error, refetch: fetch }
}