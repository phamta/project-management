import { BrowserRouter, Routes, Route } from "react-router-dom"

import Login from "./components/pages/Login"
import MainLayout from "./components/layout/MainLayout"
import Dashboard from "./components/pages/Dashboard"

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />

        <Route element={<MainLayout />}>
          <Route path="/" element={<Dashboard />} />
        </Route>

      </Routes>
    </BrowserRouter>
  )
}

export default App