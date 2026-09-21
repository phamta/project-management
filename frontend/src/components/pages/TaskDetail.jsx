import { useEffect, useState, useCallback, useMemo } from "react"
import { useParams, useNavigate } from "react-router-dom"
import {
  ArrowLeft,
  Loader2,
  Calendar,
  Clock,
  User as UserIcon,
  AlertCircle,
  CheckCircle2,
} from "lucide-react"

import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Separator } from "@/components/ui/separator"
import { Badge } from "@/components/ui/badge"

import TaskPriorityBadge from "@/components/task/TaskPriorityBadge"
import TaskStatusSelect from "@/components/task/TaskStatusSelect"
import TaskCommentList from "@/components/task/TaskCommentList"
import TaskCommentForm from "@/components/task/TaskCommentForm"

import taskService from "@/services/task.service"
import commentService from "@/services/comment.service"
import { useAuth } from "@/contexts/AuthContext"
import {
  normalizeItemResponse,
  normalizeListResponse,
} from "@/lib/api-utils"
import { formatDate, formatRelative, isOverdue } from "@/lib/date"
import { cn } from "@/lib/utils"

// ============ Helpers ============
const avatarPalette = [
  "bg-indigo-500",
  "bg-emerald-500",
  "bg-blue-500",
  "bg-amber-500",
  "bg-rose-500",
]
function hashIndex(str = "", len) {
  let h = 0
  for (let i = 0; i < str.length; i++) h = str.charCodeAt(i) + ((h << 5) - h)
  return Math.abs(h) % len
}
function getInitials(name = "") {
  return (
    name
      .trim()
      .split(/\s+/)
      .map((w) => w[0])
      .slice(0, 2)
      .join("")
      .toUpperCase() || "?"
  )
}

