<?php
    class Signout {

        //Database stuff
        private $conn;
        private $table = 'session_log';

        //Sign in properties
        public $sfu_id;
        public $uuid;

        //Constructor with database
        public function __construct($db) {
            $this->conn = $db;
        }

        public function logout() {
            //Create Query
            $query = 'UPDATE ' . $this->table . '
                    SET
                        sfu_id = :sfu_id,
                        logged_in = :logged_in
                    WHERE
                        uuid = :uuid';

            //Prepare statement
            $stmt = $this->conn->prepare($query);

            $value = 0;

            //Bind username
            $stmt->bindParam(':sfu_id', $this->sfu_id);
            $stmt->bindParam(':logged_in', $value);
            $stmt->bindParam(':uuid', $this->uuid);

            //Execute query
            $stmt->execute();

            return $stmt;
        }

    }