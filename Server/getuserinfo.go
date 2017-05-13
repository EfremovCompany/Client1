// getuserinfo.go
package main

import (
	"encoding/json"
	"net/http"
	"strconv"
)

func getuserinfo(w http.ResponseWriter, r *http.Request) {
	if r.FormValue("secret") != GetSecretPassword() {
		BadSecret(w)
		return
	}
	rows := GetAnswer("SELECT name, surname, patronymic, cdek, addr, login  FROM users WHERE idusers=\"" + r.FormValue("id") + "\"")
	var username string
	var surname string
	var patronymic string
	var cdek string
	var addr string
	var number string
	for rows.Next() {
		err := rows.Scan(&username, &surname, &patronymic, &cdek, &addr, &number)
		checkErr(err)
	}
	intID, err := strconv.Atoi(r.FormValue("id"))
	checkErr(err)
	jsonM := UserInfo{200, intID, GetSecretPassword(), username, surname, patronymic, cdek, addr, number}
	js, err := json.Marshal(jsonM)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	PrintToScreen(w, js)
}

func getuserinfoFromAuth(w http.ResponseWriter, id int) {
	rows := GetAnswer("SELECT name, surname, patronymic, cdek, addr, login  FROM users WHERE idusers=\"" + strconv.Itoa(id) + "\"")
	var username string
	var surname string
	var patronymic string
	var cdek string
	var addr string
	var number string
	for rows.Next() {
		err := rows.Scan(&username, &surname, &patronymic, &cdek, &addr, &number)
		checkErr(err)
	}
	jsonM := UserInfo{200, id, GetSecretPassword(), username, surname, patronymic, cdek, addr, number}
	js, err := json.Marshal(jsonM)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	PrintToScreen(w, js)
}
