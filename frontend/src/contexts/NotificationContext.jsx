// contexts/NotificationContext.jsx
import {
  createContext,
  useContext,
  useEffect,
  useState,
  useCallback,
  useRef,
} from "react";
import toast from "react-hot-toast";
import notificationService from "@/services/notification.service";
import { socketService } from "@/services/socket.service";
import { useAuth } from "@/contexts/AuthContext";
import { getNotificationIcon } from "@/lib/notification-utils";

const NotificationContext = createContext(null);

export const useNotification = () => {
  const ctx = useContext(NotificationContext);
  if (!ctx) throw new Error("useNotification must be inside NotificationProvider");
  return ctx;
};

export function NotificationProvider({ children }) {
  // ⚠️ Đổi theo AuthContext của bạn: token có thể là user?.token hoặc localStorage
  const { user } = useAuth();
  const token = user?.token || localStorage.getItem("accessToken");

  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const PAGE_SIZE = 20;
  const connectedRef = useRef(false);

  // ====== Load initial ======
  useEffect(() => {
    if (!user || !token) return;
    loadUnreadCount();
    loadNotifications(0, true);
  }, [user, token]);

  // ====== WebSocket lifecycle ======
  useEffect(() => {
    if (!user || !token || connectedRef.current) return;

    socketService.connect(token, () => {
      connectedRef.current = true;
      socketService.subscribe("/user/queue/notifications", (noti) => {
        handleIncoming(noti);
      });
    });
  }, [user, token]);

  // ====== Logout cleanup ======
  useEffect(() => {
    if (!user) {
      socketService.disconnect();
      connectedRef.current = false;
      setNotifications([]);
      setUnreadCount(0);
    }
  }, [user]);

  // ====== Handlers ======
  const handleIncoming = useCallback((noti) => {
    setNotifications((prev) => [noti, ...prev]);
    setUnreadCount((prev) => prev + 1);

    // Toast realtime
    const Icon = getNotificationIcon(noti.type);
    toast.custom(
      (t) => (
        <div
          className={`${
            t.visible ? "animate-enter" : "animate-leave"
          } max-w-sm w-full bg-white shadow-lg rounded-lg pointer-events-auto flex ring-1 ring-black/5`}
        >
          <div className="flex-1 p-4">
            <div className="flex items-start gap-3">
              <Icon className="h-5 w-5 text-blue-500 mt-0.5 shrink-0" />
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900">{noti.title}</p>
                <p className="mt-1 text-sm text-gray-500 line-clamp-2">
                  {noti.message}
                </p>
              </div>
            </div>
          </div>
        </div>
      ),
      { duration: 5000 }
    );
  }, []);

  const loadUnreadCount = async () => {
    try {
      const res = await notificationService.getUnreadNotifications();
      // Hỗ trợ cả 2 format: { data: { count } } hoặc { count }
      const count = res?.data?.count ?? res?.count ?? 0;
      setUnreadCount(count);
    } catch (err) {
      console.error("Load unread lỗi:", err);
    }
  };

  const loadNotifications = async (pageNum = 0, reset = false) => {
    if (loading) return;
    setLoading(true);
    try {
      const res = await notificationService.getMyNotifications?.(pageNum, PAGE_SIZE);
      const items = res?.data?.content ?? res?.content ?? [];
      setNotifications((prev) => (reset ? items : [...prev, ...items]));
      setPage(pageNum);
      setHasMore(!(res?.data?.last ?? res?.last ?? true));
    } catch (err) {
      console.error("Load notifications lỗi:", err);
    } finally {
      setLoading(false);
    }
  };

  const loadMore = () => {
    if (!loading && hasMore) loadNotifications(page + 1, false);
  };

  const markAsRead = async (id) => {
    try {
      await notificationService.markAsRead?.(id);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, isRead: true } : n))
      );
      setUnreadCount((prev) => Math.max(0, prev - 1));
    } catch (err) {
      console.error("Mark read lỗi:", err);
    }
  };

  const markAllAsRead = async () => {
    try {
      await notificationService.markAllAsRead?.();
      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
      setUnreadCount(0);
    } catch (err) {
      console.error("Mark all read lỗi:", err);
    }
  };

  return (
    <NotificationContext.Provider
      value={{
        notifications,
        unreadCount,
        loading,
        hasMore,
        loadMore,
        markAsRead,
        markAllAsRead,
        refreshUnread: loadUnreadCount,
      }}
    >
      {children}
    </NotificationContext.Provider>
  );
}