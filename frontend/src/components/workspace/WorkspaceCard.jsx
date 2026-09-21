import { ArrowUpRight, Folder } from "lucide-react"
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card"
import { cn } from "@/lib/utils"

// Backend không trả color → tự hash theo id để có màu ổn định
const colorPalette = [
  "bg-indigo-500/10 text-indigo-600 dark:text-indigo-400",
  "bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  "bg-blue-500/10 text-blue-600 dark:text-blue-400",
  "bg-amber-500/10 text-amber-600 dark:text-amber-400",
  "bg-rose-500/10 text-rose-600 dark:text-rose-400",
]

const avatarPalette = [
  "bg-indigo-500",
  "bg-emerald-500",
  "bg-blue-500",
  "bg-amber-500",
  "bg-rose-500",
]

// Hash string → index ổn định
function hashIndex(str = "", length) {
  let hash = 0
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash)
  }
  return Math.abs(hash) % length
}

// Lấy initials từ fullName
function getInitials(name = "") {
  return name
    .trim()
    .split(/\s+/)
    .map((w) => w[0])
    .slice(0, 2)
    .join("")
    .toUpperCase() || "?"
}

function WorkspaceCard({ workspace, onClick }) {
  const {
    id,
    name,
    description,
    memberCount = 0,
    projectCount = 0,
    members = [],
  } = workspace

  const colorClass = colorPalette[hashIndex(id, colorPalette.length)]
  const visibleMembers = members.slice(0, 3)
  const remaining = memberCount - visibleMembers.length

  return (
    <Card
      onClick={onClick}
      className={cn(
        "group relative cursor-pointer transition-all duration-200",
        "hover:-translate-y-0.5 hover:ring-foreground/20"
      )}
    >
      <ArrowUpRight className="absolute right-3 top-3 size-4 text-muted-foreground opacity-0 transition-opacity group-hover:opacity-100" />

      <CardHeader>
        {/* Icon: chữ cái đầu của name */}
        <div
          className={cn(
            "flex size-10 items-center justify-center rounded-lg text-sm font-semibold",
            colorClass
          )}
        >
          {name?.charAt(0).toUpperCase() || <Folder className="size-4" />}
        </div>

        <CardTitle className="mt-3 line-clamp-1">{name}</CardTitle>
        <CardDescription className="line-clamp-2 min-h-[2.5rem]">
          {description || "Chưa có mô tả"}
        </CardDescription>
      </CardHeader>

      <CardContent className="mt-auto">
        <div className="flex items-center justify-between pt-2">
          {/* Avatar group */}
          <div className="flex items-center -space-x-2">
            {visibleMembers.map((m, idx) => (
              <div
                key={m.id ?? m.userId ?? idx}
                title={m.fullName || m.username}
                className={cn(
                  "flex size-6 items-center justify-center rounded-full text-[10px] font-medium text-white ring-2 ring-background",
                  avatarPalette[hashIndex(m.userId || m.id, avatarPalette.length)]
                )}
              >
                {getInitials(m.fullName || m.username)}
              </div>
            ))}
            {remaining > 0 && (
              <div className="flex size-6 items-center justify-center rounded-full bg-muted text-[10px] font-medium text-muted-foreground ring-2 ring-background">
                +{remaining}
              </div>
            )}
          </div>

          {/* Project count */}
          <div className="text-xs text-muted-foreground">
            {projectCount} projects
          </div>
        </div>
      </CardContent>
    </Card>
  )
}

export default WorkspaceCard