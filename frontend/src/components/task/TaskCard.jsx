import { useSortable } from "@dnd-kit/sortable"
import { CSS } from "@dnd-kit/utilities"
import { Calendar, CheckSquare, Paperclip, MessageSquare } from "lucide-react"

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { cn } from "@/lib/utils"
import { formatDueDate, isOverdue } from "@/lib/date"
import TaskPriorityBadge from "./TaskPriorityBadge"

// Hash màu avatar theo userId
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

function TaskCard({ task, onClick, isDragging = false }) {
  const {
    id,
    title = "Untitled",
    description = "",
    priority = "MEDIUM",
    status = "TODO",
    dueDate,
    assignee,
    assigneeName,
    assigneeId,
    commentCount = 0,
    attachmentCount = 0,
    subtaskTotal = 0,
    subtaskDone = 0,
  } = task

  // Sortable hook
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging: isSortableDragging,
  } = useSortable({ id })

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isSortableDragging ? 0.4 : 1,
  }

  const dragging = isDragging || isSortableDragging
  const overdue = isOverdue(dueDate, status)

  const displayName = assignee?.fullName || assigneeName || "Unassigned"
  const displayId = assignee?.userId || assigneeId || displayName
  const avatarUrl = assignee?.avatar

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
      onClick={onClick}
      className={cn(
        "group cursor-grab active:cursor-grabbing select-none",
        "rounded-lg border bg-card p-3 shadow-sm",
        "transition-all duration-150",
        "hover:border-foreground/20 hover:shadow-md",
        dragging && "shadow-lg ring-2 ring-primary/30"
      )}
    >
      {/* Priority + Due date + Assignee */}
      <div className="flex items-start justify-between gap-2">
        <TaskPriorityBadge priority={priority} />

        <div className="flex items-center gap-1.5 shrink-0">
          {dueDate && (
            <span
              className={cn(
                "inline-flex items-center gap-1 text-[10px]",
                overdue
                  ? "text-red-500 font-medium"
                  : "text-muted-foreground"
              )}
            >
              <Calendar className="h-3 w-3" />
              {formatDueDate(dueDate)}
            </span>
          )}
        </div>
      </div>

      {/* Title */}
      <h4 className="mt-2 line-clamp-2 text-sm font-semibold leading-snug text-foreground">
        {title}
      </h4>

      {/* Description */}
      {description && (
        <p className="mt-1.5 line-clamp-2 text-xs text-muted-foreground">
          {description}
        </p>
      )}

      {/* Assignee row */}
      <div className="mt-2 flex items-center justify-end gap-1.5">
        <span className="text-[10px] text-muted-foreground truncate max-w-[120px]">
          {displayName} đang sửa
        </span>
        <Avatar className="h-5 w-5">
          {avatarUrl && <AvatarImage src={avatarUrl} alt={displayName} />}
          <AvatarFallback
            className={cn(
              "text-[8px] text-white",
              avatarPalette[hashIndex(displayId, avatarPalette.length)]
            )}
          >
            {getInitials(displayName)}
          </AvatarFallback>
        </Avatar>
      </div>

      {/* Footer: checklist + labels + avatars */}
      <div className="mt-3 flex items-center justify-between gap-2">
        {/* Left: checklist + comment + attachment */}
        <div className="flex items-center gap-2.5 text-[10px] text-muted-foreground">
          {subtaskTotal > 0 && (
            <span
              className={cn(
                "inline-flex items-center gap-1",
                subtaskDone === subtaskTotal && "text-emerald-500"
              )}
            >
              <CheckSquare className="h-3 w-3" />
              {subtaskDone}/{subtaskTotal}
            </span>
          )}

          {commentCount > 0 && (
            <span className="inline-flex items-center gap-1">
              <MessageSquare className="h-3 w-3" />
              {commentCount}
            </span>
          )}

          {attachmentCount > 0 && (
            <span className="inline-flex items-center gap-1">
              <Paperclip className="h-3 w-3" />
              {attachmentCount}
            </span>
          )}
        </div>

        {/* Right: labels + avatars */}
        <div className="flex items-center gap-1.5">
          {task.labels?.slice(0, 2).map((label, i) => (
            <span
              key={i}
              className="rounded bg-muted px-1.5 py-0.5 text-[10px] font-medium text-muted-foreground"
            >
              {label}
            </span>
          ))}

          {task.members?.length > 0 && (
            <div className="flex items-center -space-x-1.5">
              {task.members.slice(0, 2).map((m, i) => (
                <Avatar
                  key={m.id ?? i}
                  className="h-5 w-5 ring-2 ring-background"
                >
                  {m.avatar && <AvatarImage src={m.avatar} alt={m.fullName} />}
                  <AvatarFallback
                    className={cn(
                      "text-[8px] text-white",
                      avatarPalette[
                        hashIndex(m.userId || m.id, avatarPalette.length)
                      ]
                    )}
                  >
                    {getInitials(m.fullName)}
                  </AvatarFallback>
                </Avatar>
              ))}
              {task.members.length > 2 && (
                <div className="flex h-5 w-5 items-center justify-center rounded-full bg-muted text-[8px] font-medium text-muted-foreground ring-2 ring-background">
                  +{task.members.length - 2}
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default TaskCard