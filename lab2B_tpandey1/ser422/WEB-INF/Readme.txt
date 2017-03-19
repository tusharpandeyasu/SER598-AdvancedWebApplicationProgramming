Task 2: Simple servlet functionality 

Task 2 Part 1: Create a landing page that remembers who you are
---------------------------------------------------------------

1) The user access the location http://localhost:8080/ser422/lab2task2/get_coders.

2) If the user is the first time visitor then it redirects the user to html webform1 where First Name and Last name are proivided and the form is submitted. Upon form submission a message "Welcome for the first visit" is displayed.

3) Now if the same user in Step 1 access the location http://localhost:8080/ser422/lab2task1/get_coders the a message Welcome Again! is displayed for this user as it is an existigng user. 



Task 2 Part 2:  Make a multi-page web form 
--------------------------------------------

a) Start the Tomcat server at Port 8080 and access the web form at http://localhost:8080/ser422/lab2task2/webform1

b) Enter the First Name and Last Name and click Next Button to navigate to the next Page.

c) On second Pasge enter Programming Languages Known and the user can click Prev or Next to navigate backward and Next respectively. Click Prev to cbeck that the previous values of First Name and Last name are persisted. Click Next on Page 2 to move on to third page after selecting desired languages.

d) On third page, select desired value(s) for Available on Day(s) of the week and the user can navigate back and forth using Prev and Next as before.

e) On fourth page, enter the Contact Number and click next for review and submit.

d) On the last page all the values entered by the users on previous screens are diplayed. The user can Submit or Cancel the action from here on.

e) On Submit, the values are writtemn into Lab2.xml file and the user is redirected to First page webform1. On the clicking Cancel button the details entered by the user are not written in the xml Lab2.xml and user is redirected to First page webform1. Also, the HTML “remembered” widget values are cleared for both of these actions performed.

f) Handle Thread Safety in program code Lab2Task2Serv.java using SYNCHRONIZED block.

 
Task 2 Part 3: Return the proper response error codes
------------------------------------------------------

a) Proper response codes were handled by configuration of Deployment Descriptor, web.xml file and making appropriate chaanges in the servlet program wherever appropriate like 'Lab2Task2AServ', 'ErrorHandler'. Also another servlet ErrorHandler was created to handle the appropriate response error codes like 404 ( Not Found) , 403 ( Forbidden ), 405 SC_METHOD_NOT_ALLOWED. Additionally, I handled exceptions like javax.servlet.ServletException and java.io.IOException and general exception java.lang.Throwable.






x