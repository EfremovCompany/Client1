package main

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"strconv"

	_ "github.com/go-sql-driver/mysql"
)

func PrintToScreen(w http.ResponseWriter, r *http.Request, struct *inp) {
	js, err := json.Marshal(inp)
	checkErr(err)
	w.Header().Set("Content-Type", "application/json")
	w.Write(js)
}

func main() {
	http.HandleFunc("/", foo)
	http.ListenAndServe(":8080", nil)
}

func checkErr(err error) {
	if err != nil {
		fmt.Println(err.Error())
		log.Fatal(err.Error())
	}
}

func reg(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("mysql", "root:root@/mydb")
	checkErr(err)
	fmt.Println("Client connected " + r.RemoteAddr)
	login := r.FormValue("login")
	pass := secret(r.FormValue("pass"))
	addr := r.FormValue("addr")
	cdek := r.FormValue("cdek")
	surname := r.FormValue("surname")
	name := r.FormValue("name")
	fmt.Println(cdek)
	patronymic := r.FormValue("patronymic")

	//Problems: login may be in DB (fixed)
	//Need use primal key  (fixed)
	isWhiteList := false
	rows, err := db.Query("SELECT number FROM whiteList WHERE number=\"" + login + "\"")
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
		w.Header().Set("Content-Type", "application/json")
		w.Write(js)
		//w.Write([]byte("Access denide"))
		return
	}
	rows, err = db.Query("SELECT idusers, login FROM users")
	var uid int
	for rows.Next() {
		var username string
		err = rows.Scan(&uid, &username)
		checkErr(err)
		if username == login {
			//profile := Profile{500, "none"}

			authAndRegFailed := AuthAndRegFailed{402, "Номер уже зарегестрирован"}
			js, err := json.Marshal(authAndRegFailed)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			w.Header().Set("Content-Type", "application/json")
			w.Write(js)
			//Send error to user
			return
		}
	}
	stmt, err := db.Prepare("INSERT users SET idusers=\"" + strconv.Itoa(uid+1) + "\", login=\"" + login +
		"\",pass=\"" + pass +
		"\",addr=\"" + addr +
		"\",cdek=\"" + cdek +
		"\",surname=\"" + surname +
		"\",name=\"" + name +
		"\",patronymic=\"" + patronymic + "\"")
	checkErr(err)

	_, err = stmt.Exec()
	checkErr(err)
	authAndRegOK := AuthAndRegOK{200, GetSecretPassword(), uid + 1}
	js, err := json.Marshal(authAndRegOK)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	w.Write(js)
	//w.Write([]byte("Ok"))
	db.Close()
}

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
	w.Header().Set("Content-Type", "application/json")
	w.Write(js)
}

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
	jsonM := UserInfo{200, username, surname, patronymic, cdek, addr, number}
	js, err := json.Marshal(jsonM)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	w.Write(js)
}

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
	w.Header().Set("Content-Type", "application/json")
	w.Write(js)
}

func editusr(w http.ResponseWriter, r *http.Request) {
	/*rows := */ GetAnswer("SELECT surname FROM users")
	//var data = rows.Next()
	//w.Write(data)
}

func foo(w http.ResponseWriter, r *http.Request) {
	if r.Method == "POST" {
		fmt.Println(r.FormValue("key"))
		switch key := r.FormValue("key"); key {
		case "sendorder":
			sendorder(w, r)
		case "auth":
			auth(w, r)
		case "reg":
			reg(w, r)
		case "editusr":
			fmt.Println("2")
			editusr(w, r)
		default:
			w.Write([]byte("{ \"ErrorCode\" : 500, \"Error\" : \"Ошибка сервера\" }"))
			return
		}

	} else if r.Method == "GET" {
		switch key := r.FormValue("key"); key {
		case "getorder":
			getorderinfo(w, r)
		case "userinfo":
			getuserinfo(w, r)
		case "getproduct":
			getproduct(w, r)
		default:
			w.Write([]byte("{ \"Code\" : 500, \"Error\" : \"Ошибка сервера\" }"))
			return
		}
	} else {
		profile := Profile{200, "0"}

		js, err := json.Marshal(profile)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		w.Header().Set("Content-Type", "application/json")
		w.Write(js)
	}
}
