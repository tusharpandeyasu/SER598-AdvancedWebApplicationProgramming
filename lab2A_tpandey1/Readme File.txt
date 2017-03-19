Task 1: Simple servlet functionality 

Task 1 Part 1:
--------------

a) Access http://localhost:8080/ser422/lab2task1/webform.html to open the Web Form.

The HTML form has attributes First Name, Last Name, Programming Languages known, Available on Day(s) of the week and the additional attribute Contact Number.

Task 1 Part 2:
--------------

a) Start the Tomcat server at Port 8080 and access the web form at http://localhost:8080/ser422/lab2task1/webform.html

b) Upon entering the above attributes (First Name, Last Name, Programming Languages known, Available on Day(s) of the week and the additional attribute Contact Number) the user hits the 'Submit' button.

c) The form is submitted to the Servlet 'InputDataServ' that handles the POST request upon receipt of the information and persist it to an XML file named Lab2.xml

d) The servlet displays messsage Success! and displays the Total number of records in the file for sucessful transaction and vice versa.

e) It also has a hyper Link 'Back to Web Form' to visit the first page webform.html.

Task 1 Part 3:
--------------

a) The third part of Task 1 requires the user to query the xml file using the GET method that list the records based on the user input for different attributes (First Name, Last Name, Programming Languages known, Available on Day(s) of the week and the additional attribute Contact Number).

b) The task uses different servlet named 'QueryDataServ' that uses the doget method to process the GET request from browser. The user interacts with the servelet through the url by accessing and providing the desired values for the attributes.

Eg:
Input parameter string for url having detalis as firstname=Tush, 
lastname=Pan, 
languages=C and Ruby
Available days- Monday, Tuesday and Wednesday
Contact=13123123123

http://localhost:8080/ser422/lab2task1/get_coders?firstname=Tush&lastname=Pan&languages=C+Ruby&days=Monday+Tuesday+Wednesday&contact=13123123123


c) If the user doesnot provide value for any attribute all the records in the xml file are displays as Results.
Input Parameter on browser
http://localhost:8080/ser422/lab2task1/get_coders

The parameters of different attributes are AND’d together, while within a multi-valued attribute it is an OR.

d) If the browser is a Chrome browser then set a pink background color.



