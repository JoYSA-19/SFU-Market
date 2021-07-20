
<?php
require "../../config/RegisterDatabase.php";
$db = new RegisterDataBase();
if (isset($_POST['last_name']) && isset($_POST['first_name']) && isset($_POST['sfu_id']) && isset($_POST['phone_number']) && isset($_POST['password'])) {
    if ($db->dbConnect()) {
        if ($db->signUp("users", $_POST['last_name'], $_POST['first_name'], $_POST['sfu_id'], $_POST['phone_number'], $_POST['password'])) {
            echo "Sign Up Success";
        } else echo "Sign up Failed";
    } else echo "Error: Database connection";
} else echo "All fields are required";
?>