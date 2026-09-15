import { useEffect, useState, useCallback } from "react"
import { useParams, useNavigate } from "react-router-dom"
import { ArrowLeft, FolderOpen, Loader2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import ProjectGrid from "@/components/project/ProjectGrid"
import CreateProjectModal from "@/components/project/CreateProjectModal"
import workspaceService from "@/services/workspace.service"
import projectService from "@/services/project.service"
import { normalizeListResponse, normalizeItemResponse } from "@/lib/api-utils"

function WorkspaceDetail() {
  const { id: workspaceId } = useParams()
  const navigate = useNavigate()

  const [workspace, setWorkspace] = useState(null)
  const [projects, setProjects] = useState([])

  const [loadingWorkspace, setLoadingWorkspace] = useState(true)
  const [loadingProjects, setLoadingProjects] = useState(true)
  const [error, setError] = useState(null)

  // Modal tạo project
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [submitting, setSubmitting] = useState(false)

  // ============ Fetch workspace ============
  const fetchWorkspace = useCallback(async () => {
    try {
      setLoadingWorkspace(true)
      const data = await workspaceService.getWorkspaceById(workspaceId)
      setWorkspace(normalizeItemResponse(data))
    } catch (err) {
      console.error("Fetch workspace failed:", err)
      setError(
        err.response?.data?.message ||
          err.message ||
          "Không tải được workspace"
      )
    } finally {
      setLoadingWorkspace(false)
    }
  }, [workspaceId])

  // ============ Fetch projects ============
  const fetchProjects = useCallback(async () => {
    try {
      setLoadingProjects(true)
      const data = await projectService.getProjectInWorkspace(workspaceId)
      setProjects(normalizeListResponse(data))
    } catch (err) {
      console.error("Fetch projects failed:", err)
      // Không set error toàn cục — chỉ log để workspace vẫn hiện
    } finally {
      setLoadingProjects(false)
    }
  }, [workspaceId])

  useEffect(() => {
    if (workspaceId) {
      fetchWorkspace()
      fetchProjects()
    }
  }, [workspaceId, fetchWorkspace, fetchProjects])

  // ============ Handlers ============
  const handleBack = () => navigate("/dashboard")

  const handleCreateProject = () => setIsModalOpen(true)

  const handleSelectProject = (project) => {
    navigate(`/projects/${project.id}`)
  }

  const handleSubmitProject = async (values) => {
    try {
      setSubmitting(true)
      const newProject = await projectService.createProject(workspaceId, {
        name: values.name.trim(),
        description: values.description?.trim() || "",
      })

      const created = normalizeItemResponse(newProject)
      setProjects((prev) => [created, ...prev])
      setIsModalOpen(false)
    } catch (err) {
      console.error("Create project failed:", err)
      alert(err.response?.data?.message || "Tạo project thất bại")
    } finally {
      setSubmitting(false)
    }
  }

  // ============ Loading toàn trang ============
  if (loadingWorkspace) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
      </div>
    )
  }

  if (error) {
    return (
      <div className="space-y-4">
        <Button variant="ghost" onClick={handleBack}>
          <ArrowLeft className="mr-2 h-4 w-4" />
          Back to Dashboard
        </Button>
        <div className="rounded-xl border border-destructive/30 bg-destructive/5 p-4 text-sm text-destructive">
          {error}
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <Button
          variant="ghost"
          size="sm"
          onClick={handleBack}
          className="-ml-2 mb-2 text-muted-foreground"
        >
          <ArrowLeft className="mr-1 h-4 w-4" />
          Workspaces
        </Button>

        <div className="flex items-start justify-between">
          <div>
            <h1 className="font-heading text-2xl font-semibold tracking-tight">
              {workspace?.name}
            </h1>
            {workspace?.description && (
              <p className="mt-1 text-sm text-muted-foreground">
                {workspace.description}
              </p>
            )}
            <p className="mt-2 text-xs text-muted-foreground">
              {workspace?.memberCount ?? 0} members ·{" "}
              {workspace?.projectCount ?? projects.length} projects
            </p>
          </div>
        </div>
      </div>

      {/* Projects Section */}
      <section>
        <div className="mb-4 flex items-center gap-2">
          <h2 className="text-base font-semibold">Projects</h2>
          <span className="rounded-md bg-muted px-2 py-0.5 text-xs font-medium text-muted-foreground">
            {projects.length}
          </span>
        </div>

        {/* Loading */}
        {loadingProjects && (
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            {Array.from({ length: 3 }).map((_, i) => (
              <div key={i} className="h-40 animate-pulse rounded-xl bg-muted" />
            ))}
          </div>
        )}

        {/* Data */}
        {!loadingProjects && (
          <ProjectGrid
            projects={projects}
            onCreate={handleCreateProject}
            onSelect={handleSelectProject}
          />
        )}
      </section>

      {/* Modal tạo project — tạo file riêng tương tự CreateWorkspaceModal */}
      <CreateProjectModal
        open={isModalOpen}
        onOpenChange={setIsModalOpen}
        onSubmit={handleSubmitProject}
        submitting={submitting}
      />
    </div>
  )
}

export default WorkspaceDetail