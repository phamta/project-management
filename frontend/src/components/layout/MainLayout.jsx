import { useState } from "react"

import Sidebar from "./Sidebar"
import Header from "./Header"

function MainLayout({ children }) {
  const [collapsed, setCollapsed] = useState(false)

  return (
    <div className="min-h-screen bg-muted/30">

      {/* Sidebar */}
      <Sidebar
        collapsed={collapsed}
        onToggle={() => setCollapsed(!collapsed)}
      />

      {/* Main */}
      <div
        className={`min-h-screen transition-all duration-300 ${
          collapsed
            ? "ml-20"
            : "ml-64"
        }`}
      >

        {/* Header */}
        <Header />

        {/* Content */}
        <main className="p-6">
          {children}
        </main>

      </div>

    </div>
  )
}

export default MainLayout