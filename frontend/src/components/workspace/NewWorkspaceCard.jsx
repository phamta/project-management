import { Plus } from "lucide-react"
import { cn } from "@/lib/utils"

function NewWorkspaceCard({ onClick }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={cn(
        "group flex min-h-[160px] flex-col items-center justify-center gap-3",
        "rounded-xl border-2 border-dashed border-border",
        "text-muted-foreground transition-all duration-200",
        "hover:border-indigo-400 hover:bg-indigo-500/5 hover:text-indigo-600 dark:hover:text-indigo-400"
      )}
    >
      <div className="flex size-10 items-center justify-center rounded-full bg-muted transition-colors group-hover:bg-indigo-500/10">
        <Plus className="size-5" />
      </div>
      <span className="text-sm font-medium">New Workspace</span>
    </button>
  )
}

export default NewWorkspaceCard