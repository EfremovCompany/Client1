// sendorder.go
package main

import (
	"encoding/json"
	"net/http"
	"strconv"
)

func sendorder(w http.ResponseWriter, r *http.Request) {
	if r.FormValue("secret") != GetSecretPassword() {
		BadSecret(w)
		return
	}
	rows := GetAnswer("SELECT idorder FROM mydb.order")
	var uid int
	for rows.Next() {
		err := rows.Scan(&uid)
		checkErr(err)
	}

	price := r.FormValue("price")
	count := r.FormValue("count")
	rows = GetAnswer("SELECT addr FROM users WHERE idusers=" + r.FormValue("iduser"))

	var addr string = ""
	for rows.Next() {
		err := rows.Scan(&addr)
		checkErr(err)
	}

	id := r.FormValue("iduser")
	idproduct := r.FormValue("idproduct")
	Update("INSERT mydb.order SET idorder=\"" +
		strconv.Itoa(uid+1) + "\", iduser=" +
		id + ", idproduct=" +
		idproduct + ", status=\"Ожидание\", price=\"" +
		price + "\", addr=\"" +
		addr + "\"")

	Update("UPDATE mydb.product SET count=count-" + count + " WHERE idproduct=\"" +
		idproduct + "\"")

	authAndRegOK := OrderOK{200}
	js, err := json.Marshal(authAndRegOK)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	w.Write(js)
}
