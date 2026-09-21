import { useEffect, useState, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, Loader2, Plus } from "lucide-react";

import { Button } from "@/components/ui/button";
import TaskBoard from "@/components/task/TaskBoard";
import CreateTaskModal from "@/components/task/CreateTaskModal";
import taskService from "@/services/task.service";
import projectService from "@/services/project.service";
import workspaceService from "@/services/workspace.service";
import { normalizeListResponse, normalizeItemResponse } from "@/lib/api-utils";

function ProjectDetail() {
  const { id: projectId } = useParams();
  console.log("ProjectDetail: projectId =", projectId);
  const navigate = useNavigate();

  const [project, setProject] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingMembers, setLoadingMembers] = useState(false)
  const [error, setError] = useState(null);

  // Modal state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalDefaultStatus, setModalDefaultStatus] = useState("TODO");
  const [submitting, setSubmitting] = useState(false);

  // ============ Fetch project ============
  const fetchProject = useCallback(async () => {
    try {
      const data = await projectService.getProjectById(projectId);
      const p = normalizeItemResponse(data);
      setProject(p);

      // ⭐ Load members của workspace chứa project
      if (p?.workspaceId) {
        try {
          const membersData = await workspaceService.getWorkspaceMembers(
            p.workspaceId,
          );
          setMembers(normalizeListResponse(membersData));
        } catch (err) {
          console.warn("Không load được members:", err);
        }
      }
    } catch (err) {
      console.error("Fetch project failed:", err);
    }
  }, [projectId]);

  // ============ Fetch task ============
  const fetchTasks = useCallback(async () => {
    try {
      setLoading(true);
      const data = await taskService.getTasksByProject(projectId);
      setTasks(normalizeListResponse(data));
    } catch (err) {
      setError(
        err.response?.data?.message || err.message || "Không tải được tasks",
      );
    } finally {
      setLoading(false);
    }
  }, [projectId]);

  // ============ Fetch workspace members ============
  const fetchMembers = useCallback(async () => {
    if (!projectId) {
      console.warn("fetchMembers: projectId trống")
      setMembers([])
      return
    }
    try {
      setLoadingMembers(true)
      const data = await projectService.getProjectMembers(projectId)
      // Backend trả: { success, message, data: [...] }
      setMembers(normalizeListResponse(data))
    } catch (err) {
      console.error("Fetch members failed:", err)
      setMembers([])
    } finally {
      setLoadingMembers(false)
    }
  }, [projectId])

  useEffect(() => {
    fetchProject();
    fetchTasks();
    fetchMembers();
  }, [fetchProject, fetchTasks, fetchMembers]);

  // ============ Handlers ============
  const handleBack = () => navigate(-1);

  const handleTasksChange = (updater) => {
    setTasks((prev) =>
      typeof updater === "function" ? updater(prev) : updater,
    );
  };

  const handleStatusChange = async (taskId, newStatus) => {
    try {
      await taskService.updateTaskStatus(taskId, newStatus);
    } catch (err) {
      console.error("Update status failed:", err);
      alert(err.response?.data?.message || "Cập nhật trạng thái thất bại");
      fetchTasks();
    }
  };

  const handleSelectTask = (task) => {
    navigate(`/tasks/${task.id}`);
  };

  // ⭐ Mở modal với status mặc định
  const handleCreateInColumn = (status = "TODO") => {
    setModalDefaultStatus(status);
    setIsModalOpen(true);
  };

  // ⭐ Submit tạo task
  const handleSubmitTask = async (values) => {
    try {
      setSubmitting(true);
      const data = await taskService.createTask(projectId, values);
      const created = normalizeItemResponse(data);

      // Thêm vào đầu danh sách tasks
      setTasks((prev) => [created, ...prev]);
      setIsModalOpen(false);
    } catch (err) {
      console.error("Create task failed:", err);
      alert(err.response?.data?.message || "Tạo task thất bại");
      // Không đóng modal để user sửa
    } finally {
      setSubmitting(false);
    }
  };

  // ============ Render ============
  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Header */}
      <div>
        <Button
          variant="ghost"
          size="sm"
          onClick={handleBack}
          className="-ml-2 mb-2 text-muted-foreground"
        >
          <ArrowLeft className="mr-1 h-4 w-4" />
          Quay lại
        </Button>

        <div className="flex items-start justify-between gap-4">
          <div>
            <h1 className="font-heading text-2xl font-semibold tracking-tight">
              {project?.name || "Project"}
            </h1>
            {project?.description && (
              <p className="mt-1 text-sm text-muted-foreground">
                {project.description}
              </p>
            )}
          </div>

          <Button
            onClick={() => handleCreateInColumn("TODO")}
            className="shrink-0"
          >
            <Plus className="mr-2 h-4 w-4" />
            Tạo task
          </Button>
        </div>
      </div>

      {/* Board */}
      {error ? (
        <div className="rounded-xl border border-destructive/30 bg-destructive/5 p-4 text-sm text-destructive">
          {error}
        </div>
      ) : (
        <TaskBoard
          tasks={tasks}
          onTasksChange={handleTasksChange}
          onStatusChange={handleStatusChange}
          onSelectTask={handleSelectTask}
          onCreateInColumn={handleCreateInColumn}
        />
      )}

      {/* ⭐ Modal */}
      <CreateTaskModal
        open={isModalOpen}
        onOpenChange={setIsModalOpen}
        onSubmit={handleSubmitTask}
        submitting={submitting}
        projectId={projectId}
        defaultStatus={modalDefaultStatus}
        members={members}
      />
    </div>
  );
}

export default ProjectDetail;
