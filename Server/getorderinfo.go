// getorderinfo.go
package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
)

func getorderinfo(w http.ResponseWriter, r *http.Request) {
	if r.FormValue("secret") != GetSecretPassword() {
		BadSecret(w)
		return
	}
	fmt.Println("gs")
	rows := GetAnswer("SELECT idorder FROM my_db.order WHERE iduser=" + r.FormValue("id"))
	i := 0
	for rows.Next() {
		i += 1
	}
	var prod []Orders = make([]Orders, i)
	rows = GetAnswer("SELECT idorder, idproduct, status, price, addr FROM my_db.order WHERE iduser=" + r.FormValue("id"))
	fmt.Println(rows)

	counter := 0

	for rows.Next() {
		fmt.Println("JO")
		var uid int
		var idProduct int
		var status string
		var price int
		var addr string
		err := rows.Scan(&uid, &idProduct, &status, &price, &addr)

		checkErr(err)

		var name string
		rows = GetAnswer("SELECT name FROM my_db.product WHERE idproduct=" + strconv.Itoa(uid))
		for rows.Next() {
			err := rows.Scan(&name)
			checkErr(err)
		}

		prod[counter] = Orders{uid, idProduct, status, price, addr, name}
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
