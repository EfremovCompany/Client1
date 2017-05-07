// reg.go
package main

import (
	"encoding/json"
	"net/http"
	"strconv"
)

func reg(w http.ResponseWriter, r *http.Request) {
	login := r.FormValue("login")
	pass := secret(r.FormValue("pass"))
	addr := r.FormValue("addr")
	cdek := r.FormValue("cdek")
	surname := r.FormValue("surname")
	name := r.FormValue("name")
	patronymic := r.FormValue("patronymic")
	isWhiteList := false

	rows := GetAnswer("SELECT number FROM whiteList WHERE number=\"" + login + "\"")
	for rows.Next() {
		isWhiteList = true
	}
	if !isWhiteList {
		authAndRegFailed := AuthAndRegFailed{401, "Отказано в доступе"}
		js, err := json.Marshal(authAndRegFailed)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		PrintToScreen(w, js)
		return
	}
	rows = GetAnswer("SELECT idusers, login FROM users")
	var uid int
	for rows.Next() {
		var username string
		err := rows.Scan(&uid, &username)
		checkErr(err)
		if username == login {
			authAndRegFailed := AuthAndRegFailed{402, "Номер уже зарегестрирован"}
			js, err := json.Marshal(authAndRegFailed)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			PrintToScreen(w, js)
			return
		}
	}

	Update("INSERT users SET idusers=\"" + strconv.Itoa(uid+1) + "\", login=\"" + login +
		"\",pass=\"" + pass +
		"\",addr=\"" + addr +
		"\",cdek=\"" + cdek +
		"\",surname=\"" + surname +
		"\",name=\"" + name +
		"\",patronymic=\"" + patronymic + "\"")

	getuserinfoFromAuth(w, uid+1)

	/*authAndRegOK := AuthAndRegOK{200, GetSecretPassword(), uid + 1}
	js, err := json.Marshal(authAndRegOK)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	PrintToScreen(w, js)*/
}
