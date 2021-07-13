<?php
    header('Access-Control-Allow-Origin: *');
    header('Content-Type: application/json');
    header('Access-Control-Allow-Methods: POST');
    header('Access-Control-Allow-Headers: Access-Control-Allow-Headers, Content-Type,
    Access-Control-Allow-Methods, Authorization, X-Requested-With');

    include_once '../../config/Database.php';
    include_once '../../models/Post.php';

    //Instantiate database & connect
    $database = new Database();
    $db = $database->connect();

    //Instantiate post object
    $post = new Post($db);

    //Get raw posted data
    $data = json_decode(file_get_contents("php://input"));

    $post->user_id = $data->user_id;
    $post->textbook_name = $data->textbook_name;
    $post->suggested_price = $data->suggested_price;
    $post->photo_filepath = $data->photo_filepath;
    $post->description_text = $data->description_text;

    //Create post
    if ($post->create()) {
        echo json_encode(
            array('message' => 'Post created')
        );
    } else {
        echo json_encode(
            array('message' => 'Post not created')
        );
    }