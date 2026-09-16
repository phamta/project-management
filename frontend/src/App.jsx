import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom"
import { AuthProvider, useAuth } from "@/contexts/AuthContext"
import MainLayout from "@/components/layout/MainLayout"
import Login from "@/components/pages/Login"
import Dashboard from "@/components/pages/Dashboard"
import WorkspaceDetail from "@/components/pages/WorkspaceDetail"
import ProjectDetail from "@/components/pages/ProjectDetail"
import { NotificationProvider } from "./contexts/NotificationContext"

// Protected route: chưa login → về /login
function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth()

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    )
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  return children
}

function App() {
  return (
    <AuthProvider>
      <NotificationProvider>
        <BrowserRouter>
          <Routes>
            {/* Public */}
            <Route path="/login" element={<Login />} />

            {/* Protected — Layout Route */}
            <Route
              element={
                <ProtectedRoute>
                  <MainLayout />
                </ProtectedRoute>
              }
            >
              <Route path="/" element={<Dashboard />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/workspaces/:id" element={<WorkspaceDetail />} />
              <Route path="/projects/:id" element={<ProjectDetail />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </NotificationProvider>
    </AuthProvider>
  )
}

export default App