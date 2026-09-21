import { STATUS_COLUMNS } from "@/constants/task"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { cn } from "@/lib/utils"

function TaskStatusSelect({ value, onChange, disabled }) {
  const current = STATUS_COLUMNS.find((c) => c.key === value)

  return (
    <Select value={value} onValueChange={onChange} disabled={disabled}>
      <SelectTrigger className="w-[180px]">
        <SelectValue>
          {current && (
            <span className="flex items-center gap-2">
              <current.icon className={cn("h-3.5 w-3.5", current.color)} />
              {current.label}
            </span>
          )}
        </SelectValue>
      </SelectTrigger>
      <SelectContent>
        {STATUS_COLUMNS.map((col) => (
          <SelectItem key={col.key} value={col.key}>
            <span className="flex items-center gap-2">
              <col.icon className={cn("h-3.5 w-3.5", col.color)} />
              {col.label}
            </span>
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  )
}

export default TaskStatusSelect