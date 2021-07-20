
<?php
require "../../config/RegisterDatabase.php";
$db = new RegisterDatabase();
if (isset($_POST['sfu_id']) && isset($_POST['password'])) {
    if ($db->dbConnect()) {
        if ($db->logIn("users", $_POST['sfu_id'], $_POST['password'])) {
            echo "Login Successful";
        } else echo "Username or Password wrong";
    } else echo "Error: Database connection";
} else echo "All fields are required";
?>