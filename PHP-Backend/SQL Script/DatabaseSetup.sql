CREATE DATABASE sfu_market;

CREATE TABLE users (
    id INT NOT NULL AUTO_INCREMENT,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    sfu_id VARCHAR(255) NOT NULL,
    phone_number BIGINT NOT NULL,
    password TINYTEXT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE posts (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    textbook_name VARCHAR(255) NOT NULL,
    suggested_price FLOAT NOT NULL,
    photo_filepath MEDIUMTEXT NOT NULL,
    description_text LONGTEXT NOT NULL,
    post_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE items (
	id INT NOT NULL AUTO_INCREMENT,
    item_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE professor (
	id INT NOT NULL AUTO_INCREMENT,
    professor_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE class (
	id INT NOT NULL AUTO_INCREMENT,
    professor_id INT NOT NULL,
    class_name VARCHAR(10) NOT NULL,
    class_code VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (professor_id) REFERENCES professor(id)
);

CREATE TABLE professor_item_table (
	id INT NOT NULL AUTO_INCREMENT,
    class_id INT NOT NULL,
    item_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (class_id) REFERENCES class(id),
    FOREIGN KEY (item_id) REFERENCES items(id)
);

CREATE TABLE session_log (
	uuid VARCHAR(255) NOT NULL,
	sfu_id VARCHAR(255) NOT NULL,
    logged_in BOOLEAN,
    PRIMARY KEY (uuid)
);
