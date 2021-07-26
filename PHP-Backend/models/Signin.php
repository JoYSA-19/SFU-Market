<?php
    class Signin {

        //Database stuff
        private $conn;
        private $table = 'users';
        private $session_table = 'session_log';

        //Sign in properties
        public $sfu_id;
        public $password;
        public $uuid;

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

            $result = $stmt->fetch(PDO::FETCH_ASSOC);

            $databaseValue = $result['password'];

            if(password_verify($this->password, $databaseValue)) {
                $session = 'INSERT INTO ' . $this->session_table . '
                            SET
                                uuid = :uuid,
                                sfu_id = :sfu_id,
                                logged_in = :logged_in
                            ON DUPLICATE KEY UPDATE
                                sfu_id = :sfu_id,
                                logged_in = :logged_in';

                //Prepare statement
                $session_stmt = $this->conn->prepare($session);

                //Bind parameters
                $session_stmt->bindParam(':uuid', $this->uuid);
                $session_stmt->bindParam(':sfu_id', $this->sfu_id);
                $session_stmt->bindParam(':logged_in', $value);

                //Execute session query
                $session_stmt->execute();

                return $session_stmt;
            } else {
                return false;
            }
        }
    }