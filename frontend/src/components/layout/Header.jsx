import {
  Search,
  Bell,
  HelpCircle,
  Settings,
  LogOut,
  User,
} from "lucide-react"
import { useNavigate } from "react-router-dom"

import { Input } from "@/components/ui/input"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Button } from "@/components/ui/button"
import { useAuth } from "@/contexts/AuthContext"
import { useState, useEffect } from "react"
import notificationService from "@/services/notification.service"
import { normalizeListResponse, normalizeItemResponse } from "@/lib/api-utils";

// Helper: lấy initials từ tên
function getInitials(name = "") {
  return name
    .trim()
    .split(/\s+/)
    .map((w) => w[0])
    .slice(0, 2)
    .join("")
    .toUpperCase()
}

function formatCount(count) {
  if (count <= 0) return null
  if (count > 9) return "9+"
  return String(count)
}

function Header() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const [unreadCount, setUnreadCount] = useState(0)

  const displayName = user?.name || user?.username || "Guest"
  const displayRole = user?.role || user?.jobTitle || "Member"
  const initials = getInitials(displayName) || "U"

  const handleLogout = async () => {
    await logout()
    navigate("/login", { replace: true })
  }

  useEffect(() => {
    const fetchUnreadNotifications = async () => {
      try {
        const data = await notificationService.getUnreadNotifications();
        const count = normalizeItemResponse(data)?.count || 0;
        setUnreadCount(count);
      } catch (err) {
        console.error("Failed to fetch unread notifications:", err);
      }
    };

    fetchUnreadNotifications();
  }, []);

  const badgeText = formatCount(unreadCount)

  return (
    <header className="sticky top-0 z-30 flex min-h-16 flex-wrap items-center justify-between gap-2 border-b bg-background/95 px-3 py-2 backdrop-blur sm:px-6 sm:py-0">
      {/* Search */}
      <div className="relative min-w-0 flex-1 sm:max-w-md">
        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input placeholder="Search projects, tasks..." className="pl-9" />
      </div>

      {/* Right */}
      <div className="ml-auto flex shrink-0 items-center gap-1 sm:ml-6 sm:gap-2">
        {/* Help */}
        <Button variant="ghost" size="icon">
          <HelpCircle className="h-5 w-5" />
        </Button>

        {/* Notification */}
        <Button
          variant="ghost"
          size="icon"
          className="relative"
          aria-label={
            badgeText
              ? `Notifications (${unreadCount} unread)`
              : "Notifications"
          }
        >
          <Bell className="h-5 w-5" />

          {badgeText && (
            <span className="absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full bg-destructive px-1 text-[10px] font-medium leading-none text-destructive-foreground">
              {badgeText}
            </span>
          )}
        </Button>

        {/* User */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button
              variant="ghost"
              className="ml-2 flex items-center gap-2 px-2"
            >
              <Avatar className="h-9 w-9">
                <AvatarFallback>{initials}</AvatarFallback>
              </Avatar>

              <div className="hidden text-left md:block">
                <p className="text-sm font-medium">
                  {displayName}
                </p>

                <p className="text-xs text-muted-foreground">
                  {displayRole}
                </p>
              </div>
            </Button>
          </DropdownMenuTrigger>

          <DropdownMenuContent align="end" className="w-56">

            <DropdownMenuItem onClick={() => navigate("/profile")}>
              <User className="mr-2 h-4 w-4" />
              Profile
            </DropdownMenuItem>

            <DropdownMenuItem onClick={() => navigate("/settings")}>
              <Settings className="mr-2 h-4 w-4" />
              Settings
            </DropdownMenuItem>

            <DropdownMenuSeparator />

            <DropdownMenuItem
              className="text-destructive"
              onClick={handleLogout}
            >
              <LogOut className="mr-2 h-4 w-4" />
              Log out
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  )
}

export default Header