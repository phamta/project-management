import { useState } from "react"
import { MoreHorizontal, Trash2, Reply, Pencil } from "lucide-react"

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Textarea } from "@/components/ui/textarea"
import { cn } from "@/lib/utils"
import { formatRelative } from "@/lib/date"

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

function TaskCommentItem({
  comment,
  currentUserId,
  onReply,
  onEdit,
  onDelete,
  isReply = false,
}) {
  const [isEditing, setIsEditing] = useState(false)
  const [editContent, setEditContent] = useState(comment.content)
  const [saving, setSaving] = useState(false)

  const displayName = comment.fullName || comment.username || "Unknown"
  const avatarSeed = comment.userId || displayName
  const isOwner = currentUserId && comment.userId === currentUserId

  const handleSaveEdit = async () => {
    const trimmed = editContent.trim()
    if (!trimmed || trimmed === comment.content) {
      setIsEditing(false)
      setEditContent(comment.content)
      return
    }
    try {
      setSaving(true)
      await onEdit?.(comment.id, trimmed)
      setIsEditing(false)
    } catch (err) {
      console.error("Edit comment failed:", err)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div
      className={cn(
        "group flex gap-3",
        isReply && "ml-10 mt-3 border-l-2 border-border pl-3"
      )}
    >
      <Avatar className="h-8 w-8 shrink-0">
        {comment.avatar && (
          <AvatarImage src={comment.avatar} alt={displayName} />
        )}
        <AvatarFallback
          className={cn(
            "text-[10px] text-white",
            avatarPalette[hashIndex(avatarSeed, avatarPalette.length)]
          )}
        >
          {getInitials(displayName)}
        </AvatarFallback>
      </Avatar>

      <div className="flex-1 min-w-0">
        {/* Header */}
        <div className="flex items-center gap-2">
          <span className="text-sm font-semibold text-foreground">
            {displayName}
          </span>
          <span className="text-xs text-muted-foreground">
            {formatRelative(comment.createdAt)}
          </span>
          {comment.updatedAt !== comment.createdAt && (
            <span className="text-[10px] italic text-muted-foreground">
              (đã sửa)
            </span>
          )}
        </div>

        {/* Body */}
        {isEditing ? (
          <div className="mt-1 space-y-2">
            <Textarea
              value={editContent}
              onChange={(e) => setEditContent(e.target.value)}
              rows={3}
              autoFocus
            />
            <div className="flex gap-2">
              <Button size="sm" onClick={handleSaveEdit} disabled={saving}>
                {saving ? "Đang lưu..." : "Lưu"}
              </Button>
              <Button
                size="sm"
                variant="ghost"
                onClick={() => {
                  setIsEditing(false)
                  setEditContent(comment.content)
                }}
                disabled={saving}
              >
                Hủy
              </Button>
            </div>
          </div>
        ) : (
          <p className="mt-1 whitespace-pre-wrap text-sm text-foreground/90">
            {comment.content}
          </p>
        )}

        {/* Actions */}
        {!isEditing && (
          <div className="mt-1 flex items-center gap-3 opacity-0 transition-opacity group-hover:opacity-100">
            {!isReply && (
              <button
                type="button"
                onClick={() => onReply?.(comment)}
                className="text-xs font-medium text-muted-foreground hover:text-foreground"
              >
                Trả lời
              </button>
            )}

            {isOwner && (
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-5 w-5 text-muted-foreground"
                  >
                    <MoreHorizontal className="h-3.5 w-3.5" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="start">
                  <DropdownMenuItem onClick={() => setIsEditing(true)}>
                    <Pencil className="mr-2 h-4 w-4" />
                    Sửa
                  </DropdownMenuItem>
                  <DropdownMenuItem
                    className="text-destructive"
                    onClick={() => onDelete?.(comment)}
                  >
                    <Trash2 className="mr-2 h-4 w-4" />
                    Xóa
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            )}
          </div>
        )}

        {/* Replies */}
        {comment.replies?.length > 0 && (
          <div className="mt-3 space-y-3">
            {comment.replies.map((reply) => (
              <TaskCommentItem
                key={reply.id}
                comment={reply}
                currentUserId={currentUserId}
                onEdit={onEdit}
                onDelete={onDelete}
                isReply
              />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

export default TaskCommentItem