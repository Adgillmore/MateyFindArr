<?php
$host = "mysql4.000webhost.com";
$dbname = "a9550275_CAAA";
$user = "a9550275_Admin";
$pwd = "KaDan4dam";

try {
	$conn = new PDO("mysql:host=$host; dbname=$dbname", $user, $pwd);
}
catch (PDOException $e) {
	echo $e->getMessage();
}
?>