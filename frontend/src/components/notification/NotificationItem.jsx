// components/notification/NotificationItem.jsx
import { useNavigate } from "react-router-dom";
import { useNotification } from "@/contexts/NotificationContext";
import {
  getNotificationIcon,
  getNotificationColor,
  getNotificationLink,
  formatRelativeTime,
} from "@/lib/notification-utils";
import { cn } from "@/lib/utils";

export default function NotificationItem({ notification, onClose }) {
  const { markAsRead } = useNotification();
  const navigate = useNavigate();
  const Icon = getNotificationIcon(notification.type);

  const handleClick = () => {
    if (!notification.isRead) markAsRead(notification.id);
    onClose?.();
    navigate(getNotificationLink(notification));
  };

  return (
    <button
      onClick={handleClick}
      className={cn(
        "w-full text-left px-3 py-3 flex gap-3 hover:bg-accent transition border-b border-border last:border-0",
        !notification.isRead && "bg-primary/5"
      )}
    >
      <div
        className={cn(
          "flex-shrink-0 w-9 h-9 rounded-full flex items-center justify-center",
          getNotificationColor(notification.type)
        )}
      >
        <Icon className="h-4 w-4" />
      </div>

      <div className="flex-1 min-w-0">
        <div className="flex items-start justify-between gap-2">
          <p
            className={cn(
              "text-sm truncate",
              !notification.isRead ? "font-semibold" : "font-medium"
            )}
          >
            {notification.title}
          </p>
          {!notification.isRead && (
            <span className="flex-shrink-0 w-2 h-2 bg-primary rounded-full mt-1.5" />
          )}
        </div>

        <p className="text-xs text-muted-foreground mt-0.5 line-clamp-2">
          {notification.message}
        </p>

        <p className="text-[11px] text-muted-foreground/70 mt-1">
          {formatRelativeTime(notification.createdAt)}
        </p>
      </div>
    </button>
  );
}