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

    //Call method to execute MySQL query
    $result = $account->login();

    //Get data from result
    $row = $result->fetch(PDO::FETCH_ASSOC);

    $databaseValue = $row['password'];

    //If something went wrong, return 404
    if($row = false) {
        echo http_response_code(404);
    }
    //If the correct password is with an existing SFU ID, return 200 OK
    else if(password_verify($data->password, $databaseValue)) {
        echo http_response_code(200);
    }
    //Otherwise return code 403 no matching password with SFU ID
    else {
        echo http_response_code(403);
    }