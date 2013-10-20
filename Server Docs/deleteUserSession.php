<?php

include 'logon.php';
include 'ServerLog.php';
 
// Logging class initialization
$log = new Logging();
$code;

//Declare variables
$gmail = "";

if (isset($_POST['gmail'])) {
	$gmail = ($_POST['gmail']);
}

$sql = "delete from session where gmail = :gmail";
$myQuery = $conn->prepare($sql);
$myQuery->execute(array(':gmail'=>$gmail));
$code = $conn->errorInfo();
print(json_encode($code));
$log->lwrite("Deleted user's location data");
$log->lclose();
?>