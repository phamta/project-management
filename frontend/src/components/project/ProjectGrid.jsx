import { FolderOpen } from "lucide-react"
import ProjectCard from "./ProjectCard"
import NewProjectCard from "./NewProjectCard"
import { Button } from "@/components/ui/button"

function ProjectGrid({ projects = [], onCreate, onSelect }) {
  if (projects.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center rounded-xl border border-dashed border-border py-16">
        <FolderOpen className="size-12 text-muted-foreground/50" />
        <h3 className="mt-4 text-base font-medium">No projects yet</h3>
        <p className="mt-1 text-sm text-muted-foreground">
          Create your first project to get started
        </p>
        <Button className="mt-5" onClick={onCreate}>
          Create Project
        </Button>
      </div>
    )
  }

  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
      <NewProjectCard onClick={onCreate} />

      {projects.map((p) => (
        <ProjectCard
          key={p.id}
          project={p}
          onClick={() => onSelect?.(p)}
        />
      ))}
    </div>
  )
}

export default ProjectGrid