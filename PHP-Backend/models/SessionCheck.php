<?php
    class SessionCheck {

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

        public function sessionCheck() {
            //Create Query
            $query = 'SELECT logged_in
                    FROM
                        ' . $this->table . '
                    WHERE
                        sfu_id = :sfu_id
                        AND
                        uuid = :uuid';

            //Prepare statement
            $stmt = $this->conn->prepare($query);

            //Bind username
            $stmt->bindParam(':sfu_id', $this->sfu_id);
            $stmt->bindParam(':uuid', $this->uuid);

            //Execute query
            $stmt->execute();

            $result = $stmt->fetch(PDO::FETCH_ASSOC);

            $databaseValue = $result['logged_in'];

            return $databaseValue;
        }
    }