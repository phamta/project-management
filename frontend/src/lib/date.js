import { format, formatDistanceToNow, isPast, isToday, isTomorrow } from "date-fns"
import { vi } from "date-fns/locale"

/**
 * Format ngày hiển thị ngắn gọn: "03-06-2026"
 */
export function formatDate(date) {
  if (!date) return ""
  const d = new Date(date)
  if (isNaN(d.getTime())) return ""
  return format(d, "dd-MM-yyyy")
}

/**
 * Format kiểu "2h ago", "3d ago"
 */
export function formatRelative(date) {
  if (!date) return ""
  const d = new Date(date)
  if (isNaN(d.getTime())) return ""
  return formatDistanceToNow(d, { addSuffix: true, locale: vi })
}

/**
 * Check task có overdue không
 */
export function isOverdue(dueDate, status) {
  if (!dueDate || status === "DONE") return false
  return isPast(new Date(dueDate)) && !isToday(new Date(dueDate))
}

/**
 * Format due date thông minh: "Hôm nay", "Ngày mai", "03-06"
 */
export function formatDueDate(dueDate) {
  if (!dueDate) return ""
  const d = new Date(dueDate)
  if (isNaN(d.getTime())) return ""
  if (isToday(d)) return "Hôm nay"
  if (isTomorrow(d)) return "Ngày mai"
  return format(d, "dd-MM-yyyy")
}