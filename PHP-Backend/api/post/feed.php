<?php
    //Headers
    header('Access-Control-Allow-Origin: *');
    header('Content-Type: application/json');

    include_once '../../config/Database.php';
    include_once '../../models/Post.php';

    //Instantiate database & connect
    $database = new Database();
    $db = $database->connect();

    //Instantiate post object
    $post = new Post($db);

    //Market post query
    $result = $post->read();

    //Get row count
    $num = $result->rowCount();

    //Check if any posts
    if($num > 0) {

        //Post array
        $posts_arr = array();

        while($row = $result->fetch(PDO::FETCH_ASSOC)) {
            extract($row);

            $post_item = array(
                'id' => $id,
                'user_id' => $user_id,
                'textbook_name' => $textbook_name,
                'suggested_price' => $suggested_price,
                'photo_filepath' => $photo_filepath,
                'description_text' => $description_text,
                'post_date' => $post_date,
                'first_name' => $first_name,
                'last_name' => $last_name,
                'sfu_id' => $sfu_id,
                'phone_number' => $phone_number
            );

            //Post to 'data'
            array_push($posts_arr, $post_item);
        }

        //Turn to JSON & output
        echo json_encode($posts_arr);

    } else {
        //No posts on database
        echo http_response_code(404);
    }