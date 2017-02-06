<?php                                        
session_start();             
?>                             
<!DOCTYPE html>                
<html lang="nl">                  
  <head>                                                            
    <link rel="shortcut icon" href="favicon.jpg">                   
    <meta charset="utf-8">                                                                                                
    <title>Parkinggarage Groningen</title>                           
    <link rel="stylesheet" type="text/css" href="style.css">       
  </head>                                                                          
  <body>                            
    <div id="wrapper">     
      <div id="header">                
        <a class='button' href='index.php'>Back</a>
      </div>
      <div id="inhoud"> 

        <?php 
        include('dbconfig.php'); 

        //pulling data from previous page
        $name = $_POST['name'];
        $day = $_POST['day'];
        $hours = $_POST['hours'];
        $minutes = $_POST['minutes'];
        $duration1 = $_POST['duration1'];
        $duration2 = $_POST['duration2'];

        $duration = ($duration1 * 60) + $duration2;
        $time = $hours.':'.$minutes;

        //adding reservation to database        
        $sql = "INSERT INTO reservation (name, day, hours, minutes, duration) 
                VALUES ('$name','$day', '$hours', '$minutes', '$duration')";

        if ($mysqli->query($sql)) {

        echo "<h2>Thank you!</h2>         
              <p>Your reservation has been made</p> ";
        echo'
            <table width="50%">
                <tr>
                  <td>Your name:</td>
                  <td>'.$name.'</td>     
                </tr>
                <tr>
                  <td>Day:</td>
                  <td>'.$day.'</td>       
                </tr>
                <tr>
                  <td>Time:</td>
                  <td>'.$time.'</td>
                </tr>
                <tr>
                  <td>Duration of stay:</td>
                  <td>'.$duration.' minutes</td>       
                </tr>
            </table>';

      } else {
        echo "<br>Something went wrong :( <br> Please try again";
      }

        ?>

        <br><div id="footer"><a>&#169; <?php echo date("Y"); ?> Eudora</a></div>                                                               
      </div> 
     </div>                                                                                                       
    </div>
  </body>
</html>    