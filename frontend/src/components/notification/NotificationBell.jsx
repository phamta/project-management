// components/notification/NotificationBell.jsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Bell, CheckCheck, Loader2 } from "lucide-react";

import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useNotification } from "@/contexts/NotificationContext";
import NotificationItem from "./NotificationItem";
import { cn } from "@/lib/utils";

function formatCount(count) {
  if (count <= 0) return null;
  return count > 9 ? "9+" : String(count);
}

export default function NotificationBell() {
  const { notifications, unreadCount, loading, hasMore, loadMore, markAllAsRead } =
    useNotification();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);

  const badgeText = formatCount(unreadCount);

  const handleScroll = (e) => {
    const { scrollTop, scrollHeight, clientHeight } = e.currentTarget;
    if (scrollHeight - scrollTop - clientHeight < 50 && hasMore && !loading) {
      loadMore();
    }
  };

  return (
    <DropdownMenu open={open} onOpenChange={setOpen}>
      <DropdownMenuTrigger asChild>
        <Button
          variant="ghost"
          size="icon"
          className="relative"
          aria-label={
            badgeText ? `Notifications (${unreadCount} unread)` : "Notifications"
          }
        >
          <Bell className="h-5 w-5" />
          {badgeText && (
            <span className="absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full bg-destructive px-1 text-[10px] font-medium leading-none text-destructive-foreground">
              {badgeText}
            </span>
          )}
        </Button>
      </DropdownMenuTrigger>

      <DropdownMenuContent
        align="end"
        className="w-[380px] p-0 max-h-[500px] flex flex-col"
      >
        {/* Header */}
        <div className="flex items-center justify-between px-3 py-2.5 border-b">
          <h3 className="text-sm font-semibold">Thông báo</h3>
          {unreadCount > 0 && (
            <button
              onClick={(e) => {
                e.preventDefault();
                markAllAsRead();
              }}
              className="flex items-center gap-1 text-xs text-primary hover:underline"
            >
              <CheckCheck className="h-3.5 w-3.5" />
              Đọc tất cả
            </button>
          )}
        </div>

        {/* List */}
        <div
          onScroll={handleScroll}
          className="overflow-y-auto flex-1 max-h-[400px]"
        >
          {notifications.length === 0 && !loading && (
            <div className="py-10 text-center text-sm text-muted-foreground">
              Không có thông báo nào
            </div>
          )}

          {notifications.map((noti) => (
            <NotificationItem
              key={noti.id}
              notification={noti}
              onClose={() => setOpen(false)}
            />
          ))}

          {loading && (
            <div className="flex justify-center py-4">
              <Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="border-t px-3 py-2 text-center">
          <button
            onClick={(e) => {
              e.preventDefault();
              setOpen(false);
              navigate("/notifications");
            }}
            className="text-xs text-primary hover:underline font-medium"
          >
            Xem tất cả
          </button>
        </div>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}