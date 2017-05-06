// editusr
package main

import (
	"encoding/json"
	"net/http"
	"strconv"
)

func editusr(w http.ResponseWriter, r *http.Request) {
	login := r.FormValue("login")
	pass := secret(r.FormValue("pass"))
	addr := r.FormValue("addr")
	cdek := r.FormValue("cdek")
	surname := r.FormValue("surname")
	name := r.FormValue("name")
	patronymic := r.FormValue("patronymic")
	isPassCorrect := false

	rows := GetAnswer("SELECT login FROM users WHERE login=\"" + login + "\" AND pass=\"" + pass + "\"")
	for rows.Next() {
		isPassCorrect = true
	}

	if !isPassCorrect {
		authAndRegFailed := AuthAndRegFailed{401, "Пароль не корректный"}
		js, err := json.Marshal(authAndRegFailed)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		PrintToScreen(w, js)
		return
	}

	rows = GetAnswer("SELECT idusers FROM users WHERE login=\"" + login + "\"")
	var uid int
	for rows.Next() {
		err := rows.Scan(&uid)
		checkErr(err)
	}

	Update("INSERT users SET idusers=\"" + strconv.Itoa(uid) +
		"\",addr=\"" + addr +
		"\",cdek=\"" + cdek +
		"\",surname=\"" + surname +
		"\",name=\"" + name +
		"\",patronymic=\"" + patronymic + "\"")

	success := Success{200}
	js, err := json.Marshal(success)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	PrintToScreen(w, js)
}
