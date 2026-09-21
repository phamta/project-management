// services/socket.service.js
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const WS_URL = import.meta.env.VITE_WS_URL || "http://localhost:8080/ws";

let client = null;
const subscriptions = new Map();

export const socketService = {
  connect(token, onConnect) {
    if (client?.active) return;

    client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        console.log("✅ WS connected");
        onConnect?.();
      },
      onStompError: (frame) =>
        console.error("❌ STOMP error:", frame.headers.message),
      onWebSocketClose: () => console.warn("⚠️ WS closed"),
    });

    client.activate();
  },

  subscribe(destination, callback) {
    if (!client?.connected) return;
    if (subscriptions.has(destination)) {
      subscriptions.get(destination).unsubscribe();
    }
    const sub = client.subscribe(destination, (msg) => {
      try {
        callback(JSON.parse(msg.body));
      } catch (e) {
        console.error("Parse WS message lỗi:", e);
      }
    });
    subscriptions.set(destination, sub);
  },

  disconnect() {
    subscriptions.forEach((s) => s.unsubscribe());
    subscriptions.clear();
    if (client?.active) {
      client.deactivate();
      client = null;
    }
  },

  isConnected: () => client?.connected || false,
};