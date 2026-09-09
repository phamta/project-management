import { BrowserRouter, Routes, Route } from "react-router-dom"

import Login from "./components/pages/Login"
import MainLayout from "./components/layout/MainLayout"

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />

        <Route
          path="/"
          element={
            <MainLayout>
              <h1 className="text-3xl font-bold">
                Dashboard
              </h1>

              <p className="mt-2 text-muted-foreground">
                Welcome to CollabFlow
              </p>
            </MainLayout>
          }
        />
      </Routes>
    </BrowserRouter>
  )
}

export default App