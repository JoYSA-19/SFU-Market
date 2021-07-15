<?php
    class Signin {

        //Database stuff
        private $conn;
        private $table = 'users';

        //Sign in properties
        public $sfu_id;

        //Constructor with database
        public function __construct($db) {
            $this->conn = $db;
        }

        public function login() {
            //Create Query
            $query = 'SELECT password
                    FROM
                        ' . $this->table . '
                    WHERE
                        sfu_id = :sfu_id';

            //Prepare statement
            $stmt = $this->conn->prepare($query);

            //Bind username
            $stmt->bindParam(':sfu_id', $this->sfu_id);

            //Execute query
            $stmt->execute();

            return $stmt;
        }
    }