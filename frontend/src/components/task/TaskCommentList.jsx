import { MessageSquare } from "lucide-react"
import TaskCommentItem from "./TaskCommentItem"

/**
 * Group flat comments thành cây parent → replies
 */
function buildCommentTree(comments = []) {
  const map = new Map()
  const roots = []

  // Pass 1: clone + đảm bảo replies là array
  comments.forEach((c) => {
    map.set(c.id, { ...c, replies: [] })
  })

  // Pass 2: link children
  comments.forEach((c) => {
    const node = map.get(c.id)
    if (c.parentId && map.has(c.parentId)) {
      map.get(c.parentId).replies.push(node)
    } else {
      roots.push(node)
    }
  })

  return roots
}

function TaskCommentList({
  comments = [],
  currentUserId,
  onReply,
  onEdit,
  onDelete,
  loading = false,
}) {
  if (loading) {
    return (
      <div className="space-y-4">
        {Array.from({ length: 3 }).map((_, i) => (
          <div key={i} className="flex gap-3">
            <div className="h-8 w-8 animate-pulse rounded-full bg-muted" />
            <div className="flex-1 space-y-2">
              <div className="h-3 w-32 animate-pulse rounded bg-muted" />
              <div className="h-3 w-full animate-pulse rounded bg-muted" />
            </div>
          </div>
        ))}
      </div>
    )
  }

  if (comments.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center rounded-lg border border-dashed border-border py-10">
        <MessageSquare className="h-8 w-8 text-muted-foreground/50" />
        <p className="mt-2 text-sm text-muted-foreground">
          Chưa có bình luận nào
        </p>
        <p className="text-xs text-muted-foreground/70">
          Hãy là người đầu tiên bình luận
        </p>
      </div>
    )
  }

  const tree = buildCommentTree(comments)

  return (
    <div className="space-y-5">
      {tree.map((comment) => (
        <TaskCommentItem
          key={comment.id}
          comment={comment}
          currentUserId={currentUserId}
          onReply={onReply}
          onEdit={onEdit}
          onDelete={onDelete}
        />
      ))}
    </div>
  )
}

export default TaskCommentList