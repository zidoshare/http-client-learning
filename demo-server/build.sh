#!/usr/bin/env bash

CGO_ENABLED=0 GOOS=darwin GOARCH=amd64 go build -o ./bin/demo-server-mac
CGO_ENABLED=0 GOOS=windows GOARCH=amd64 go build -o ./bin/demo-server-win.exe
go build -o ./bin/demo-server-linux