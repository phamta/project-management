import { ArrowUpRight, CheckSquare } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/Card"
import { cn } from "@/lib/utils"

// Map màu workspace -> class Tailwind
const colorMap = {
  indigo: "bg-indigo-500/10 text-indigo-600 dark:text-indigo-400",
  emerald: "bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  blue: "bg-blue-500/10 text-blue-600 dark:text-blue-400",
  amber: "bg-amber-500/10 text-amber-600 dark:text-amber-400",
  rose: "bg-rose-500/10 text-rose-600 dark:text-rose-400",
}

const avatarColorMap = {
  indigo: "bg-indigo-500",
  emerald: "bg-emerald-500",
  blue: "bg-blue-500",
  amber: "bg-amber-500",
  rose: "bg-rose-500",
}

function WorkspaceCard({ workspace }) {
  const { name, description, color = "indigo", emoji, memberCount, taskCount, members = [] } = workspace

  // Chỉ hiện tối đa 3 avatar, còn lại hiện "+N"
  const visibleMembers = members.slice(0, 3)
  const remaining = memberCount - visibleMembers.length

  return (
    <Card
      className={cn(
        "group relative cursor-pointer transition-all duration-200",
        "hover:ring-foreground/20 hover:-translate-y-0.5"
      )}
    >
      {/* Arrow xuất hiện khi hover */}
      <ArrowUpRight
        className="absolute right-3 top-3 size-4 text-muted-foreground opacity-0 transition-opacity group-hover:opacity-100"
      />

      <CardHeader>
        {/* Icon workspace */}
        <div
          className={cn(
            "flex size-10 items-center justify-center rounded-lg text-lg",
            colorMap[color]
          )}
        >
          {emoji || name.charAt(0).toUpperCase()}
        </div>

        <CardTitle className="mt-3 line-clamp-1">{name}</CardTitle>
        <CardDescription className="line-clamp-2 min-h-[2.5rem]">
          {description}
        </CardDescription>
      </CardHeader>

      <CardContent className="mt-auto">
        <div className="flex items-center justify-between pt-2">
          {/* Avatar group */}
          <div className="flex items-center -space-x-2">
            {visibleMembers.map((m) => (
              <div
                key={m.id}
                title={m.name}
                className={cn(
                  "flex size-6 items-center justify-center rounded-full text-[10px] font-medium text-white ring-2 ring-background",
                  avatarColorMap[m.color] || "bg-zinc-500"
                )}
              >
                {m.initials}
              </div>
            ))}
            {remaining > 0 && (
              <div className="flex size-6 items-center justify-center rounded-full bg-muted text-[10px] font-medium text-muted-foreground ring-2 ring-background">
                +{remaining}
              </div>
            )}
          </div>

          {/* Task count */}
          <div className="flex items-center gap-1 text-xs text-muted-foreground">
            <CheckSquare className="size-3.5" />
            <span>{taskCount} tasks</span>
          </div>
        </div>
      </CardContent>
    </Card>
  )
}

export default WorkspaceCard