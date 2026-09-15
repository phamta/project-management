import { useState, useMemo } from "react"
import {
  DndContext,
  DragOverlay,
  PointerSensor,
  useSensor,
  useSensors,
  closestCorners,
} from "@dnd-kit/core"
import { arrayMove } from "@dnd-kit/sortable"

import { STATUS_COLUMNS } from "@/constants/task"
import TaskColumn from "./TaskColumn"
import TaskCard from "./TaskCard"

function TaskBoard({
  tasks = [],
  onTasksChange,
  onStatusChange,
  onSelectTask,
  onCreateInColumn,
}) {
  const [activeTask, setActiveTask] = useState(null)

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: { distance: 5 },
    })
  )

  const columns = useMemo(() => {
    const map = {}
    STATUS_COLUMNS.forEach((col) => {
      map[col.key] = tasks.filter((t) => t.status === col.key)
    })
    return map
  }, [tasks])

  const findColumnByTaskId = (id) => {
    for (const col of STATUS_COLUMNS) {
      if (columns[col.key]?.some((t) => t.id === id)) return col.key
    }
    return null
  }

  const handleDragStart = (event) => {
    const task = tasks.find((t) => t.id === event.active.id)
    setActiveTask(task)
  }

  const handleDragOver = (event) => {
    const { active, over } = event
    if (!over) return

    const activeId = active.id
    const overId = over.id

    let targetStatus = null
    if (typeof overId === "string" && overId.startsWith("column-")) {
      targetStatus = overId.replace("column-", "")
    } else {
      targetStatus = findColumnByTaskId(overId)
    }

    const sourceStatus = findColumnByTaskId(activeId)
    if (!targetStatus || !sourceStatus) return
    if (sourceStatus === targetStatus) return

    onTasksChange?.((prev) =>
      prev.map((t) => (t.id === activeId ? { ...t, status: targetStatus } : t))
    )
  }

  const handleDragEnd = async (event) => {
    const { active, over } = event
    setActiveTask(null)
    if (!over) return

    const activeId = active.id
    const overId = over.id

    if (typeof overId === "string" && overId.startsWith("column-")) {
      const newStatus = overId.replace("column-", "")
      const task = tasks.find((t) => t.id === activeId)
      if (task && task.status !== newStatus) {
        try {
          await onStatusChange?.(activeId, newStatus)
        } catch (err) {
          console.error("Update status failed:", err)
        }
      }
      return
    }

    const sourceStatus = findColumnByTaskId(activeId)
    const targetStatus = findColumnByTaskId(overId)
    if (!sourceStatus || !targetStatus) return

    if (sourceStatus === targetStatus) {
      const columnTasks = columns[sourceStatus]
      const oldIndex = columnTasks.findIndex((t) => t.id === activeId)
      const newIndex = columnTasks.findIndex((t) => t.id === overId)
      if (oldIndex !== -1 && newIndex !== -1 && oldIndex !== newIndex) {
        const reordered = arrayMove(columnTasks, oldIndex, newIndex)
        onTasksChange?.((prev) => {
          const others = prev.filter((t) => t.status !== sourceStatus)
          return [...others, ...reordered]
        })
      }
    } else {
      const task = tasks.find((t) => t.id === activeId)
      if (task && task.status !== targetStatus) {
        try {
          await onStatusChange?.(activeId, targetStatus)
        } catch (err) {
          console.error("Update status failed:", err)
        }
      }
    }
  }

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCorners}
      onDragStart={handleDragStart}
      onDragOver={handleDragOver}
      onDragEnd={handleDragEnd}
    >
      {/*
        ⭐ Grid responsive thay vì flex + overflow-x-auto
        - Mobile: 1 cột
        - Tablet: 2 cột
        - Desktop: 4 cột (mỗi cột 1fr → tự co giãn)
      */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
        {STATUS_COLUMNS.map((col) => (
          <TaskColumn
            key={col.key}
            column={col}
            tasks={columns[col.key] || []}
            onCreate={onCreateInColumn}
            onSelectTask={onSelectTask}
          />
        ))}
      </div>

      <DragOverlay
        dropAnimation={{
          duration: 200,
          easing: "cubic-bezier(0.18,0.67,0.6,1.22)",
        }}
      >
        {activeTask ? (
          <div className="rotate-3 cursor-grabbing">
            <TaskCard task={activeTask} />
          </div>
        ) : null}
      </DragOverlay>
    </DndContext>
  )
}

export default TaskBoard