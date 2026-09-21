import { useEffect, useState, useMemo } from "react"
import { Loader2, Calendar as CalendarIcon, Check, ChevronsUpDown } from "lucide-react"

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command"

import { cn } from "@/lib/utils"
import {
  TASK_STATUS,
  TASK_PRIORITY,
  STATUS_COLUMNS,
  PRIORITY_CONFIG,
} from "@/constants/task"

import workspaceService from "@/services/workspace.service"

// ============ Helpers ============
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

// Convert LocalDateTime (ISO) → value cho input datetime-local
function toDatetimeLocalValue(iso) {
  if (!iso) return ""
  const d = new Date(iso)
  if (isNaN(d.getTime())) return ""
  const pad = (n) => String(n).padStart(2, "0")
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(
    d.getHours()
  )}:${pad(d.getMinutes())}`
}

// ============ Component ============
function CreateTaskModal({
  open,
  onOpenChange,
  onSubmit,             // async (values) => Promise
  submitting = false,
  projectId,           // projectId để tạo task
  defaultStatus = "TODO",
  members = [],          // danh sách members để chọn assignee
}) {
  const [form, setForm] = useState({
    title: "",
    description: "",
    status: defaultStatus,
    priority: "MEDIUM",
    assigneeId: "",
    dueDate: "",
  })
  const [errors, setErrors] = useState({})
  const [assigneeOpen, setAssigneeOpen] = useState(false)

  // Reset form khi mở modal
  useEffect(() => {
    if (open) {
      setForm({
        title: "",
        description: "",
        status: defaultStatus,
        priority: "MEDIUM",
        assigneeId: "",
        dueDate: "",
      })
      setErrors({})
    }
  }, [open, defaultStatus])

  // Selected assignee object
  const selectedAssignee = useMemo(
    () => members.find((m) => (m.userId || m.id) === form.assigneeId),
    [members, form.assigneeId]
  )

  // ============ Handlers ============
  const handleChange = (field) => (e) => {
    const value = e?.target ? e.target.value : e
    setForm((prev) => ({ ...prev, [field]: value }))
    if (errors[field]) setErrors((prev) => ({ ...prev, [field]: undefined }))
  }

  const validate = () => {
    const errs = {}
    if (!form.title.trim()) {
      errs.title = "Tiêu đề không được để trống"
    } else if (form.title.trim().length > 255) {
      errs.title = "Tiêu đề tối đa 255 ký tự"
    }
    if (form.description.length > 2000) {
      errs.description = "Mô tả tối đa 2000 ký tự"
    }
    setErrors(errs)
    return Object.keys(errs).length === 0
  }

  const handleClose = () => {
    if (submitting) return
    onOpenChange(false)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (submitting) return
    if (!validate()) return

    await onSubmit({
      projectId: projectId,
      title: form.title.trim(),
      description: form.description.trim() || null,
      status: form.status,
      priority: form.priority,
      assigneeId: form.assigneeId || null,
      dueDate: form.dueDate ? new Date(form.dueDate).toISOString() : null,
    })
  }

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Tạo task mới</DialogTitle>
          <DialogDescription>
            Điền thông tin bên dưới để tạo task cho project này.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Title */}
          <div className="space-y-2">
            <Label htmlFor="title">
              Tiêu đề <span className="text-destructive">*</span>
            </Label>
            <Input
              id="title"
              autoFocus
              placeholder="VD: Thiết kế trang login..."
              value={form.title}
              onChange={handleChange("title")}
              aria-invalid={!!errors.title}
            />
            {errors.title && (
              <p className="text-xs text-destructive">{errors.title}</p>
            )}
          </div>

          {/* Description */}
          <div className="space-y-2">
            <Label htmlFor="description">Mô tả</Label>
            <Textarea
              id="description"
              rows={3}
              placeholder="Mô tả chi tiết về task..."
              value={form.description}
              onChange={handleChange("description")}
              aria-invalid={!!errors.description}
            />
            {errors.description && (
              <p className="text-xs text-destructive">{errors.description}</p>
            )}
          </div>

          {/* Row: Status + Priority */}
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-2">
              <Label>Trạng thái</Label>
              <Select
                value={form.status}
                onValueChange={(v) => setForm((p) => ({ ...p, status: v }))}
              >
                <SelectTrigger>
                  <SelectValue />
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
            </div>

            <div className="space-y-2">
              <Label>Độ ưu tiên</Label>
              <Select
                value={form.priority}
                onValueChange={(v) => setForm((p) => ({ ...p, priority: v }))}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {Object.entries(PRIORITY_CONFIG).map(([key, cfg]) => (
                    <SelectItem key={key} value={key}>
                      <span className="flex items-center gap-2">
                        <cfg.icon className={cn("h-3.5 w-3.5", cfg.iconClass)} />
                        {cfg.label}
                      </span>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>

          {/* Row: Assignee + Due Date */}
          <div className="grid grid-cols-2 gap-3">
            {/* Assignee combobox */}
            <div className="space-y-2">
              <Label>Người làm</Label>
              <Popover open={assigneeOpen} onOpenChange={setAssigneeOpen}>
                <PopoverTrigger asChild>
                  <Button
                    type="button"
                    variant="outline"
                    role="combobox"
                    className="w-full justify-between font-normal"
                  >
                    {selectedAssignee ? (
                      <span className="flex items-center gap-2 truncate">
                        <Avatar className="h-5 w-5">
                          {selectedAssignee.avatar && (
                            <AvatarImage
                              src={selectedAssignee.avatar}
                              alt={selectedAssignee.fullName}
                            />
                          )}
                          <AvatarFallback
                            className={cn(
                              "text-[8px] text-white",
                              avatarPalette[
                                hashIndex(
                                  selectedAssignee.userId || selectedAssignee.id,
                                  avatarPalette.length
                                )
                              ]
                            )}
                          >
                            {getInitials(
                              selectedAssignee.fullName || selectedAssignee.username
                            )}
                          </AvatarFallback>
                        </Avatar>
                        <span className="truncate">
                          {selectedAssignee.fullName || selectedAssignee.username}
                        </span>
                      </span>
                    ) : (
                      <span className="text-muted-foreground">
                        Chưa chọn
                      </span>
                    )}
                    <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                  </Button>
                </PopoverTrigger>

                <PopoverContent className="w-[260px] p-0" align="start">
                  <Command>
                    <CommandInput placeholder="Tìm thành viên..." />
                    <CommandList>
                      <CommandEmpty>Không tìm thấy.</CommandEmpty>
                      <CommandGroup>
                        <CommandItem
                          value="unassigned"
                          onSelect={() => {
                            setForm((p) => ({ ...p, assigneeId: "" }))
                            setAssigneeOpen(false)
                          }}
                        >
                          <Check
                            className={cn(
                              "mr-2 h-4 w-4",
                              !form.assigneeId ? "opacity-100" : "opacity-0"
                            )}
                          />
                          <span className="text-muted-foreground italic">
                            Bỏ trống
                          </span>
                        </CommandItem>

                        {members.map((m) => {
                          const mId = m.userId || m.id
                          const displayName = m.fullName || m.username
                          return (
                            <CommandItem
                              key={mId}
                              value={displayName}
                              onSelect={() => {
                                setForm((p) => ({ ...p, assigneeId: mId }))
                                setAssigneeOpen(false)
                              }}
                            >
                              <Check
                                className={cn(
                                  "mr-2 h-4 w-4",
                                  form.assigneeId === mId
                                    ? "opacity-100"
                                    : "opacity-0"
                                )}
                              />
                              <Avatar className="mr-2 h-5 w-5">
                                {m.avatar && (
                                  <AvatarImage src={m.avatar} alt={displayName} />
                                )}
                                <AvatarFallback
                                  className={cn(
                                    "text-[8px] text-white",
                                    avatarPalette[
                                      hashIndex(mId, avatarPalette.length)
                                    ]
                                  )}
                                >
                                  {getInitials(displayName)}
                                </AvatarFallback>
                              </Avatar>
                              <span className="truncate">{displayName}</span>
                            </CommandItem>
                          )
                        })}
                      </CommandGroup>
                    </CommandList>
                  </Command>
                </PopoverContent>
              </Popover>
            </div>

            {/* Due date */}
            <div className="space-y-2">
              <Label htmlFor="dueDate">Hạn chót</Label>
              <div className="relative">
                <CalendarIcon className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                  id="dueDate"
                  type="datetime-local"
                  className="pl-9"
                  value={form.dueDate}
                  onChange={handleChange("dueDate")}
                />
              </div>
            </div>
          </div>

          <DialogFooter>
            <Button
              type="button"
              variant="ghost"
              onClick={handleClose}
              disabled={submitting}
            >
              Hủy
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              {submitting ? "Đang tạo..." : "Tạo task"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}

export default CreateTaskModal