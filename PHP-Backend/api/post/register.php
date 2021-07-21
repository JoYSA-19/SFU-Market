<?php
    header('Access-Control-Allow-Origin: *');
    header('Content-Type: application/json');
    header('Access-Control-Allow-Methods: POST');
    header('Access-Control-Allow-Headers: Access-Control-Allow-Headers, Content-Type,
    Access-Control-Allow-Methods, Authorization, X-Requested-With');

    include_once '../../config/Database.php';
    include_once '../../models/Account.php';

    //Instantiate database & connect
    $database = new Database();
    $db = $database->connect();

    //Instantiate post object
    $verify = new Account($db);
    $account = new Account($db);

    //Get raw posted data
    $data = json_decode(file_get_contents("php://input"));

    //Create account
    if ($account->create($data->first_name, $data->last_name, $data->phone_number, $data->sfu_id, $data->password)) {
        echo http_response_code(200);
    } else {
        echo http_response_code(403);
    }