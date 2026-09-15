import { useEffect, useState } from "react"
import { Loader2 } from "lucide-react"

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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"

const STATUS_OPTIONS = [
  { value: "PLANNING", label: "Planning" },
  { value: "IN_PROGRESS", label: "In Progress" },
  { value: "ON_HOLD", label: "On Hold" },
  { value: "COMPLETED", label: "Completed" },
  { value: "CANCELLED", label: "Cancelled" },
]

function CreateProjectModal({ open, onOpenChange, onSubmit, submitting }) {
  const [form, setForm] = useState({
    name: "",
    description: "",
    status: "PLANNING",
  })
  const [errors, setErrors] = useState({})

  useEffect(() => {
    if (open) {
      setForm({ name: "", description: "", status: "PLANNING" })
      setErrors({})
    }
  }, [open])

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }))
    if (errors[field]) setErrors((prev) => ({ ...prev, [field]: undefined }))
  }

  const validate = () => {
    const errs = {}
    if (!form.name.trim()) errs.name = "Tên project không được để trống"
    else if (form.name.trim().length > 100)
      errs.name = "Tên project tối đa 100 ký tự"
    if (form.description.trim().length > 500)
      errs.description = "Mô tả tối đa 500 ký tự"
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
      name: form.name.trim(),
      description: form.description.trim(),
      status: form.status,
    })
  }

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Tạo Project mới</DialogTitle>
          <DialogDescription>
            Project là nơi nhóm bạn quản lý tasks, deadline và tiến độ.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="name">
              Tên project <span className="text-destructive">*</span>
            </Label>
            <Input
              id="name"
              autoFocus
              placeholder="VD: Website Redesign Q4..."
              value={form.name}
              onChange={handleChange("name")}
            />
            {errors.name && (
              <p className="text-xs text-destructive">{errors.name}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="description">Mô tả</Label>
            <Textarea
              id="description"
              rows={3}
              placeholder="Mô tả ngắn về project..."
              value={form.description}
              onChange={handleChange("description")}
            />
            {errors.description && (
              <p className="text-xs text-destructive">{errors.description}</p>
            )}
          </div>

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
                {STATUS_OPTIONS.map((opt) => (
                  <SelectItem key={opt.value} value={opt.value}>
                    {opt.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
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
              {submitting ? "Đang tạo..." : "Tạo Project"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}

export default CreateProjectModal