<?php

include 'logon.php';
include 'ServerLog.php';
 
// Logging class initialization
$log = new Logging();

//Declare variables
$gmail;

if (isset($_POST['gmail'])) {
	$gmail = $_POST['gmail'];
}

$sql = "delete from users where gmail = :gmail";
$myQuery = $conn->prepare($sql);
$myQuery->execute(array(':gmail'=>$gmail));


$log->lwrite("Deregistered user");
$log->lclose();

?>