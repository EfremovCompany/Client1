// getorderinfo.go
package main

import (
	"encoding/json"
	"net/http"
)

func getorderinfo(w http.ResponseWriter, r *http.Request) {
	if r.FormValue("secret") != GetSecretPassword() {
		BadSecret(w)
		return
	}
	rows := GetAnswer("SELECT idproduct FROM mydb.order WHERE iduser=" + r.FormValue("id"))
	i := 0
	for rows.Next() {

		i += 1
	}
	rows = GetAnswer("SELECT idorder, idproduct, status, price, addr FROM mydb.order WHERE iduser=" + r.FormValue("id"))

	var prod []Orders = make([]Orders, i)

	counter := 0

	for rows.Next() {
		var id int
		var idProduct int
		var status string
		var price int
		var addr string
		err := rows.Scan(&id, &idProduct, &status, &price, &addr)

		checkErr(err)

		prod[counter] = Orders{id, idProduct, status, price, addr}
		counter = counter + 1
	}
	jsonM := OrderArray{200, counter, prod}
	js, err := json.Marshal(jsonM)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	PrintToScreen(w, js)
}
