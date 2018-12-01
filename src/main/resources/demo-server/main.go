package main

import (
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
	app.Run(iris.Addr(":8080"), iris.WithoutServerError(iris.ErrServerClosed))
}
