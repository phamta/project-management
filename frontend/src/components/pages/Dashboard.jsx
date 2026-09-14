import { useEffect, useState, useCallback } from "react"
import { useNavigate } from "react-router-dom";

import WorkspaceGrid from "@/components/workspace/WorkspaceGrid"
import CreateWorkspaceModal from "@/components/workspace/CreateWorkspaceModal"
import workspaceService from "@/services/workspace.service"
// import { toast } from "@/components/ui/use-toast"

function Dashboard() {
  const [workspaces, setWorkspaces] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const navigate = useNavigate()

  // Modal state
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [submitting, setSubmitting] = useState(false)

  // ============ Fetch workspaces ============
  const fetchWorkspaces = useCallback(async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await workspaceService.getWorkspaces()
      setWorkspaces(data?.content ?? data ?? [])
    } catch (err) {
      const message =
        err.response?.data?.message ||
        err.message ||
        "Không tải được danh sách workspace"
      setError(message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchWorkspaces()
  }, [fetchWorkspaces])

  // ============ Handlers ============
  const handleOpenCreate = () => setIsModalOpen(true)

  const handleSelect = (ws) => {
    console.log("Select workspace:", ws.name)
    // TODO: navigate(`/workspaces/${ws.id}`)
    navigate(`/workspaces/${ws.id}`)
  }

  // ⭐ Xử lý submit form tạo workspace
  const handleCreateWorkspace = async (values) => {
    try {
      setSubmitting(true)

      const newWs = await workspaceService.createWorkspace({
        name: values.name.trim(),
        description: values.description?.trim() || "",
      })

      // Cách 1: Thêm vào đầu danh sách (optimistic)
      // setWorkspaces((prev) => [newWs, ...prev])

      // Cách 2: Refetch lại toàn bộ (an toàn hơn, đảm bảo data đồng bộ)
      await fetchWorkspaces()

      setIsModalOpen(false)
      // toast.success(`Đã tạo workspace "${newWs.name}"`)
    } catch (err) {
      const message =
        err.response?.data?.message ||
        err.message ||
        "Tạo workspace thất bại"
      // toast.error(message)
      // Không đóng modal để user sửa lại
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="font-heading text-2xl font-semibold tracking-tight">
          Dashboard
        </h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Welcome to CollabFlow
        </p>
      </div>

      {/* Workspaces Section */}
      <section>
        <div className="mb-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <h2 className="text-base font-semibold">Workspaces</h2>
            <span className="rounded-md bg-muted px-2 py-0.5 text-xs font-medium text-muted-foreground">
              {workspaces.length}
            </span>
          </div>
        </div>

        {/* Loading */}
        {loading && (
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            {Array.from({ length: 4 }).map((_, i) => (
              <div key={i} className="h-40 animate-pulse rounded-xl bg-muted" />
            ))}
          </div>
        )}

        {/* Error */}
        {!loading && error && (
          <div className="rounded-xl border border-destructive/30 bg-destructive/5 p-4 text-sm text-destructive">
            {error}
          </div>
        )}

        {/* Data */}
        {!loading && !error && (
          <WorkspaceGrid
            workspaces={workspaces}
            onCreate={handleOpenCreate}
            onSelect={handleSelect}
          />
        )}
      </section>

      {/* ⭐ Modal tạo workspace */}
      <CreateWorkspaceModal
        open={isModalOpen}
        onOpenChange={setIsModalOpen}
        onSubmit={handleCreateWorkspace}
        submitting={submitting}
      />
    </div>
  )
}

export default Dashboard