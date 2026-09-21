import {
  LayoutDashboard,
  FolderKanban,
  CheckSquare,
  Users,
  CalendarDays,
  Settings,
  LogOut,
  ChevronLeft,
  ChevronRight,
} from "lucide-react"

import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Separator } from "@/components/ui/separator"

const menuItems = [
  {
    title: "Workspace",
    icon: LayoutDashboard,
    path: "/",
  },
  {
    title: "Projects",
    icon: FolderKanban,
    path: "/projects",
  },
  {
    title: "My Tasks",
    icon: CheckSquare,
    path: "/tasks",
  },
  {
    title: "Team",
    icon: Users,
    path: "/team",
  },
  {
    title: "Calendar",
    icon: CalendarDays,
    path: "/calendar",
  },
]

function Sidebar({ collapsed = false, onToggle }) {
  return (
    <aside
      className={`fixed left-0 top-0 z-40 flex h-screen flex-col border-r bg-background transition-all duration-300 ${
        collapsed ? "w-16 md:w-20" : "w-16 md:w-64"
      }`}
    >
      {/* Logo */}
      <div className="flex h-16 items-center justify-center px-2 md:justify-between md:px-4">
        {!collapsed && (
          <div className="flex items-center gap-2">
            <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary text-primary-foreground">
              <span className="font-bold">C</span>
            </div>

            <span className="hidden text-lg font-bold md:inline">
              CollabFlow
            </span>
          </div>
        )}

        {collapsed && (
          <div className="mx-auto flex h-9 w-9 items-center justify-center rounded-lg bg-primary text-primary-foreground">
            <span className="font-bold">C</span>
          </div>
        )}
      </div>

      <Separator />

      {/* Navigation */}
      <nav className="flex-1 space-y-1 p-3">
        {menuItems.map((item) => {
          const Icon = item.icon

          return (
            <Button
              key={item.title}
              variant="ghost"
              className={`w-full ${
                collapsed
                  ? "justify-center px-2"
                  : "justify-center px-2 md:justify-start"
              }`}
            >
              <Icon className="h-5 w-5 shrink-0" />

              {!collapsed && (
                <span className="ml-3 hidden md:inline">
                  {item.title}
                </span>
              )}
            </Button>
          )
        })}
      </nav>

      {/* Bottom */}
      <div className="space-y-2 p-3">
        <Button
          variant="ghost"
          className={`w-full ${
            collapsed
              ? "justify-center px-2"
              : "justify-center px-2 md:justify-start"
          }`}
        >
          <Settings className="h-5 w-5 shrink-0" />

          {!collapsed && (
            <span className="ml-3 hidden md:inline">
              Settings
            </span>
          )}
        </Button>

        <Separator />

        {/* User */}
        <div
          className={`flex items-center ${
            collapsed
              ? "justify-center"
              : "justify-between"
          } rounded-lg p-2`}
        >
          <div className="flex items-center gap-3">
            <Avatar className="h-9 w-9">
              <AvatarFallback>
                TV
              </AvatarFallback>
            </Avatar>

            {!collapsed && (
              <div className="hidden min-w-0 md:block">
                <p className="truncate text-sm font-medium">
                  Tan Van
                </p>

                <p className="truncate text-xs text-muted-foreground">
                  Developer
                </p>
              </div>
            )}
          </div>

          {!collapsed && (
            <Button
              variant="ghost"
              size="icon"
              className="hidden h-8 w-8 md:inline-flex"
            >
              <LogOut className="h-4 w-4" />
            </Button>
          )}
        </div>

        {/* Collapse */}
        <Button
          variant="outline"
          size="icon"
          className="w-full"
          onClick={onToggle}
        >
          {collapsed ? (
            <ChevronRight className="h-4 w-4" />
          ) : (
            <ChevronLeft className="h-4 w-4" />
          )}
        </Button>
      </div>
    </aside>
  )
}

export default Sidebar