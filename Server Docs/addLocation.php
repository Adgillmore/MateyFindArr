<?php
 
include 'logon.php';
include 'ServerLog.php';

// Logging class initialization
$log = new Logging();

$gmail ="";
global $latitude;
global $longitude;
global $timestamp;
global $speed;
global $status;
global $code;
 
if (isset($_POST['gmail'])) {
$gmail = ($_POST['gmail']);
}
if (isset($_POST['latitude'])) {
$latitude = $_POST['latitude'];
}
if (isset($_POST['longitude'])) {
$longitude = $_POST['longitude'];
}
if (isset($_POST['timestamp'])) {
$timestamp = $_POST['timestamp'];
}
if (isset($_POST['speed'])) {
$speed= $_POST['speed'];
}
if (isset($_POST['status'])) {
$status= $_POST['status'];
}
//Check that if user doesn't exist in the session INSERT, if they do exist then UPDATE
$sql1 = "select count(*) as locationExists from session where gmail = :gmail";
$sql2 = "insert into session (gmail, latitude, longitude, speed, timestamp, status) values (:gmail, :latitude, :longitude, :speed, :timestamp, :status)";
$sql3 = "update session set gmail = :gmail, latitude = :latitude, longitude = :longitude, speed = :speed, timestamp = :timestamp, status= :status where gmail = :gmail";

$myQuery = $conn->prepare($sql1);
$myQuery->execute(array(':gmail'=>$gmail));
$result = $myQuery->fetch();

$locationExists = $result['locationExists'];

if ($locationExists != null && $locationExists == 0) {
	$myQuery = $conn->prepare($sql2);
	$myQuery->execute(array(':gmail'=>$gmail, ':latitude'=>$latitude, ':longitude'=>$longitude, ':speed'=>$speed, ':timestamp'=>$timestamp, ':status'=>$status));
    $code = $myQuery->errorInfo();
 
      // write message to the log file
      $log->lwrite("Added location ");
      
      // close log file
      $log->lclose();
}
else if ($locationExists != null && $locationExists > 0) {
	$myQuery = $conn->prepare($sql3);
	$myQuery->execute(array(':gmail'=>$gmail, ':latitude'=>$latitude, ':longitude'=>$longitude, ':speed'=>$speed, ':timestamp'=>$timestamp, ':status'=>$status));
    $code = $myQuery->errorInfo();

      // write message to the log file
      $log->lwrite("Updated location ");
      
      // close log file
      $log->lclose();
}
else {
	// write message to the log file
      $log->lwrite("Error");
      
      // close log file
      $log->lclose();
}
print(json_encode($code));
?>	