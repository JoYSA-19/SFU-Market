<?php
    class Post {

        //Database stuff
        private $conn;
        private $table = 'posts';

        //Post Properties
        public $id;
        public $user_id;
        public $textbook_name;
        public $suggested_price;
        public $photo_filepath;
        public $description_text;
        public $post_date;

        //Constructor with database
        public function __construct($db) {
            $this->conn = $db;
        }

        //Post to database
        public function create() {
            //Create query
            $query = 'INSERT INTO ' . $this->table . '
                        SET
                            user_id = :user_id,
                            textbook_name = :textbook_name,
                            suggested_price = :suggested_price,
                            photo_filepath = :photo_filepath,
                            description_text = :description_text';

            //Prepare statement
            $stmt = $this->conn->prepare($query);

            //Clean data
            $this->user_id = htmlspecialchars(strip_tags($this->user_id));
            $this->textbook_name = htmlspecialchars(strip_tags($this->textbook_name));
            $this->suggested_price = htmlspecialchars(strip_tags($this->suggested_price));
            $this->photo_filepath = htmlspecialchars(strip_tags($this->photo_filepath));
            $this->description_text = htmlspecialchars(strip_tags($this->description_text));

            //Bind data
            $stmt->bindParam(':user_id', $this->user_id);
            $stmt->bindParam(':textbook_name', $this->textbook_name);
            $stmt->bindParam(':suggested_price', $this->suggested_price);
            $stmt->bindParam(':photo_filepath', $this->photo_filepath);
            $stmt->bindParam(':description_text', $this->description_text);

            //Execute query
            if($stmt->execute()) {
                return true;
            }

            //Print errer if something goes wrong
            printf("Error: %s.\n", $stmt->error);

            return false;
        }

        //Get from database
        public function read() {
            //Create query
            $query = 'SELECT posts.*, users.first_name, users.last_name, users.sfu_id, users.phone_number FROM ' . $this->table . '
                        LEFT JOIN users
                        ON posts.user_id = users.id
                        ORDER BY posts.post_date DESC';

            //Prepare statement
            $stmt = $this->conn->prepare($query);

            //Execute query
            $stmt->execute();

            return $stmt;
        }
    }