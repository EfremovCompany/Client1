package main

import (
	"crypto/md5"
	"database/sql"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"strconv"

	_ "github.com/go-sql-driver/mysql"
)

type AuthAndRegOK struct {
	Code       int
	SecretCode string
	UserID     int
}

type UserInfo struct {
	Code       int
	Name       string
	Surname    string
	Patronymic string
	Cdek       string
	Addr       string
	Number     string
}

type OrderOK struct {
	Code int
}

type Orders struct {
	Id        int
	idproduct int
	Status    string
	Price     int
	Addr      string
}

type OrderArray struct {
	Code   int
	Count  int
	Orders []Orders
}

type AuthAndRegFailed struct {
	Code  int
	Error string
}

type Profile struct {
	Code       int
	SecretCode string
}

type Product struct {
	Name      string
	Des       string
	Count     int
	Min_prise float64
	Scr       string
}

type ProductArray struct {
	Code     int
	Count    int
	ProductI []Product
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

func secret(someStr string) string {
	h := md5.New()
	h.Write([]byte(someStr))
	return fmt.Sprintf("%x", h.Sum(nil))
}

func BadSecret(w http.ResponseWriter) {
	authAndRegFailed := AuthAndRegFailed{403, "Неправильный секретный пароль"}
	js, err := json.Marshal(authAndRegFailed)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	w.Write(js)
}

func GetSecretPassword() string {
	return "4mcq5xxoz9nf6pn7fl8ensm40"
}

func sendorder(w http.ResponseWriter, r *http.Request) {
	if r.FormValue("secret") != GetSecretPassword() {
		BadSecret(w)
		return
	}
	db, err := sql.Open("mysql", "root:root@/mydb")
	checkErr(err)
	rows, err := db.Query("SELECT idorder FROM mydb.order")
	checkErr(err)
	var uid int
	for rows.Next() {
		err = rows.Scan(&uid)
		checkErr(err)
	}

	price := r.FormValue("price")
	count := r.FormValue("count")
	//addr := r.FormValue("addr")
	rows, err = db.Query("SELECT addr FROM users WHERE idusers=" + r.FormValue("iduser"))

	var addr string = ""
	for rows.Next() {
		err = rows.Scan(&addr)
		checkErr(err)
	}

	//summ := 0 //strconv.Itoa(price) * strconv.Itoa(count)
	//checkErr(err)

	fmt.Println("Client connected " + r.RemoteAddr)
	id := r.FormValue("iduser")
	idproduct := r.FormValue("idproduct")
	stmt, err := db.Prepare("INSERT mydb.order SET idorder=\"" +
		strconv.Itoa(uid+1) + "\", iduser=" +
		id + ", idproduct=" +
		idproduct + ", status=\"Ожидание\", price=\"" +
		price + "\", addr=\"" +
		addr + "\"")
	checkErr(err)

	_, err = stmt.Exec()
	checkErr(err)

	stmt, err = db.Prepare("UPDATE mydb.product SET count=count-" + count + " WHERE idproduct=\"" +
		idproduct + "\"")
	checkErr(err)

	_, err = stmt.Exec()
	checkErr(err)

	authAndRegOK := OrderOK{200}
	js, err := json.Marshal(authAndRegOK)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	w.Write(js)
	db.Close()
}

func auth(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("mysql", "root:root@/mydb")
	checkErr(err)
	fmt.Println("Client connected " + r.RemoteAddr)
	login := r.FormValue("login")
	pass := secret(r.FormValue("pass"))
	rows, err := db.Query("SELECT idusers FROM users WHERE login=\"" + login + "\"AND pass=\"" + pass + "\"")
	checkErr(err)
	for rows.Next() {
		var username int
		err = rows.Scan(&username)
		authAndRegOK := AuthAndRegOK{200, GetSecretPassword(), username}
		js, err := json.Marshal(authAndRegOK)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		w.Header().Set("Content-Type", "application/json")
		w.Write(js)
		return
	}
	authAndRegFailed := AuthAndRegFailed{403, "Неправильный пароль"}
	js, err := json.Marshal(authAndRegFailed)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	w.Write(js)
	db.Close()
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
	db, err := sql.Open("mysql", "root:root@/mydb")
	checkErr(err)
	fmt.Println("Client connected " + r.RemoteAddr)
	rows, err := db.Query("SELECT idproduct FROM mydb.order WHERE iduser=" + r.FormValue("id"))
	checkErr(err)
	i := 0
	for rows.Next() {

		i += 1
	}
	rows, err = db.Query("SELECT idorder, idproduct, status, price, addr FROM mydb.order WHERE iduser=" + r.FormValue("id"))
	checkErr(err)

	var prod []Orders = make([]Orders, i)

	counter := 0

	for rows.Next() {
		var id int
		var idProduct int
		var status string
		var price int
		var addr string
		err = rows.Scan(&id, &idProduct, &status, &price, &addr)

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
	//w.Write([]byte("Ok"))
	db.Close()
}

func getuserinfo(w http.ResponseWriter, r *http.Request) {
	if r.FormValue("secret") != GetSecretPassword() {
		BadSecret(w)
		return
	}
	db, err := sql.Open("mysql", "root:root@/mydb")
	checkErr(err)
	fmt.Println("Client connected " + r.RemoteAddr)
	rows, err := db.Query("SELECT name, surname, patronymic, cdek, addr, login  FROM users WHERE idusers=\"" + r.FormValue("id") + "\"")
	checkErr(err)
	var username string
	var surname string
	var patronymic string
	var cdek string
	var addr string
	var number string
	for rows.Next() {
		err = rows.Scan(&username, &surname, &patronymic, &cdek, &addr, &number)
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
	db.Close()
}

func getproduct(w http.ResponseWriter, r *http.Request) {
	if r.FormValue("secret") != GetSecretPassword() {
		BadSecret(w)
		return
	}
	db, err := sql.Open("mysql", "root:root@/mydb")
	checkErr(err)

	rows, err := db.Query("SELECT idproduct FROM product")
	checkErr(err)
	i := 0
	for rows.Next() {
		i += 1
	}
	rows, err = db.Query("SELECT * FROM product")
	checkErr(err)

	var prod []Product = make([]Product, i)

	counter := 0

	for rows.Next() {
		var uid string
		var name string
		var des string
		var count int
		var min_price float64
		var src string
		err = rows.Scan(&uid, &name, &des, &count, &min_price, &src)

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
	//w.Write([]byte("Ok"))
	db.Close()
}

func foo(w http.ResponseWriter, r *http.Request) {
	if r.Method == "POST" {
		switch key := r.FormValue("key"); key {
		case "sendorder":
			sendorder(w, r)
		case "auth":
			auth(w, r)
		case "reg":
			reg(w, r)
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
