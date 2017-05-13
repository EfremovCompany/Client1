// getaddr.go
package main

import (
	"encoding/json"
	"net/http"
)

func getaddr(w http.ResponseWriter, r *http.Request) {
	rows := GetAnswer("SELECT addr FROM whiteList WHERE number=\"" + r.FormValue("login") + "\"")
	var addr string
	for rows.Next() {
		err := rows.Scan(&addr)
		checkErr(err)
	}
	addrS := AddrReturn{200, addr}
	js, err := json.Marshal(addrS)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	PrintToScreen(w, js)
}
