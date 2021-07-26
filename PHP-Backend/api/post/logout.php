<?php
    header('Access-Control-Allow-Origin: *');
    header('Content-Type: application/json');
    header('Access-Control-Allow-Methods: POST');
    header('Access-Control-Allow-Headers: Access-Control-Allow-Headers, Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

    include_once '../../config/LoginDatabase.php';
    include_once '../../models/Signin.php';

    //Instantiate database & connect
    $database = new LoginDatabase();
    $db = $database->connect();

    //Instantiate post object
    $account = new Signin($db);

    //Get raw posted data
    $data = json_decode(file_get_contents("php://input"));

    //Assign variables to class object
    $account->sfu_id = $data->sfu_id;
    $account->password = $data->password;
    $account->uuid = $data->uuid;

    //Call method to execute MySQL query
    $result = $account->login();
    //Return code 403 if no matching password with SFU ID
    if ($result === false) {
        http_response_code(403);
    } else {
        http_response_code(200);
    }