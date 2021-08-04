<?php

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

/*
 * The PHPMailer library is downloaded from the following location:
 *     https://github.com/PHPMailer/PHPMailer
 * The source code has not been modified.
 * The file has the following copyright from the original author:
 * Copyright (C) 1991, 1999 Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * Everyone is permitted to copy and distribute verbatim copies
 * of this license document, but changing it is not allowed.
 */

require '../../vendor/autoload.php';

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
        private $token;

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
                            password = :password,
                            token = :token';

            $stmt = $this->conn->prepare($query);

            //Clean data
            $this->first_name = htmlspecialchars(strip_tags($this->first_name));
            $this->last_name = htmlspecialchars(strip_tags($this->last_name));
            $this->phone_number = htmlspecialchars(strip_tags($this->phone_number));
            $this->sfu_id = htmlspecialchars(strip_tags($this->sfu_id));
            $this->password = htmlspecialchars(strip_tags($this->password));

            //hash password
            $this->password = password_hash($this->password, PASSWORD_DEFAULT);
          
            //Create token for email verification
            function createToken($len=32){
                return substr(md5(openssl_random_pseudo_bytes(20)), -$len);
            }
            $token = createToken(10);
            $this->token = $token;
            
            $this->verificationMail($this->sfu_id, $this->token);
            
            //Bind data
            $stmt->bindParam(':first_name', $this->first_name);
            $stmt->bindParam(':last_name', $this->last_name);
            $stmt->bindParam(':phone_number', $this->phone_number);
            $stmt->bindParam(':sfu_id', $this->sfu_id);
            $stmt->bindParam(':password', $this->password);
            $stmt->bindParam(':token', $this->token);

            //Execute query
            if($stmt->execute()) {
                return true;
            }

            //Print error if something goes wrong
            echo "Error: " . $stmt->error;

            return false;
        }
        
        public function verificationMail($sfu_id, $token) {

            $mail = new PHPMailer(true);

            try {
                $mail->SMTPDebug = 2;
                $mail->isSMTP();
                $mail->Host = 'smtp.gmail.com';
                $mail->SMTPAuth = true;
                $mail->Username = 'sfumarket9@gmail.com';
                $mail->Password = 'sfumarket2021';
                $mail->SMTPSecure = 'ssl';
                $mail->Port = 465;

                $mail->setFrom('sfumarket9@gmail.com', 'Activate your Account');
                $mail->addAddress($this->sfu_id);
                $mail->addReplyTo($this->sfu_id);


                $mail->isHTML(true);
                $mail->Subject = 'Confirm Email';
                $mail->Body = 'Click here to activate your account:
                <a href="https://localhost/PHP-Backend/api/post/verify.php?sfu_id=' . $sfu_id . '&token=' . $token . '">Confirm Email</a>';
                $mail->send();
                echo 'Message sent!';
            } catch (Exception $e) {
                echo "Message could not be sent", $mail->ErrorInfo;
            }
        }

        public function get($sfu_id) {
            //Set sfu_id
            $this->sfu_id = $sfu_id;

            $query = 'SELECT * FROM ' . $this->table . '
                WHERE
                    sfu_id = :sfu_id';

            $stmt = $this->conn->prepare($query);

            $stmt->bindParam(':sfu_id', $this->sfu_id);

            $stmt->execute();

            return $stmt;
        }
    }
