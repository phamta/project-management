import { FolderOpen } from "lucide-react"
import WorkspaceCard from "./WorkspaceCard"
import NewWorkspaceCard from "./NewWorkspaceCard"
import { Button } from "@/components/ui/button"

function WorkspaceGrid({ workspaces = [], onCreate, onSelect }) {
  // Empty state
  if (workspaces.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center rounded-xl border border-dashed border-border py-16">
        <FolderOpen className="size-12 text-muted-foreground/50" />
        <h3 className="mt-4 text-base font-medium">No workspaces yet</h3>
        <p className="mt-1 text-sm text-muted-foreground">
          Create your first workspace to get started
        </p>
        <Button className="mt-5" onClick={onCreate}>
          Create Workspace
        </Button>
      </div>
    )
  }

  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
      {/* Card "+ New" đặt đầu tiên */}
      <NewWorkspaceCard onClick={onCreate} />

      {workspaces.map((ws) => (
        <div key={ws.id} onClick={() => onSelect?.(ws)}>
          <WorkspaceCard workspace={ws} />
        </div>
      ))}
    </div>
  )
}

export default WorkspaceGrid