package main

import (
	"fmt"
	"net/http"

	"github.com/kataras/iris"
	"github.com/kataras/iris/middleware/logger"
	"github.com/kataras/iris/middleware/recover"
)

func main() {
	app := iris.New()
	app.Logger().SetLevel("debug")
	app.Use(recover.New())
	app.Use(logger.New())
	app.Handle("GET", "/", func(ctx iris.Context) {
		ctx.HTML("<h1>welcome</h1>")
	})
	app.Get("ping", func(ctx iris.Context) {
		ctx.WriteString("pong")
	})
	app.Get("/hello", func(ctx iris.Context) {
		ctx.JSON(iris.Map{"message": "Hello iris!"})
	})
	app.Get("/red", func(ctx iris.Context) {
		ctx.Redirect("/ping")
	})
	app.Post("/sendForm", func(ctx iris.Context) {
		name := ctx.FormValue("name")
		pwd := ctx.FormValue("pwd")
		if name == "zido" && pwd == "123456" {
			ctx.WriteString("yes")
		} else {
			ctx.WriteString("no")
		}
	})
	app.Get("/login", func(ctx iris.Context) {
		ctx.SetCookieKV("id", "123456", func(cookie *http.Cookie) {
			cookie.Path = "/user"
		})
	})
	app.Get("/user/get", func(ctx iris.Context) {
		session := ctx.GetCookie("id")
		fmt.Printf("id is %s\n", session)
		if session == "123456" {
			ctx.WriteString("ok")
		} else {
			ctx.WriteString("no")
		}
	})
	app.Run(iris.Addr(":8080"), iris.WithoutServerError(iris.ErrServerClosed))
}
