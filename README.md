# platt

Clojure interface to the java port of leveldb at https://github.com/dain/leveldb

Only implements put/fetch and transactions so far.

Uses nippy for serialization and thus supports all datastructure that nippy does.

## Simple key value store

    (def db (open-database "filename"))
    
    (with-db db
    	(put :a #{1 2 3}))
    
    (with-db db
    	(fetch :a)) => #{1 2 3}
    
    (close-database db)

## Transactions

    (with-db db
		  (put :a 1)
			(transact
			  (put :a 2)
				(put :b 2)
				(/ 7 0)))
    
    (with-db db
		  (fetch :a)) => 1

## License

Copyright © 2013 Kristoffer Ström

Distributed under the Eclipse Public License, the same as Clojure.
