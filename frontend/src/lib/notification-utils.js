// lib/notification-utils.js
import {
  MessageSquare,
  AtSign,
  UserPlus,
  CheckCircle2,
  RefreshCw,
  Clock,
  FolderPlus,
  Building2,
} from "lucide-react";

const ICON_MAP = {
  COMMENT_ADDED: MessageSquare,
  COMMENT_REPLIED: MessageSquare,
  MENTION: AtSign,
  TASK_ASSIGNED: UserPlus,
  TASK_STATUS_CHANGED: CheckCircle2,
  TASK_UPDATED: RefreshCw,
  TASK_DUE_SOON: Clock,
  PROJECT_MEMBER_ADDED: FolderPlus,
  WORKSPACE_MEMBER_ADDED: Building2,
};

const COLOR_MAP = {
  COMMENT_ADDED: "text-blue-500 bg-blue-50",
  COMMENT_REPLIED: "text-blue-500 bg-blue-50",
  MENTION: "text-purple-500 bg-purple-50",
  TASK_ASSIGNED: "text-green-500 bg-green-50",
  TASK_STATUS_CHANGED: "text-emerald-500 bg-emerald-50",
  TASK_UPDATED: "text-orange-500 bg-orange-50",
  TASK_DUE_SOON: "text-red-500 bg-red-50",
  PROJECT_MEMBER_ADDED: "text-indigo-500 bg-indigo-50",
  WORKSPACE_MEMBER_ADDED: "text-cyan-500 bg-cyan-50",
};

export const getNotificationIcon = (type) => ICON_MAP[type] || MessageSquare;
export const getNotificationColor = (type) =>
  COLOR_MAP[type] || "text-gray-500 bg-gray-50";

export const formatRelativeTime = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  const diff = Math.floor((Date.now() - date.getTime()) / 1000);
  if (diff < 60) return "Vừa xong";
  if (diff < 3600) return `${Math.floor(diff / 60)} phút trước`;
  if (diff < 86400) return `${Math.floor(diff / 3600)} giờ trước`;
  if (diff < 604800) return `${Math.floor(diff / 86400)} ngày trước`;
  return date.toLocaleDateString("vi-VN");
};

export const getNotificationLink = (noti) => {
  const { referenceType, referenceId } = noti;
  if (referenceType === "TASK") return `/tasks/${referenceId}`;
  if (referenceType === "COMMENT") return `/tasks/${referenceId}#comments`;
  if (referenceType === "PROJECT") return `/projects/${referenceId}`;
  if (referenceType === "WORKSPACE") return `/workspaces/${referenceId}`;
  return "/notifications";
};