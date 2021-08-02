<?php
    include_once '../../config/Database.php';
    include_once '../../models/Verified.php';

    //Instantiate database & connect
    $database = new Database();
    $db = $database->connect();

    //Instantiate post object
    $account = new Verified($db);

    //Assign variables to class object
    $account->sfu_id = $_GET['sfu_id'];
    $account->token = $_GET['token'];

    $result = $account->verificationCheck();

    //Return code 404 if there is no current session
    if ($result != 1) {
        echo http_response_code(404);
    } else {
        echo 'Account Successfully Verified!';
    }