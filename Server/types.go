// types.go
package main

type AuthAndRegOK struct {
	Code       int
	SecretCode string
	UserID     int
}

type AddrReturn struct {
	Code int
	Addr string
}

type Success struct {
	Code int
}

type UserInfo struct {
	Code       int
	UserID     int
	Secret     string
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
	Name      string
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

type WhiteList struct {
	Id     int
	Number string
	Addr   string
}

type WhiteListArray struct {
	Code       int
	Count      int
	WhiteListI []WhiteList
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
