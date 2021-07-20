<?php
require "DataBaseConfig.php";

class RegisterDatabase
{
    public $connect;
    public $data;
    private $sql;
    protected $servername;
    protected $username;
    protected $password;
    protected $databasename;

    public function __construct()
    {
        $this->connect = null;
        $this->data = null;
        $this->sql = null;
        $dbc = new DataBaseConfig();
        $this->servername = $dbc->servername;
        $this->username = $dbc->username;
        $this->password = $dbc->password;
        $this->databasename = $dbc->databasename;
    }

    function dbConnect() {
        $this->connect = mysqli_connect($this->servername, $this->username, $this->password, $this->databasename);
        return $this->connect;
    }

    function prepareData($data) {
        return mysqli_real_escape_string($this->connect, stripslashes(htmlspecialchars($data)));
    }
    //Log In
    function logIn($table, $sfu_id, $password)
    {
        $sfu_id = $this->prepareData($sfu_id);
        $password = $this->prepareData($password);
        $this->sql = "select * from " . $table . " where sfu_id = '" . $sfu_id . "'";
        $result = mysqli_query($this->connect, $this->sql);
        $row = mysqli_fetch_assoc($result);
        if (mysqli_num_rows($result) != 0) {
            $dbusername = $row['sfu_id'];
            $dbpassword = $row['password'];
            if ($dbusername == $sfu_id && password_verify($password, $dbpassword)) {
                $login = true;
            } else $login = false;
        } else $login = false;

        return $login;
    }    
    //Sign up
     function signUp($table, $last_name, $first_name, $sfu_id, $phone_number, $password) {
        $last_name = $this->prepareData($last_name);
        $first_name = $this->prepareData($first_name);
        $sfu_id = $this->prepareData($sfu_id);
        $phone_number = $this->prepareData($phone_number);
        $password = $this->prepareData($password);
        $password = password_hash($password, PASSWORD_DEFAULT);
        $this->sql =
            "INSERT INTO " . $table . " (last_name, first_name, sfu_id, phone_number, password) VALUES ('" . $last_name . "','" . $first_name . "','" . $sfu_id . "','" . $phone_number .  "','" . $password . "')";
        if (mysqli_query($this->connect, $this->sql)) {
             return true;
        } else return false;
    }
}