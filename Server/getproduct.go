// getproduct.go
package main

import (
	"encoding/json"
	"net/http"
)

func getproduct(w http.ResponseWriter, r *http.Request) {
	if r.FormValue("secret") != GetSecretPassword() {
		BadSecret(w)
		return
	}

	rows := GetAnswer("SELECT idproduct FROM product")
	i := 0
	for rows.Next() {
		i += 1
	}
	rows = GetAnswer("SELECT * FROM product")

	var prod []Product = make([]Product, i)

	counter := 0

	for rows.Next() {
		var uid string
		var name string
		var des string
		var count int
		var min_price float64
		var src string
		err := rows.Scan(&uid, &name, &des, &count, &min_price, &src)

		checkErr(err)

		prod[counter] = Product{name, des, count, min_price, src}
		counter = counter + 1
	}
	jsonM := ProductArray{200, counter, prod}
	js, err := json.Marshal(jsonM)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	PrintToScreen(w, js)
}
