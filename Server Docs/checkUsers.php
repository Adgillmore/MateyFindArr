<?php
 
    include 'logon.php';
    include 'ServerLog.php';
 
    // Logging class initialization
    $log = new Logging();

$queryString = array();

    foreach($_POST as $key => $gmail) {
	$queryString[] = $gmail;
	}	
	$csl = implode("', '", $queryString); //process comma separated list
	$sql="select gmail from users where gmail in ('".$csl."')";
	$myquery = $conn->prepare($sql);
        $myquery->execute();
        $result = $myquery->fetchAll();
    $log->lwrite("Cross-checked users");
    $log->lclose();
    print(json_encode($result));
    ?>				