<?php
//Modified from Elad Nava on stack overflow
//http://stackoverflow.com/questions/11242743/gcm-with-php-google-cloud-messaging

include 'logon.php';

// Replace with real BROWSER API key from Google APIs
$apiKey = "AIzaSyA1zWBx-rdcoOQfmCtXQnBEMpjca326wgk";

    $registrationIDs = array();
	$message = "";
	$userCount = 0;
	$POSTsubarray = array();
 
 	if (isset($_POST['JSONString'])) {
		$message = $_POST['JSONString'];
	}
	print $message;
	print_r($_POST);
	
	if (!empty($_POST)) {
	$userCount = (count($_POST)-1); //take one off count to remove 'Submit'
	$POSTsubarray = array_slice($_POST, 0, $userCount);
	}
	print_r($POSTsubarray);
    foreach($POSTsubarray as $key => $gmail) {
	//get users regIDs
	$sql="select registrationID from users where gmail ='".$gmail."'";
        $myquery = $conn->prepare($sql);
        $myquery->execute();
		foreach($myquery->fetchAll() as $IDresult) {
        $registrationIDs[] = $IDresult['registrationID'];
                }
        }
	print_r($registrationIDs);
	

// Message to be sent
//$message = "[{'title':'A test event', '0':'A test event', 'dtstart':'1343908800000', '1':'1343908800000', 'dtend':'1343912400000', '2':'1343912400000', 'description':'Not a lot', '3','Not a lot', 'eventTimezone':'GMT', '4':'GMT', 'calendar_id':'2', '5':'2'}]";

// Set POST variables
$url = 'https://android.googleapis.com/gcm/send';

$fields = array(
                'registration_ids'  => $registrationIDs,
                'data'              => array( "message" => $message ),
                );

$headers = array( 
                    'Authorization: key=' . $apiKey,
                    'Content-Type: application/json'
                );

// Open connection
$ch = curl_init();

// Set the url, number of POST vars, POST data
curl_setopt( $ch, CURLOPT_URL, $url );

curl_setopt( $ch, CURLOPT_POST, true );
curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers);
curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );

curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $fields ) );

// Execute post
$result = curl_exec($ch);

// Close connection
curl_close($ch);

echo $result;
?>