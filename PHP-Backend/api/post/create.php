<?php
    header('Access-Control-Allow-Origin: *');
    header('Content-Type: application/json');
    header('Access-Control-Allow-Methods: POST');
    header('Access-Control-Allow-Headers: Access-Control-Allow-Headers, Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

    include_once '../../config/Database.php';
    include_once '../../models/Post.php';

    //Instantiate database & connect
    $database = new Database();
    $db = $database->connect();

    //Instantiate post object
    $post = new Post($db);

    //Assign file properties
    if(!isset($_FILES)) {
        echo http_response_code(404);
    }

    $file = $_FILES['file'];

    $fileName = $_FILES['file']['name'];
    $fileTmpName = $_FILES['file']['tmp_name'];
    $fileSize = $_FILES['file']['size'];
    $fileError = $_FILES['file']['error'];
    $fileType = $_FILES['file']['type'];

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

    //Set post properties
    $post->user_id = $_POST['user_id'];
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