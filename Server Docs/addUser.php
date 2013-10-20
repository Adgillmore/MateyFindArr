<?php
 
    include 'logon.php';
    include 'ServerLog.php';
 
    // Logging class initialization
    $log = new Logging();
 
    $gmail = ""; //If post does not contain gmail then empty string is returned
    $regID = "";
 
    if (isset($_POST['gmail'])) {
    $gmail = $_POST['gmail'];
    }
 
    if (isset($_POST['regID'])) {
    $regID = $_POST['regID'];
    }
 
    $sql1 = "select count(*) as entries from users where gmail = :gmail";
    $sql2 = "insert into users (gmail, registrationID) values (:gmail, :regID)";
 
    $gmailCheck = $conn->prepare($sql1); //returns PDO statement, value ['exists'] from 0 to whatever;
	$gmailCheck->execute(array(':gmail'=>$gmail));
	
    $gmailResult = $gmailCheck->fetch(PDO::FETCH_ASSOC);//fetches array from PDO statement.
    $code = $gmailCheck->errorInfo();
    
    if ($gmailResult['entries'] == 0 && $regID !="" && $gmail !="") { //checks if gmail address is new (i.e. not in database)
    $response = $conn->prepare($sql2); //creates new gmail entry.
	$response->execute(array(':gmail'=>$gmail, ':regID'=>$regID));
    // write message to the log file
    $log->lwrite("Registered user");
    $log->lclose();
    $code = $response->errorInfo();
    } else {
    // write message to the log file
    $log->lwrite("User not registered - already exist in database");
    $log->lclose();
    }
 
    print(json_encode($code));
    ?>	