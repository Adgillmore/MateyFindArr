     <?php
     
    include 'logon.php';
    include 'ServerLog.php';
 
    // Logging class initialization
    $log = new Logging();
    
    $resultarray = array();
     
    //Need to change to a list of user IDs to retrieve
    foreach($_POST as $key => $gmail) {
	$sql="select gmail, latitude, longitude, speed, max(timestamp) latestTimestamp, status from session where gmail =:gmail";
        $myquery = $conn->prepare($sql);
        $myquery->execute(array(':gmail'=>$gmail));
        $result = $myquery->fetch();
        if ($result==null) {
        $log->lwrite("Retrieved null location");
        $log->lclose();
        }
        $resultarray[] = $result;
    }
    // write message to the log file
    $log->lwrite("Retrieved users' locations");
    $log->lclose();

    print(json_encode($resultarray))
    ?>