// ============ Component ============
function TaskDetail() {
  const { id: taskId } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()

  const currentUserId = user?.id || user?.userId

  const [task, setTask] = useState(null)
  const [comments, setComments] = useState([])

  const [loadingTask, setLoadingTask] = useState(true)
  const [loadingComments, setLoadingComments] = useState(true)
  const [error, setError] = useState(null)

  // Comment form
  const [submittingComment, setSubmittingComment] = useState(false)
  const [replyTo, setReplyTo] = useState(null)

  // Status update
  const [updatingStatus, setUpdatingStatus] = useState(false)

  // ============ Fetch task ============
  const fetchTask = useCallback(async () => {
    try {
      setLoadingTask(true)
      setError(null)
      const data = await taskService.getTaskById(taskId)
      setTask(normalizeItemResponse(data))
    } catch (err) {
      console.error("Fetch task failed:", err)
      setError(
        err.response?.data?.message ||
          err.message ||
          "Không tải được thông tin task"
      )
    } finally {
      setLoadingTask(false)
    }
  }, [taskId])

  // ============ Fetch comments ============
  const fetchComments = useCallback(async () => {
    try {
      setLoadingComments(true)
      const data = await commentService.getComments(taskId)
      console.log("Fetched comments:", data)
      setComments(normalizeListResponse(data))
    } catch (err) {
      console.error("Fetch comments failed:", err)
      setComments([])
    } finally {
      setLoadingComments(false)
    }
  }, [taskId])

  useEffect(() => {
    if (taskId) {
      fetchTask()
      fetchComments()
    }
  }, [taskId, fetchTask, fetchComments])

  // ============ Handlers ============
  const handleBack = () => navigate(-1)

  const handleStatusChange = async (newStatus) => {
    if (newStatus === task.status || updatingStatus) return
    try {
      setUpdatingStatus(true)
      // Optimistic
      setTask((prev) => ({ ...prev, status: newStatus }))
      await taskService.updateTaskStatus(taskId, newStatus)
    } catch (err) {
      console.error("Update status failed:", err)
      alert(err.response?.data?.message || "Cập nhật trạng thái thất bại")
      fetchTask() // rollback
    } finally {
      setUpdatingStatus(false)
    }
  }

  const handleCreateComment = async (content, parentId = null) => {
    try {
      setSubmittingComment(true)
      const data = await commentService.createComment(taskId, {
        content,
        parentId,
      })
      const created = normalizeItemResponse(data)

      // Nếu là reply → gắn vào parent
      if (parentId) {
        setComments((prev) => {
          const next = prev.map((c) =>
            c.id === parentId
              ? { ...c, replies: [...(c.replies || []), created] }
              : c
          )
          // Fallback: nếu cha chưa có replies (flat list)
          return [...next, created]
        })
      } else {
        setComments((prev) => [...prev, created])
      }
      setReplyTo(null)
    } catch (err) {
      console.error("Create comment failed:", err)
      alert(err.response?.data?.message || "Không thể gửi bình luận")
      throw err
    } finally {
      setSubmittingComment(false)
    }
  }

  const handleEditComment = async (commentId, newContent) => {
    try {
      const data = await commentService.updateComment(commentId, {
        content: newContent,
      })
      const updated = normalizeItemResponse(data)
      setComments((prev) =>
        prev.map((c) =>
          c.id === commentId ? { ...c, ...updated } : c
        )
      )
    } catch (err) {
      console.error("Edit comment failed:", err)
      alert(err.response?.data?.message || "Không thể sửa bình luận")
      throw err
    }
  }

  const handleDeleteComment = async (comment) => {
    if (!window.confirm("Bạn có chắc muốn xóa bình luận này?")) return
    try {
      await commentService.deleteComment(comment.id)
      setComments((prev) => prev.filter((c) => c.id !== comment.id))
    } catch (err) {
      console.error("Delete comment failed:", err)
      alert(err.response?.data?.message || "Không thể xóa bình luận")
    }
  }

  // ============ Derived ============
  const commentCount = useMemo(() => {
    return comments.reduce((sum, c) => sum + 1 + (c.replies?.length || 0), 0)
  }, [comments])

  // ============ Loading ============
  if (loadingTask) {
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
          Quay lại
        </Button>
        <div className="rounded-xl border border-destructive/30 bg-destructive/5 p-4 text-sm text-destructive">
          {error}
        </div>
      </div>
    )
  }

  const overdue = isOverdue(task?.dueDate, task?.status)
  const isDone = task?.status === "DONE"

  // ============ Render ============
  return (
    <div className="space-y-6">
      {/* Back */}
      <Button
        variant="ghost"
        size="sm"
        onClick={handleBack}
        className="-ml-2 text-muted-foreground"
      >
        <ArrowLeft className="mr-1 h-4 w-4" />
        Quay lại
      </Button>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1fr_320px]">
        {/* ============ Left: Main content ============ */}
        <div className="space-y-6 min-w-0">
          {/* Title + Description */}
          <div>
            <h1
              className={cn(
                "font-heading text-2xl font-semibold tracking-tight",
                isDone && "line-through text-muted-foreground"
              )}
            >
              {task?.title || "Untitled"}
            </h1>
            {task?.description && (
              <p className="mt-3 whitespace-pre-wrap text-sm text-foreground/80">
                {task.description}
              </p>
            )}
          </div>

          <Separator />

          {/* Comments */}
          <section className="space-y-4">
            <div className="flex items-center gap-2">
              <h2 className="text-base font-semibold">Bình luận</h2>
              <span className="rounded-md bg-muted px-2 py-0.5 text-xs font-medium text-muted-foreground">
                {commentCount}
              </span>
            </div>

            {/* Comment form */}
            <TaskCommentForm
              onSubmit={handleCreateComment}
              submitting={submittingComment}
              replyTo={replyTo}
              onCancelReply={() => setReplyTo(null)}
            />

            <Separator />

            {/* Comment list */}
            <TaskCommentList
              comments={comments}
              currentUserId={currentUserId}
              onReply={setReplyTo}
              onEdit={handleEditComment}
              onDelete={handleDeleteComment}
              loading={loadingComments}
            />
          </section>
        </div>

        {/* ============ Right: Sidebar info ============ */}
        <aside className="space-y-5 lg:sticky lg:top-6 lg:self-start">
          {/* Status */}
          <div className="space-y-2">
            <label className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
              Trạng thái
            </label>
            <TaskStatusSelect
              value={task?.status}
              onChange={handleStatusChange}
              disabled={updatingStatus}
            />
            {updatingStatus && (
              <p className="text-[10px] text-muted-foreground">
                Đang cập nhật...
              </p>
            )}
          </div>

          {/* Priority */}
          <div className="space-y-2">
            <label className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
              Độ ưu tiên
            </label>
            <div>
              <TaskPriorityBadge priority={task?.priority} />
            </div>
          </div>

          {/* Assignee */}
          <div className="space-y-2">
            <label className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
              Người làm
            </label>
            {task?.assigneeId ? (
              <div className="flex items-center gap-2">
                <Avatar className="h-7 w-7">
                  <AvatarFallback
                    className={cn(
                      "text-[10px] text-white",
                      avatarPalette[
                        hashIndex(task.assigneeId, avatarPalette.length)
                      ]
                    )}
                  >
                    {getInitials(task.assigneeName)}
                  </AvatarFallback>
                </Avatar>
                <div className="min-w-0">
                  <p className="truncate text-sm font-medium">
                    {task.assigneeName || "—"}
                  </p>
                  {task.assigneeEmail && (
                    <p className="truncate text-xs text-muted-foreground">
                      {task.assigneeEmail}
                    </p>
                  )}
                </div>
              </div>
            ) : (
              <p className="text-sm italic text-muted-foreground">Chưa giao</p>
            )}
          </div>

          <Separator />

          {/* Due date */}
          <div className="space-y-2">
            <label className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
              Hạn chót
            </label>
            {task?.dueDate ? (
              <div
                className={cn(
                  "flex items-center gap-2 text-sm",
                  overdue && "text-red-500 font-medium"
                )}
              >
                {overdue ? (
                  <AlertCircle className="h-4 w-4" />
                ) : (
                  <Calendar className="h-4 w-4" />
                )}
                <span>{formatDate(task.dueDate)}</span>
                {overdue && (
                  <Badge
                    variant="destructive"
                    className="ml-auto text-[10px]"
                  >
                    Quá hạn
                  </Badge>
                )}
              </div>
            ) : (
              <p className="text-sm italic text-muted-foreground">
                Chưa đặt hạn
              </p>
            )}
          </div>

          <Separator />

          {/* Creator */}
          <div className="space-y-2">
            <label className="text-xs font-semibold uppercase tracking-wide text-muted-foreground">
              Người tạo
            </label>
            <div className="flex items-center gap-2">
              <Avatar className="h-6 w-6">
                <AvatarFallback
                  className={cn(
                    "text-[9px] text-white",
                    avatarPalette[
                      hashIndex(task?.creatorId || "", avatarPalette.length)
                    ]
                  )}
                >
                  {getInitials(task?.creatorName)}
                </AvatarFallback>
              </Avatar>
              <span className="text-sm">{task?.creatorName || "—"}</span>
            </div>
          </div>

          <Separator />

          {/* Timeline */}
          <div className="space-y-3 text-xs text-muted-foreground">
            <div className="flex items-start gap-2">
              <Clock className="mt-0.5 h-3.5 w-3.5 shrink-0" />
              <div>
                <p className="text-foreground/80">
                  Tạo {formatRelative(task?.createdAt)}
                </p>
                {task?.updatedAt !== task?.createdAt && (
                  <p>Cập nhật {formatRelative(task?.updatedAt)}</p>
                )}
                {task?.completedAt && (
                  <p className="mt-1 flex items-center gap-1 text-emerald-600">
                    <CheckCircle2 className="h-3.5 w-3.5" />
                    Hoàn tất {formatRelative(task.completedAt)}
                  </p>
                )}
              </div>
            </div>

            {task?.projectId && (
              <button
                type="button"
                onClick={() => navigate(`/projects/${task.projectId}`)}
                className="flex items-center gap-1 text-primary hover:underline"
              >
                <UserIcon className="h-3.5 w-3.5" />
                Project: {task.projectName || task.projectId}
              </button>
            )}
          </div>
        </aside>
      </div>
    </div>
  )
}

export default TaskDetail