import { useState } from "react"
import { Loader2, Send } from "lucide-react"

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"
import { cn } from "@/lib/utils"
import { useAuth } from "@/contexts/AuthContext"

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

function TaskCommentForm({
  onSubmit,          // async (content, parentId) => Promise
  submitting = false,
  replyTo = null,    // comment object đang reply
  onCancelReply,
  placeholder = "Viết bình luận...",
}) {
  const { user } = useAuth()
  const [content, setContent] = useState("")

  const displayName = user?.fullName || user?.username || "You"
  const avatarSeed = user?.id || user?.userId || displayName

  const handleSubmit = async (e) => {
    e?.preventDefault()
    const trimmed = content.trim()
    if (!trimmed || submitting) return

    try {
      await onSubmit(trimmed, replyTo?.id ?? null)
      setContent("")
      onCancelReply?.()
    } catch (err) {
      // Parent xử lý lỗi
    }
  }

  // Ctrl/Cmd + Enter để submit
  const handleKeyDown = (e) => {
    if ((e.ctrlKey || e.metaKey) && e.key === "Enter") {
      handleSubmit(e)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="flex gap-3">
      <Avatar className="h-8 w-8 shrink-0">
        {user?.avatar && <AvatarImage src={user.avatar} alt={displayName} />}
        <AvatarFallback
          className={cn(
            "text-[10px] text-white",
            avatarPalette[hashIndex(avatarSeed, avatarPalette.length)]
          )}
        >
          {getInitials(displayName)}
        </AvatarFallback>
      </Avatar>

      <div className="flex-1 space-y-2">
        {replyTo && (
          <div className="flex items-center justify-between rounded-md border border-border bg-muted/50 px-3 py-1.5 text-xs text-muted-foreground">
            <span>
              Đang trả lời{" "}
              <strong className="text-foreground">
                {replyTo.fullName || replyTo.username}
              </strong>
            </span>
            <button
              type="button"
              onClick={onCancelReply}
              className="hover:text-foreground"
            >
              Hủy
            </button>
          </div>
        )}

        <Textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder={placeholder}
          rows={replyTo ? 2 : 3}
          disabled={submitting}
        />

        <div className="flex items-center justify-between">
          <p className="text-[10px] text-muted-foreground">
            Nhấn <kbd className="rounded bg-muted px-1">Ctrl</kbd> +{" "}
            <kbd className="rounded bg-muted px-1">Enter</kbd> để gửi
          </p>
          <Button
            type="submit"
            size="sm"
            disabled={!content.trim() || submitting}
          >
            {submitting ? (
              <Loader2 className="mr-2 h-3.5 w-3.5 animate-spin" />
            ) : (
              <Send className="mr-2 h-3.5 w-3.5" />
            )}
            Gửi
          </Button>
        </div>
      </div>
    </form>
  )
}

export default TaskCommentForm