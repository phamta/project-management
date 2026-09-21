import { useDroppable } from "@dnd-kit/core"
import { SortableContext, verticalListSortingStrategy } from "@dnd-kit/sortable"
import { Plus } from "lucide-react"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import TaskCard from "./TaskCard"

function TaskColumn({ column, tasks = [], onCreate, onSelectTask }) {
  const Icon = column.icon

  // Droppable column
  const { setNodeRef, isOver } = useDroppable({
    id: `column-${column.key}`,
    data: { type: "column", status: column.key },
  })

  const isOverLimit = tasks.length > column.wipLimit

  return (
    <div
      className={cn(
        "flex flex-col rounded-xl border border-t-2 bg-muted/30",
        column.accent,
        "min-w-[280px] w-[300px] shrink-0"
      )}
    >
      {/* Header */}
      <div className="flex items-center justify-between gap-2 border-b p-3">
        <div className="flex items-center gap-2 min-w-0">
          <div
            className={cn(
              "flex items-center gap-1.5 rounded-md px-2 py-1",
              column.headerBg
            )}
          >
            <Icon className={cn("h-3.5 w-3.5", column.color)} />
            <span className={cn("text-xs font-semibold", column.headerText)}>
              {column.label}
            </span>
            <span
              className={cn(
                "text-xs font-medium",
                isOverLimit ? "text-red-500" : column.headerText,
                "opacity-70"
              )}
            >
              {tasks.length}
            </span>
          </div>
        </div>

        <div className="flex items-center gap-1.5 shrink-0">
          <span className="text-[10px] text-muted-foreground">
            WIP Limit: <span className="font-medium">{column.wipLimit}</span>
          </span>
          <Button
            variant="ghost"
            size="icon"
            className="h-6 w-6"
            onClick={() => onCreate?.(column.key)}
          >
            <Plus className="h-3.5 w-3.5" />
          </Button>
        </div>
      </div>

      {/* Tasks */}
      <div
        ref={setNodeRef}
        className={cn(
          "flex-1 space-y-2 overflow-y-auto p-2 min-h-[120px]",
          "transition-colors",
          isOver && "bg-primary/5"
        )}
      >
        <SortableContext
          items={tasks.map((t) => t.id)}
          strategy={verticalListSortingStrategy}
        >
          {tasks.map((task) => (
            <TaskCard
              key={task.id}
              task={task}
              onClick={() => onSelectTask?.(task)}
            />
          ))}
        </SortableContext>

        {/* Empty state */}
        {tasks.length === 0 && (
          <div
            className={cn(
              "flex h-32 items-center justify-center rounded-lg border-2 border-dashed border-border/60",
              isOver && "border-primary/40 bg-primary/5"
            )}
          >
            <span className="text-xs italic text-muted-foreground">
              Kéo thẻ vào đây
            </span>
          </div>
        )}
      </div>
    </div>
  )
}

export default TaskColumn