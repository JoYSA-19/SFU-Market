<?php
    header('Access-Control-Allow-Origin: *');

    include_once '../../config/LoginDatabase.php';
    include_once '../../models/Signout.php';

    //Instantiate database & connect
    $database = new LoginDatabase();
    $db = $database->connect();

    //Instantiate post object
    $account = new Signout($db);

    //Assign variables to class object
    $account->sfu_id = $_POST['sfu_id'];
    $account->uuid = $_POST['uuid'];

    //Call method to execute MySQL query
    $result = $account->logout();
    //Return code 404 if log out failed
    if ($result === false) {
        http_response_code(404);
    } else {
        http_response_code(200);
    }