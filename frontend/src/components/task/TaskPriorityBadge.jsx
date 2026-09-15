import { PRIORITY_CONFIG } from "@/constants/task"
import { cn } from "@/lib/utils"

function TaskPriorityBadge({ priority = "MEDIUM" }) {
  const config = PRIORITY_CONFIG[priority] || PRIORITY_CONFIG.MEDIUM
  const Icon = config.icon

  return (
    <span
      className={cn(
        "inline-flex items-center gap-1 rounded-md px-2 py-0.5 text-[10px] font-bold uppercase tracking-wide",
        config.badgeClass
      )}
    >
      <Icon className="h-3 w-3" />
      {config.label}
    </span>
  )
}

export default TaskPriorityBadge