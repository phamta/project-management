import { useState } from "react"
import WorkspaceGrid from "@/components/workspace/WorkspaceGrid"
import { mockWorkspaces } from "@/data/mockWorkspaces"

function Dashboard() {
  const [workspaces] = useState(mockWorkspaces)

  const handleCreate = () => {
    console.log("Open create workspace modal")
  }

  const handleSelect = (ws) => {
    console.log("Select workspace:", ws.name)
  }

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="font-heading text-2xl font-semibold tracking-tight">
          Dashboard
        </h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Welcome to CollabFlow
        </p>
      </div>

      {/* Workspaces Section */}
      <section>
        <div className="mb-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <h2 className="text-base font-semibold">Workspaces</h2>
            <span className="rounded-md bg-muted px-2 py-0.5 text-xs font-medium text-muted-foreground">
              {workspaces.length}
            </span>
          </div>
        </div>

        <WorkspaceGrid
          workspaces={workspaces}
          onCreate={handleCreate}
          onSelect={handleSelect}
        />
      </section>
    </div>
  )
}

export default Dashboard