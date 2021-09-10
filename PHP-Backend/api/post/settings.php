<?php
    header('Access-Control-Allow-Origin: *');
    header('Content-Type: application/json');
    header('Access-Control-Allow-Methods: POST');
    header('Access-Control-Allow-Headers: Access-Control-Allow-Headers, Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

    include_once '../../config/Database.php';
    include_once '../../models/Account.php';
    include_once '../../models/SessionCheck.php';

    //Instantiate database & connect
    $database = new Database();
    $db = $database->connect();

    //check if this phone is authorized to info
    $verify = new SessionCheck($db);

    //Get raw posted data
    $data = json_decode(file_get_contents("php://input"));

    //Assign variables to class object
    $verify->sfu_id = $data->sfu_id;
    $verify->uuid = $data->uuid;

    //Call method to execute MySQL query
    $result = $verify->sessionCheck();

    //Do not retrieve info if device is not authorized, otherwise process query to get info
    if ($result != 1) {
        echo http_response_code(404);
    } else {
        $settings = new Account($db);

        $info = $settings->get($data->sfu_id);

        if($info == false) {
            echo http_response_code(403);
        } else {
            $num = $info->rowCount();

            if($num == 1) {
                $row = $info->fetch(PDO::FETCH_ASSOC);

                extract($row);

                $post_info = array(
                    'first_name' => $first_name,
                    'last_name' => $last_name,
                    'sfu_id' => $sfu_id,
                    'phone_number' => $phone_number
                );

                echo json_encode($post_info);
            } else {
                echo http_response_code(404);
            }
        }
    }