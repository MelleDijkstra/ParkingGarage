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
      <div id="inhoud">
        <img src="images/car.jpg" style="float:right;">
      
        <h2>Welcome</h2>         
        <p>Make your reservation</p>  

        <form action='bevestiging.php' method='post' enctype="multipart/form-data">
          <div class='list'> 
            <table width="100%">
                <tr>
                  <td>Your name:</td>
                  <td><input required name='name' type='text' class='list-control' style="width:35%" placeholder="Barney Stinson"></td>     
                </tr>
                <tr>
                  <td>Day:</td>
                  <td><select class="list-control" name="day" style="width:50%">
                         <option value="1">Monday</option>
                         <option value="2">Thuesday</option>
                         <option value="3">Wednesday</option>
                         <option value="4">Thursday</option>
                         <option value="5">Friday</option>
                         <option value="6">Saturday</option>
                         <option value="7">Sunday</option>
                       </select>
                  </td>       
                </tr>
                <tr>
                  <td>Time:</td>
                  <td>
                    <select class="list-control" name="hours" style="width:25%">
                       <option>00</option>
                       <option>01</option>
                       <option>02</option>
                       <option>03</option>
                       <option>04</option>
                       <option>05</option>
                       <option>06</option>
                       <option>07</option>
                       <option>08</option>
                       <option>09</option>
                       <option>10</option>
                       <option>11</option>
                       <option>12</option>
                       <option>13</option>
                       <option>14</option>
                       <option>15</option>
                       <option>16</option>
                       <option>17</option>
                       <option>18</option>
                       <option>19</option>
                       <option>20</option>
                       <option>21</option>
                       <option>22</option>
                       <option>23</option>
                    </select>
                    <select class="list-control" name="minutes" style="width:25%">
                       <option>00</option>
                       <option>01</option>
                       <option>02</option>
                       <option>03</option>
                       <option>04</option>
                       <option>05</option>
                       <option>06</option>
                       <option>07</option>
                       <option>08</option>
                       <option>09</option>
                       <option>10</option>
                       <option>11</option>
                       <option>12</option>
                       <option>13</option>
                       <option>14</option>
                       <option>15</option>
                       <option>16</option>
                       <option>17</option>
                       <option>18</option>
                       <option>19</option>
                       <option>20</option>
                       <option>21</option>
                       <option>22</option>
                       <option>23</option>
                       <option>24</option>
                       <option>25</option>
                       <option>26</option>
                       <option>27</option>
                       <option>28</option>
                       <option>29</option>
                       <option>30</option>
                       <option>31</option>
                       <option>32</option>
                       <option>33</option>
                       <option>34</option>
                       <option>35</option>
                       <option>36</option>
                       <option>37</option>
                       <option>38</option>
                       <option>39</option>
                       <option>40</option>
                       <option>41</option>
                       <option>42</option>
                       <option>43</option>
                       <option>44</option>
                       <option>45</option>
                       <option>46</option>
                       <option>47</option>
                       <option>48</option>
                       <option>49</option>
                       <option>50</option>
                       <option>51</option>
                       <option>52</option>
                       <option>53</option>
                       <option>54</option>
                       <option>55</option>
                       <option>56</option>
                       <option>57</option>
                       <option>58</option>
                       <option>59</option>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td>Duration of stay:</td>
                  <td><input required name='duration1' type='number' min='0' max='23' class='list-control' style="width:15%" value="0">
                      <input required name='duration2' type='number' min="0" max='59' class='list-control' style="width:15%" value="0">
                  </td>       
                </tr>
            </table> 
            <button type='submit' class="button">Send</button>
          </div>                  
        </form> 
        <br><div id="footer"><a>&#169; <?php echo date("Y"); ?> Eudora</a></div>                                                              
      </div>  
    </div>                                                                                                            
  </body>
</html>    