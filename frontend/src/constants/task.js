import {
  CircleDashed,
  Clock,
  Eye,
  CheckCircle2,
  ArrowDown,
  ArrowRight,
  ArrowUp,
  AlertTriangle,
} from "lucide-react"

// ============ STATUS ============
export const TASK_STATUS = {
  TODO: "TODO",
  IN_PROGRESS: "IN_PROGRESS",
  IN_REVIEW: "IN_REVIEW",
  DONE: "DONE",
}

export const STATUS_COLUMNS = [
  {
    key: "TODO",
    label: "Cần làm",
    sublabel: "To Do",
    icon: CircleDashed,
    color: "text-zinc-500",
    headerBg: "bg-zinc-500/10 dark:bg-zinc-500/20",
    headerText: "text-zinc-700 dark:text-zinc-300",
    accent: "border-t-zinc-400",
    wipLimit: 8,
  },
  {
    key: "IN_PROGRESS",
    label: "Đang làm",
    sublabel: "In Progress",
    icon: Clock,
    color: "text-blue-500",
    headerBg: "bg-blue-500/10 dark:bg-blue-500/20",
    headerText: "text-blue-700 dark:text-blue-300",
    accent: "border-t-blue-500",
    wipLimit: 3,
  },
  {
    key: "IN_REVIEW",
    label: "Đang Review",
    sublabel: "In Review",
    icon: Eye,
    color: "text-amber-500",
    headerBg: "bg-amber-500/10 dark:bg-amber-500/20",
    headerText: "text-amber-700 dark:text-amber-300",
    accent: "border-t-amber-500",
    wipLimit: 4,
  },
  {
    key: "DONE",
    label: "Hoàn tất",
    sublabel: "Done",
    icon: CheckCircle2,
    color: "text-emerald-500",
    headerBg: "bg-emerald-500/10 dark:bg-emerald-500/20",
    headerText: "text-emerald-700 dark:text-emerald-300",
    accent: "border-t-emerald-500",
    wipLimit: 20,
  },
]

// ============ PRIORITY ============
export const TASK_PRIORITY = {
  LOW: "LOW",
  MEDIUM: "MEDIUM",
  HIGH: "HIGH",
  URGENT: "URGENT",
}

export const PRIORITY_CONFIG = {
  URGENT: {
    label: "URGENT",
    icon: AlertTriangle,
    badgeClass:
      "bg-red-500/15 text-red-600 dark:text-red-400 border border-red-500/30",
    iconClass: "text-red-500",
  },
  HIGH: {
    label: "HIGH",
    icon: ArrowUp,
    badgeClass:
      "bg-orange-500/15 text-orange-600 dark:text-orange-400 border border-orange-500/30",
    iconClass: "text-orange-500",
  },
  MEDIUM: {
    label: "MEDIUM",
    icon: ArrowRight,
    badgeClass:
      "bg-blue-500/15 text-blue-600 dark:text-blue-400 border border-blue-500/30",
    iconClass: "text-blue-500",
  },
  LOW: {
    label: "LOW",
    icon: ArrowDown,
    badgeClass:
      "bg-zinc-500/15 text-zinc-600 dark:text-zinc-400 border border-zinc-500/30",
    iconClass: "text-zinc-500",
  },
}