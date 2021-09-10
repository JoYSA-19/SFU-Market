<?php
    header('Access-Control-Allow-Origin: *');
    header('Content-Type: application/json');
    header('Access-Control-Allow-Methods: POST');
    header('Access-Control-Allow-Headers: Access-Control-Allow-Headers, Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

    include_once '../../config/Database.php';
    include_once '../../models/SessionCheck.php';
    include_once '../../models/Account.php';
    include_once '../../models/Post.php';

    //Instantiate database & connect
    $database = new Database();
    $db = $database->connect();



    //Assign file properties
    if(!isset($_FILES)) {
        echo http_response_code(404);
    }

    $file = $_FILES['file'];

    $fileName = $_FILES['file']['name'];
    $fileTmpName = $_FILES['file']['tmp_name'];
    $fileSize = $_FILES['file']['size'];
    $fileError = $_FILES['file']['error'];

    //Get file format
    $file_path = explode('.', $fileName);
    $file_ext = strtolower(end($file_path));

    //Set allowed file format types
    $allowed = array('jpg', 'jpeg', 'png', 'heic', 'webp');

    //Store the image into the photos folder on the server if there are no errors and the photo is not bigger than 5MB
    if(in_array($file_ext, $allowed)) {
        if($fileError === 0) {
            if($fileSize < 5000000) {
                $fileNameNew = uniqid('', true) . "." . $file_ext;
                $file_dest = dirname(__DIR__, 3) . '\photos\\' . $fileNameNew;
                move_uploaded_file($fileTmpName, $file_dest);
            } else {
                echo http_response_code(405);
            }
        }
    } else {
        echo http_response_code(403);
    }

    //Check for authorization
    $verify = new SessionCheck($db);

    //Get raw posted data
    $data = json_decode(file_get_contents("php://input"));

    //Assign variables to class object
    $verify->sfu_id = $_POST['sfu_id'];
    $verify->uuid = $_POST['uuid'];

    //Call method to execute MySQL query
    $result = $verify->sessionCheck();

    //Do not retrieve info if device is not authorized, otherwise process query to get info
    if ($result != 1) {
        echo http_response_code(404);
        return;
    } else {
        //Instantiate post object
        $post = new Post($db);

        $settings = new Account($db);

        $info = $settings->get($_POST['sfu_id']);

        if($info == false) {
            echo http_response_code(403);
        } else {
            $num = $info->rowCount();

            if($num == 1) {
                $row = $info->fetch(PDO::FETCH_ASSOC);

                extract($row);
            }
        }

        //Set post properties
        $post->user_id = $id;
        $post->textbook_name = $_POST['textbook_name'];
        $post->suggested_price = $_POST['suggested_price'];
        $post->photo_filepath = $file_dest;
        $post->description_text = $_POST['description_text'];

        //Create post
        if (!$post->create()) {
            echo http_response_code(404);
        } else {
            echo http_response_code(200);
        }
    }