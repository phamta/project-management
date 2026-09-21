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

function CreateWorkspaceModal({
  open,
  onOpenChange,
  onSubmit,       // async (values) => Promise
  submitting = false,
}) {
  const [form, setForm] = useState({ name: "", description: "" })
  const [errors, setErrors] = useState({})

  // Reset form khi modal mở lại
  useEffect(() => {
    if (open) {
      setForm({ name: "", description: "" })
      setErrors({})
    }
  }, [open])

  // ============ Handlers ============
  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }))
    // Xóa lỗi của field đang gõ
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: undefined }))
    }
  }

  const validate = () => {
    const newErrors = {}

    const name = form.name.trim()
    if (!name) {
      newErrors.name = "Tên workspace không được để trống"
    } else if (name.length > 50) {
      newErrors.name = "Tên workspace tối đa 50 ký tự"
    }

    const description = form.description.trim()
    if (description.length > 255) {
      newErrors.description = "Mô tả tối đa 255 ký tự"
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleClose = () => {
    if (submitting) return // chặn đóng khi đang submit
    onOpenChange(false)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (submitting) return

    if (!validate()) return

    await onSubmit({
      name: form.name.trim(),
      description: form.description.trim(),
    })
    // Parent chịu trách nhiệm đóng modal khi thành công
  }

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Tạo Workspace mới</DialogTitle>
          <DialogDescription>
            Workspace là nơi nhóm bạn cộng tác, quản lý dự án và công việc.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Name */}
          <div className="space-y-2">
            <Label htmlFor="name">
              Tên workspace <span className="text-destructive">*</span>
            </Label>
            <Input
              id="name"
              placeholder="VD: Marketing Team, Mobile App..."
              autoFocus
              value={form.name}
              onChange={handleChange("name")}
              aria-invalid={!!errors.name}
            />
            {errors.name && (
              <p className="text-xs text-destructive">{errors.name}</p>
            )}
          </div>

          {/* Description */}
          <div className="space-y-2">
            <Label htmlFor="description">Mô tả</Label>
            <Textarea
              id="description"
              placeholder="Mô tả ngắn về mục đích của workspace này..."
              rows={3}
              value={form.description}
              onChange={handleChange("description")}
              aria-invalid={!!errors.description}
            />
            {errors.description && (
              <p className="text-xs text-destructive">{errors.description}</p>
            )}
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
              {submitting ? "Đang tạo..." : "Tạo Workspace"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}

export default CreateWorkspaceModal