Overview
In this assignment you will extend the capabilities of your current webserver by laying the groundwork for more extensive client/server interactions by more fully implementing POST and adding CGI.

Keep in mind that one third (30%) of your grade is related to your source code, so make sure to create comments and use small, well-defined methods and classes when appropriate.

This assignment is expected to take around 5 to 10 hours to complete, though it may take more or less time depending on your abilities and background. It is essential that you work effectively with your group to complete it in a reasonable about of time.

You must submit via the Sakai Assignments tool.  Only on-time assignments submitted through Sakai will be accepted.

 

Specifics of Operation
Your program's main file must be named "HTTP1Server.java" without any Java packaging. This means that running your compiled program will be identical to the output below:

  java -cp . HTTP1Server 3456

You should be sparing in the use of additional classes, however you should design as you feel fit.

Note: Your server MUST run on the ilab machines correctly. You can develop on another system (e.g. Windows or Mac), but if your server does not run on the ilab machines points will be deducted at the discretion of the TA.

What to hand in.
Include a README.txt file with group members
We are NOT using Sakai groups for this assignment. Instead,  If you work in a group, you MUST include a file called README.txt that lists the names of your group members. The file must list one person per line, in the format  <netid>:<name>.  Only one person in the group needs to submit.

Compress all your source files into a '.zip' (Windows zip) or 'tar.gz' (Linux gzipped tarball) named "HTTP1Server.zip" or "HTTP1Server.tar.gz"

Submit your compressed file. Do not use any Java packaging.

 
Overview

 
To successfully complete this assignment, your program must operate the same way as Project 1, except for two significant differences:
 
    POST operations must be supported as per RFC-1945.
    Support invoking server-side code using CGI.   

POST:
    POST requests differ in that they are not requests for a data resource, but a computation resource. In a POST request the client requests the server either run or interpret some data. POST commands are often used for parsing HTML FORM data. A POST request's URL is expected to be some executable code on the server side. Since a POST request often results in code execution, it must carry parameters as well. Parameters are contained in the request body and each parameter is separated by a "&". Each parameter is listed by: <NAME>=<VALUE> . If the Content-Type is anything else, for now, you should regard it as something you can not parse and respond with a 'Internal Server Error' code.
    
    e.g.:
    Request that the server run 'orderForm.cgi' located in folder /cgi_bin/ with parameters 'Name=Me' and 'cost=0':
        POST /cgi_bin/orderForm.cgi HTTP/1.0
        From: me@mycomputer
        User-Agent: telnet
        Content-Type: application/x-www-form-urlencoded
        Content-Length: 14

 

        Name=Me&cost=0
        
    
    Parameters are URL-coded, meaning that certain characters that are not allowed in URLs are replaced with escape symbols. You should correctly translate URL-coded parameters as per RFC-3986. You do not need to support the full UTF-8 character set and command codes. Only characters that are considered reserved under HTTP: "!     *     '     (     )     ;     :     @     $  +   ,     /     ?     #     [     ]" and whitespace. However, for this assignment we encode and decode the reserved characters using ! instead of %, that is:

x=! encoded becomes x=!!
x=!&y=@ encoded becomes x=!!&y=!@

Notice that the payload of the POST request is essentially the QUERY_STRING of the URI, in which  = and & have a specific meaning, therefore they remain un-escaped. The client (tester) will send the payload en-coded to your server, and your server needs to decode it before writing it to the STDIN of the cgi script.

In other words, for URL 

http://localhost/cgi_bin/CgiQuery.cgi?Exp=1!+2&Res=3
http is the scheme which tells it to go to the http server (nothing to do)
localhost points to the host where the server resides (nothing to do)
/cgi_bin/CgiQuery.cgi is the CGI script (retrieve this script and run it in a process)
Exp=1!+2&Res=3 is the query string encoded with ! as described above which shows in the payload (read the payload and decode it to Exp=1+2&Res=3, then write it to the process that runs the CGI script)
The post request received by the server for this URL will look like:

        POST /cgi_bin/CgiQuery.cgi HTTP/1.0
        From: me@mycomputer
        User-Agent: telnet
        Content-Type: application/x-www-form-urlencoded
        Content-Length: 14

 

        Exp=1!+2&Res=3

