<?php
    $server = 'localhost';
    $user = 'inven_nl_parking';
    $password = '4hc9HH4MgozU';
    $database = 'inventc_nl_parking';   

    $mysqli = new mysqli($server, $user, $password, $database);

    if ($mysqli->connect_error) {

    die ("De poging om een verbinding te maken met de database is mislukt");

    }

?>