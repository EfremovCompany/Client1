// db.go
package main

import (
	"database/sql"
	"fmt"
	"reflect"

	_ "github.com/go-sql-driver/mysql"
)

func GetAnswer(input string) *sql.Rows {
	db, err := sql.Open("mysql", "root:root@/mydb")
	checkErr(err)
	rows, err := db.Query(input)
	fmt.Println(reflect.TypeOf(rows))
	checkErr(err)
	return rows
}

func Update(input string) {
	db, err := sql.Open("mysql", "root:root@/mydb")
	checkErr(err)
	stmt, err := db.Prepare(input)
	checkErr(err)
	_, err = stmt.Exec()
	checkErr(err)
}
