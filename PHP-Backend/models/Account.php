<?php
    class Account {

        //Database stuff
        private $conn;
        private $table = 'users';

        //Account properties
        private $first_name;
        private $last_name;
        private $phone_number;
        private $sfu_id;
        private $password;
        private $id;

        //Constructor with database
        public function __construct($db) {
            $this->conn = $db;
        }

        //Send new account information to database
        public function create($first_name, $last_name, $phone_number, $sfu_id, $password) {
            //Set data
            $this->first_name = $first_name;
            $this->last_name = $last_name;
            $this->phone_number = $phone_number;
            $this->sfu_id = $sfu_id;
            $this->password = $password;

            //check if SFU ID exists on file
            $queryVerify = 'SELECT * FROM ' . $this->table . '
                    WHERE sfu_id = :sfu_id';

            $stmtVerify = $this->conn->prepare($queryVerify);

            $this->sfu_id = htmlspecialchars(strip_tags($this->sfu_id));

            $stmtVerify->bindParam(':sfu_id', $this->sfu_id);

            $stmtVerify->execute();

            $num = $stmtVerify->rowCount();
            if($num == 1) {
                return false;
            }

            //Create query to add new user
            $query = 'INSERT INTO ' . $this->table . '
                        SET
                            first_name = :first_name,
                            last_name = :last_name,
                            phone_number = :phone_number,
                            sfu_id = :sfu_id,
                            password = :password';

            $stmt = $this->conn->prepare($query);

            //Clean data
            $this->first_name = htmlspecialchars(strip_tags($this->first_name));
            $this->last_name = htmlspecialchars(strip_tags($this->last_name));
            $this->phone_number = htmlspecialchars(strip_tags($this->phone_number));
            $this->sfu_id = htmlspecialchars(strip_tags($this->sfu_id));
            $this->password = htmlspecialchars(strip_tags($this->password));

            //hash password
            $this->password = password_hash($this->password, PASSWORD_DEFAULT);

            //Bind data
            $stmt->bindParam(':first_name', $this->first_name);
            $stmt->bindParam(':last_name', $this->last_name);
            $stmt->bindParam(':phone_number', $this->phone_number);
            $stmt->bindParam(':sfu_id', $this->sfu_id);
            $stmt->bindParam(':password', $this->password);

            //Execute query
            if($stmt->execute()) {
                return true;
            }

            //Print errer if something goes wrong
            echo "Error: " . $stmt->error;

            return false;
        }
    }