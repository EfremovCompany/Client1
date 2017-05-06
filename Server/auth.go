// auth
package main

import (
	"encoding/json"
	"fmt"
	"net/http"
)

func auth(w http.ResponseWriter, r *http.Request) {
	fmt.Println("ON")
	login := r.FormValue("login")
	pass := secret(r.FormValue("pass"))
	rows := GetAnswer("SELECT idusers FROM users WHERE login=\"" + login + "\"AND pass=\"" + pass + "\"")
	for rows.Next() {
		var username int
		err := rows.Scan(&username)
		checkErr(err)
		getuserinfoFromAuth(w, username)
		/*authAndRegOK := AuthAndRegOK{200, GetSecretPassword(), username}
		js, err := json.Marshal(authAndRegOK)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		w.Header().Set("Content-Type", "application/json")
		w.Write(js)*/
		return
	}
	authAndRegFailed := AuthAndRegFailed{403, "Неправильный пароль"}
	js, err := json.Marshal(authAndRegFailed)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	PrintToScreen(w, js)
}
