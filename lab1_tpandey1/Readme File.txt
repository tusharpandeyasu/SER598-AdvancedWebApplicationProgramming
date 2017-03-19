This Readme file explains the usage, Input Parameter and design process flow adopted for the Lab1:

2) Command Line Input Parameter
For Server
java SockServer2 

For Client
java SockClient2  [int1/<r> command]
Usage: The user should enter an interger or hit 'r' key to reset the Running Total



3) Command Line Input Parameters
For Server
java SockServer3

For Client
java SockClient3   Client ID <int1>   [int2/<r> command]
Usage: The user should enter an interger (int1) as first parameter that corresponds to Client ID.
Second parameter can be an integer that adds to the running total or the user can hit 'r' key <r> to reset the Running Total for the client




5) Command Line Input Parameters
1st Part (To Generate XML File)
For Server
java SockServer5 

For Client
java SockClient5   Client ID<int1>   [int2/<r> command]

Usage: The user should enter an interger (int1) as first parameter that corresponds to Client ID.
Second parameter can be an integer that adds to the running total or the user can hit 'r' key <r> to reset the Running Total for the client

The program captures the information provided by the Client, namely Client ID and running total corresponding to the client ID in the XML file created dynamically named ClientsRunningTotal.xml when the user doesn't provide the XML File name and creates the xml file.


2nd Part (To restote from XML file when server starts up)
Second, when the user wants to upload the date from the Filename ClientsRunningTotal.xml to the hash map having attributes Client ID and Total he needs to provide the file name as the First Parameter. For loading the xml file to Hash Map

For Server
java SockServer5 ClientsRunningTotal.xml

For Client
java SockClient5   Client ID<int1>   [int2/<r> command]




6) The user needs to setup one prompt for the server and atleast 2 command prompts for Clients to test the working functionality of the threads to handle the running total for Both the Distinct Clients as well as modifying the data for a unique client simultaneously.

User can provide the First Parameter as an integer (5000ms fot exp.) which corresponds tto thread slepp in order to observe the multithread functionality in whiich the thread of same Clients overlap each other in order to perform operations concurrently. For the second part, the HashMap was replaced with the "ConcurrentHashMap", as part of the 2nd half of the Problem 6 in order to increase the throughout as it only locks a portion of the collection on update.

"ConcurrentHashMap" was used over HashMap and SynchronizedHashMap as it is thread safe without synchronizing the whole map. Moreover, there is no locking at the object 
level unlike SynchronizedHashMap where Synchronization at Object level. In this case reads can happen very fast while write is done with a lock whereas in the case of SynchronizedHashMap every read/write operation needs to acquire lock. Thus the program was designed using the "ConcurrentHashMap" over SynchronizedHashMap and HashMap.


Thanks!

Regards,
Tushar Pandey
Spring 2016
1210522167


