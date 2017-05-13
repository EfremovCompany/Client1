// admin.go
package main

import (
	"fmt"
	"net/http"
)

func admin(w http.ResponseWriter, r *http.Request) {
	if r.Method == "POST" {
		fmt.Println(r.FormValue("key"))
		switch key := r.FormValue("key"); key {
		case "setwhitelist":
			setwhitelist(w, r)
		default:
			w.Write([]byte("{ \"ErrorCode\" : 500, \"Error\" : \"Ошибка сервера\" }"))
			return
		}

	} else if r.Method == "GET" {
		switch key := r.FormValue("key"); key {
		case "getwhitelist":
			getwhitelist(w, r)
		default:
			w.Write([]byte("{ \"Code\" : 500, \"Error\" : \"Ошибка сервера\" }"))
			return
		}
	}
}
