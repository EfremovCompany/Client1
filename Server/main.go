package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"

	_ "github.com/go-sql-driver/mysql"
)

func PrintToScreen(w http.ResponseWriter, js []byte) {
	w.Header().Set("Content-Type", "application/json")
	w.Write(js)
}

func main() {
	http.HandleFunc("/", foo)
	http.HandleFunc("/sendorder", sendorder)
	http.HandleFunc("/auth", auth)
	http.HandleFunc("/reg", reg)
	http.HandleFunc("/editusr", editusr)
	http.HandleFunc("/getorder", getorderinfo)
	http.HandleFunc("/userinfo", getuserinfo)
	http.HandleFunc("/getproduct", getproduct)
	http.HandleFunc("/getaddr", getaddr)
	http.HandleFunc("/admin", admin)
	http.ListenAndServe(":8080", nil)
}

func checkErr(err error) {
	if err != nil {
		fmt.Println(err.Error())
		log.Fatal(err.Error())
	}
}

func foo(w http.ResponseWriter, r *http.Request) {
	/*if r.Method == "POST" {
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
		case "getaddr":
			getaddr(w, r)
		default:
			w.Write([]byte("{ \"Code\" : 500, \"Error\" : \"Ошибка сервера\" }"))
			return
		}
	} else {*/
	profile := Profile{200, "0"}

	js, err := json.Marshal(profile)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.Write(js)
	//}
}
