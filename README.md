
# Log File Parser

Build server logs different events to a file named logfile.txt. Every event has 2 entries in the file 
- one entry when the event was started and another when the event was finished. The entries in the file have 
no specific order (a finish event could occur before a start event for a given id) Every line in the file is a 
JSON object containing the following event data:

- id - the unique event identifier 
- state - whether the event was started or finished (can have values "STARTED" or "FINISHED" 
- timestamp - the timestamp of the event in milliseconds 

Application Server logs also have the following additional attributes: 
- type - type of log 
- host - hostname


## Project Details
This is a Standalone Spring Boot Application 
which bundles the code into an executable jar.

Once after execution user is presented with menu where
user can perform various operation for the log file.

Below are the list of operation perform by the Application

    1. Parse the log file
    2. Generate the json file having all the event data
    3. Generate the json file having event data by id
    4. Genrate the json file having event data by alert type (true or false)
    5. Generate the dummy log file to for processing
    6. Exit from menu.


## Project Requirement

Below are the list of project requirement that are handled in this project

- Program takes path to logfile.txt from user via menu driven application. 
- Program Parses the contents of logfile.txt 
- Program flags any long events that take longer than 4ms 
- Program stores the found event details to 
  file-based HSQLDB having following values: 
    - Event id 
    - Event duration 
    - Type and Host if applicable 
    - Alert (true if the even
- Program uses logger to print info and debug messages
- Implemented multithreaded solution
- Program handles very large files (gigabytes).
  Tested with One million records
- Integrated the Asnchronous approch to parse the file 
## Steps to execute the program

    1. Download the jar from below URL :

        https://github.com/yspbiz85/cs/blob/master/bundle/cs-0.0.1-SNAPSHOT.jar

    2. Execute the jar using command 

        java -jar cs-0.0.1-SNAPSHOT.jar

    3. Select the options from menu
    4. For Generating Dummy Log file,Press 5 (Refer below images)  
        
        ![image](https://raw.githubusercontent.com/yspbiz85/cs/master/img/Dummy1.png)
        ![image](https://raw.githubusercontent.com/yspbiz85/cs/master/img/Dummy2.png)
