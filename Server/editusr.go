// editusr
package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
)

func editusr(w http.ResponseWriter, r *http.Request) {
	if r.FormValue("secret") != GetSecretPassword() {
		BadSecret(w)
		return
	}
	login := r.FormValue("login")
	o_pass := secret(r.FormValue("old"))
	fmt.Println(o_pass)
	rows := GetAnswer("SELECT idusers FROM my_db.users WHERE login=\"" + login + "\"AND pass=\"" + o_pass + "\"")
	for rows.Next() {
		var username int
		fmt.Println("as")
		err := rows.Scan(&username)
		checkErr(err)
		pass := secret(r.FormValue("pass"))
		addr := r.FormValue("addr")
		surname := r.FormValue("surname")
		name := r.FormValue("name")
		patronymic := r.FormValue("patronymic")
		fmt.Println("UPDATE my_db.users SET " +
			"pass=\"" + pass +
			"\",addr=\"" + addr +
			"\",surname=\"" + surname +
			"\",name=\"" + name +
			"\",patronymic=\"" + patronymic + "\" WHERE idusers=" + strconv.Itoa(username) + "")

		Update("UPDATE my_db.users SET " +
			"pass=\"" + pass +
			"\",addr=\"" + addr +
			"\",surname=\"" + surname +
			"\",name=\"" + name +
			"\",patronymic=\"" + patronymic + "\" WHERE idusers=" + strconv.Itoa(username) + "")

		success := Success{200}
		js, err := json.Marshal(success)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		PrintToScreen(w, js)
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