Notice that in the Testcase.txt the encoding does not show, so what you will see in the testcases for the payload is Exp=1+2&Res=3.

We do not allow the usage of java URI,  URL , URLEncode etc classes for this assignment!   

    

CGI:
    The Common Gateway Interface provides a single mechanism to invoke any type of code on any type of machine with an HTTP server. The most recent reliable spec is the CGI 1.1 draft, ca. 1995 . Rather than support the full spec, since it is fairly dated, what you should do is execute the code named in the CGI request URL in an environment where the parameters sent exist as name/value pairs, capture all the output from the script (redirect STDOUT) and send it as a text/html response to the requester. If a CGI script runs to completion successfully, but does not produce output, you should return a "204 No Content" code to indicate a success with no output. You will need to invoke code outside of Java using Java. I would recommend you look up  Java Runtimes and Processes. CGI scripts have the file type '.cgi'.
    

For this assignment, you don't need to write CGI scripts, we provide them to you, see attachments. All you need to do, is read the header and payload of the POST request, decode the encoded payload (as described above under URL-coded), and then run a process in your server with the cgi script and pass the decoded payload to the STDIN. You will also get the STDOUT of the process and return it as payload to the client.

You can run a CGI script by simple doing .[name of the script],

e.g.: ./cgi_bin/helloworld.cgi
Hello World!

helloworld.cgi does not require a parameter, but others do. the CGI scripts that expect parameters read them from the STDIN.
 

Grading Outline
    Source code organization and comments: 30%
    Correct parsing of all URL-coded arguments: 20%
    Efficient communication thread management: 10%
    Correct implemenation of all response codes: 10%
    Correct creation of server-side CGI environment variables and Runtime: 15%
    Correct execution and response capture of CGI data: 15%
    

    
Resources:
        RFC-1945
        HTTP POST
        HTTP POST vs GET
        HTTP POST examples
        
        RFC-3986
        URL coding
        URL percent coding

        CGI-1.1 (1995)
        HTTP CGI examples
        Excerpt from 'CGI Programming 101'
        

Notes:
Folder organization: Make a folder which will serve as the project_root for your project. Your server's main class and other auxiliary classes should be in this project_root. Extract the doc_root folder from part 1 and the cgi_bin folder attached to part 2 inside the project_root. 
(1) In this project, a GET request to a CGI script should be treated as a GET request to a normal document, there's no need to execute the CGI script.

(2) When the POST request doesn't have the "Content-Length" header, or the value is not numeric, your server should return "HTTP/1.0 411 Length Required".

(3) When the POST request doesn't have the "Content-Type" header, your server should return "HTTP/1.0 500 Internal Server Error".

(4) Other POST headers are not mandatory.

(5) If the POST request has good headers, but its target is not a CGI script, your server should return "HTTP/1.0 405 Method Not Allowed".

(6) If everything is good in the POST request, your server should:

make sure you downloaded cgi_bin.zip and extracted  cgi_bin folder where all the cgi scripts are
decode the payload according to RFC-3986 and the encoding scheme described above for the reserved characters.
set the CONTENT_LENGTH environment variable to the length of the decoded payload when executing the CGI script.
send the decoded payload to the CGI script via STDIN.
 

(7) Your server only needs to support these environment variables in this version:

CONTENT_LENGTH: Content-Length of the request.
SCRIPT_NAME: the path of the current CGI script. (Example: For the request "POST /cgi_bin/test.cgi HTTP/1.0", SCRIPT_NAME = /cgi_bin/test.cgi).
SERVER_NAME: the IP of the server.
SERVER_PORT: the port that the server is listening to.
HTTP_FROM: if the POST request has the header "From", then set this to the value of "From".
HTTP_USER_AGENT: if the POST request has the header "User-Agent", then set this to the value of "User-Agent".
(8) If the CGI script doesn't have the "execute" permission, then you should return "HTTP/1.0 403 Forbidden".

(9) Your server still needs to support all the features in Project 1.

(10) If a '.cgi' resource is fetch with GET assign it MIME type 'application/octet-stream'
