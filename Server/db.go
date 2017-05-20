// db.go
package main

import (
	"database/sql"

	_ "github.com/go-sql-driver/mysql"
)

func GetAnswer(input string) *sql.Rows {
	db, err := sql.Open("mysql", "root:root@/my_db")
	checkErr(err)
	rows, err := db.Query(input)
	checkErr(err)
	return rows
}

func Update(input string) {
	db, err := sql.Open("mysql", "root:root@/my_db")
	checkErr(err)
	stmt, err := db.Prepare(input)
	checkErr(err)
	_, err = stmt.Exec()
	checkErr(err)
}
