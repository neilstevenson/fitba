package main

import (
	"database/sql"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
	"log"
)

func main() {
		database, err := sql.Open("mysql", "root:root@tcp(127.0.0.1:3306)/fitba")

		err = database.Ping()
		if err != nil {
			log.Fatal(err)
		}

                var leagues = []string{ "Bundesliga", "Zweite Bundesliga" }
                for _, league := range leagues {
                	fmt.Printf("\n")
                	fmt.Printf("%s\n", league)
                	fmt.Printf("============\n")

			var query = "SELECT name, pts FROM team WHERE league='" +
				 league + "' ORDER BY 2 DESC, 1 ASC"
		
			rows, _ := database.Query(query)

			count := 0
	
			var ( name string )
			var ( pts int32 )
			for rows.Next() {
				rows.Scan(&name, &pts)
	        		fmt.Printf("%s %d\n", name, pts)
	        		count++
			}	

			fmt.Printf("======================================\n")		
	        	fmt.Printf("Count : %d\n", count)
			fmt.Printf("======================================\n")		
                	fmt.Printf("\n")
	
			rows.Close()
                }
		
		database.Close()
}
