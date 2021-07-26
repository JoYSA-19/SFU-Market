<?php
    header('Access-Control-Allow-Origin: *');
    header('Content-Type: application/json');
    header('Access-Control-Allow-Methods: POST');
    header('Access-Control-Allow-Headers: Access-Control-Allow-Headers, Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

    include_once '../../config/LoginDatabase.php';
    include_once '../../models/SessionCheck.php';

    //Instantiate database & connect
    $database = new LoginDatabase();
    $db = $database->connect();

    //Instantiate post object
    $account = new SessionCheck($db);

    //Get raw posted data
    $data = json_decode(file_get_contents("php://input"));

    //Assign variables to class object
    $account->sfu_id = $data->sfu_id;
    $account->uuid = $data->uuid;

    //Call method to execute MySQL query
    $result = $account->sessionCheck();

    //Return code 404 if there is no current session
    if ($result != 1) {
        echo http_response_code(404);
    } else {
        echo http_response_code(200);
    }