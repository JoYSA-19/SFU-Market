<?php
    $filepath = $_POST['filepath'];

    $size = getimagesize($filepath);

    header('Content-type: '.$size['mime']);
    header('Content-length:' . filesize($filepath));

    readfile($filepath);