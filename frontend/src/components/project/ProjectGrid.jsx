import ProjectCard from "./ProjectCard";
import NewProjectCard from "./NewProjectCard";

function ProjectGrid({ projects = [], onCreate, onSelect }) {
  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
      <NewProjectCard onClick={onCreate} />

      {projects.map((p) => (
        <ProjectCard key={p.id} project={p} onClick={() => onSelect?.(p)} />
      ))}
    </div>
  );
}

export default ProjectGrid;
