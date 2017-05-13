// secret.go
package main

import (
	"crypto/md5"
	"encoding/json"
	"fmt"
	"net/http"
)

func secret(someStr string) string {
	h := md5.New()
	h.Write([]byte(someStr))
	return fmt.Sprintf("%x", h.Sum(nil))
}

func CheckAdminSecret(input string) bool {
	return "UPISsyxr1HYaDz2vuxZ81g6O7" == input
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
