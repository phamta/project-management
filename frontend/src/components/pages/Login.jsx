import { useState } from "react"

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"

import { Input } from "@/components/ui/input"

import { Label } from "@/components/ui/label"

import { Button } from "@/components/ui/button"

import { Checkbox } from "@/components/ui/checkbox"

import { Separator } from "@/components/ui/separator"

import { GitBranch } from "lucide-react"

function Login() {
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")

  const handleSubmit = (e) => {
    e.preventDefault()

    console.log({
      email,
      password,
    })
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-muted/30 px-4">

      <div className="w-full max-w-md">

        {/* Logo */}
        <div className="mb-8 flex flex-col items-center">

          <div className="mb-3 flex h-12 w-12 items-center justify-center rounded-xl bg-primary text-primary-foreground">
            <span className="text-xl font-bold">
              C
            </span>
          </div>

          <h1 className="text-2xl font-bold">
            CollabFlow
          </h1>

          <p className="mt-1 text-sm text-muted-foreground">
            Project collaboration platform
          </p>

        </div>

        {/* Login Card */}
        <Card>

          <CardHeader className="space-y-1">
            <CardTitle className="text-2xl">
              Welcome back
            </CardTitle>

            <CardDescription>
              Enter your credentials to sign in
            </CardDescription>
          </CardHeader>

          <CardContent>

            <form
              onSubmit={handleSubmit}
              className="space-y-5"
            >

              {/* Email */}
              <div className="space-y-2">

                <Label htmlFor="email">
                  Email
                </Label>

                <Input
                  id="email"
                  type="email"
                  placeholder="name@example.com"
                  value={email}
                  onChange={(e) =>
                    setEmail(e.target.value)
                  }
                  required
                />

              </div>

              {/* Password */}
              <div className="space-y-2">

                <div className="flex items-center justify-between">

                  <Label htmlFor="password">
                    Password
                  </Label>

                  <Button
                    type="button"
                    variant="link"
                    className="h-auto p-0 text-sm"
                  >
                    Forgot password?
                  </Button>

                </div>

                <Input
                  id="password"
                  type="password"
                  placeholder="Enter your password"
                  value={password}
                  onChange={(e) =>
                    setPassword(e.target.value)
                  }
                  required
                />

              </div>

              {/* Remember */}
              <div className="flex items-center space-x-2">

                <Checkbox id="remember" />

                <Label
                  htmlFor="remember"
                  className="text-sm font-normal"
                >
                  Remember me
                </Label>

              </div>

              {/* Login */}
              <Button
                type="submit"
                className="w-full"
              >
                Sign in
              </Button>

              <div className="relative">

                <div className="absolute inset-0 flex items-center">
                  <Separator />
                </div>

                <div className="relative flex justify-center">
                  <span className="bg-card px-2 text-xs uppercase text-muted-foreground">
                    Or continue with
                  </span>
                </div>

              </div>

              {/* Github */}
              <Button
                type="button"
                variant="outline"
                className="w-full"
              >
                <GitBranch className="mr-2 h-4 w-4" />
                Continue with GitHub
              </Button>

            </form>

            {/* Register */}
            <div className="mt-6 text-center text-sm">

              <span className="text-muted-foreground">
                Don't have an account?{" "}
              </span>

              <Button
                variant="link"
                className="h-auto p-0"
              >
                Sign up
              </Button>

            </div>

          </CardContent>

        </Card>

        <p className="mt-6 text-center text-xs text-muted-foreground">
          © 2026 CollabFlow. All rights reserved.
        </p>

      </div>

    </div>
  )
}

export default Login