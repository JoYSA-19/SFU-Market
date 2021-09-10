<?php
    class LoginDatabase {
        //Database parameters
        private $host = 'localhost';
        private $db_name = 'sfu_market';
        private $username = 'Signin';
        private $password = 'qwerty';
        private $conn;

        //Database connect
        public function connect() {
            $this->conn = null;

            try {
                $this->conn = new PDO('mysql:host=' . $this->host . ';dbname=' . $this->db_name,
                 $this->username, $this->password);
                 $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            } catch(PDOException $e) {
                echo 'Connection Error: ' . $e->getMessage();
            }

            return $this->conn;

        }
    }