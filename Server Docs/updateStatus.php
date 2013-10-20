<?php

include 'logon.php';
include 'ServerLog.php';
 
// Logging class initialization
$log = new Logging();

global $status;
global $gmail;
 
if (isset($_POST['status'])) {
$status= $_POST['status'];
}

if (isset($_POST['gmail'])) {
$gmail= $_POST['gmail'];
}

//Check that if user doesn't exist in the session INSERT, if they do exist then UPDATE
$sql1 = "select count(*) as locationExists from session where gmail = :gmail";
$sql2 = "update session set status = :status where gmail = :gmail";
 
$myQuery = $conn->prepare($sql1);
$myQuery->execute(array(':gmail'=>$gmail));
$result = $myQuery->fetch();
 
$locationExists = $result['locationExists'];

if ($locationExists != null && $locationExists > 0) {
	$myQuery = $conn->prepare($sql2);
        $myQuery->execute(array(':gmail'=>$gmail, ':status'=>$status));
	$log->lwrite("Updated user status");
        $log->lclose();
}
else {
	$log->lwrite("User status not updated - no location data");
        $log->lclose();
}

?>	