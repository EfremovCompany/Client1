// types.go
package main

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
