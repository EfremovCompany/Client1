// getwhitelist.go
package main

import (
	"encoding/json"
	"fmt"
	"net/http"
)

func setwhitelist(w http.ResponseWriter, r *http.Request) {
	if !CheckAdminSecret(r.FormValue("secret")) {
		BadSecret(w)
		return
	}
	fmt.Println(r.FormValue("key"))
	/*idwhiteList=" + r.FormValue("id") + ",*/
	Update("INSERT whitelist SET  number=\"" + r.FormValue("number") +
		"\",addr=\"" + r.FormValue("addr") + "\"")
	jsonM := OrderOK{200}
	js, err := json.Marshal(jsonM)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	PrintToScreen(w, js)
}

func getwhitelist(w http.ResponseWriter, r *http.Request) {
	if !CheckAdminSecret(r.FormValue("secret")) {
		BadSecret(w)
		return
	}

	rows := GetAnswer("SELECT idwhiteList FROM whiteList")
	i := 0
	for rows.Next() {
		i += 1
	}
	rows = GetAnswer("SELECT * FROM whiteList")
	var prod []WhiteList = make([]WhiteList, i)

	counter := 0

	for rows.Next() {
		var uid int
		var phone string
		var addr string
		err := rows.Scan(&uid, &phone, &addr)

		checkErr(err)

		prod[counter] = WhiteList{uid, phone, addr}
		counter = counter + 1
	}
	jsonM := WhiteListArray{200, counter, prod}
	js, err := json.Marshal(jsonM)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	PrintToScreen(w, js)
}
