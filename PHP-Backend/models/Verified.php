<?php
    class Verified {

        //Database stuff
        private $conn;
        private $table = 'users';

        //Sign in properties
        public $sfu_id;
        public $token;
        public $confirmation;
        //Constructor with database
        public function __construct($db) {
            $this->conn = $db;
        }

        public function verificationCheck() {
            //Create Query
            $query = 'UPDATE ' . $this->table . '
                    SET
                        sfu_id = :sfu_id,
                        confirmation = :confirmation,
                        token = :token
                    WHERE
                        token = :token';

            //Prepare statement
            $stmt = $this->conn->prepare($query);
            
            $value = 1;

            //Bind username
            $stmt->bindParam(':sfu_id', $this->sfu_id);
            $stmt->bindParam(':confirmation', $value);
            $stmt->bindParam(':token', $this->token);

            //Execute query
            if($stmt->execute()) {
                return true;
            }

            //Print error if something goes wrong
            echo "Error: " . $stmt->error;
            return false;
        }
    }