Project 1: Java Web Server for HTTP 1.0
Due Date Moved to: Friday, October 2, 11:55PM
UPDATED! Assignment Instructions, include a README.txt file with group members

We are NOT using Sakai groups for this assignment. Instead,  If you work in a group, you MUST include a file called README.txt that lists the names of your group members. The file must list one person per line, in the format  <netid>:<name>.  Only one person in the group needs to submit.
Overview
  In this assignment you will design a web server in Java that supports a functional subset of the HTTP 1.0 protocol, the initial full release of the HyperText Transfer Protocol. Your web server will have to accept incoming socket connections, read a request from the client, and answer with the appropriate HTTP status code.

  This assignment is expected to take around 12 to 28 hours to complete, though it may take more or less time depending on your abilities and background. It is essential that you work effectively with your group to complete it in a reasonable amount of time.

  It is due 11:55PM Sept. 30.  You must submit via the Sakai Assignments 2 tool.  Only on-time assignments submitted through Sakai will be accepted.


Specifics of Operation
  Your program's main file must be named "PartialHTTP1Server.java" without any Java packaging. This means that running your compiled program will be identical to the output below:

  java -cp . PartialHTTP1Server 3456

  You should be sparing in the use of additional classes, however you should design as you feel fit.

  If you work in a group with more than one person, you MUST include a file called README.txt that lists the names of your group members. The file must be in the format of one person per line, in the format  <netid>:<name>.  That is, each line of the README.txt file must have your netid, followed by a ":" as the separator, and then your name.  Have only one person in the group submit.

  Compress all your source files and README.txt file into a '.zip' (Windows zip) or 'tar.gz' (Linux gzipped tarball) named "PartialHTTP1Server.zip" or "PartialHTTP1Server.tar.gz"

  Submit your compressed file. Do not use any Java packaging.


To successfully complete this assignment, your program must do the following:

    Accept the port to listen on as args[0] in the main method.  The port should be parsed into an int or Integer.

    Construct a ServerSocket that accepts connections on the port specified in the command-line argument.

    When a client connects to your ServerSocket, you are to hand off the created Socket to a Thread that handles all the client's communication. Absolutely no reading or writing to or from a Socket should occur in the class that the ServerSocket is accepting connections in except when all your Threads are busy (see below).

    Your server should support at most 50 simultaneous connections efficiently. You should have new connections serviced by Threads in an extensible data structure or Thread pool that holds space for no more than 5 Threads when the server is idle. Your pool of available Threads, or space for Threads, should expand and contract commensurate with the average rate of incoming connections. Your server loop should NOT ACCEPT new connections if all 50 Threads are busy and should instead send a "503 Service Unavailable" response and immediately close the connection. This is the only time the main class that holds your ServerSocket is allowed to use a Socket it created for communication.

    In your communication Thread you should read a single String from the client, parse it as an HTTP 1.0 request and send back an appropriate response according to the HTTP 1.0 protocol.
  
   You must support GET, POST and HEAD commands.
   You must support MIME types (see which below).
   Your server must also include all elements of a properly-formatted HTTP 1.0 response.
   You need not implement the HTTP 1.0 commands DELETE, PUT, LINK or UNLINK.

    Once your response has been sent, you should flush() your output streams, wait a quarter second, close down all communication objects and cleanly exit the communication Thread.

    Be careful to handle Exceptions intelligently. No Exceptions should be capable of crashing your server.

    
    

HTTP 1.0:
HTTP 1.0 is fully defined in RFC-1945

1. Version tags:
    Every HTTP 1.0 request and response must have the HTTP version in it.

    Requests:
        <command> <resource> HTTP/1.0
        
    Response:
        HTTP/1.0 <status code> <explanation>
        <response head>
        
        <response body>
 
    If your server receives a request that does not have a version number, it is considered malformed and should get a "400 Bad Request" response.
    If your server receives a request that has a version number greater than 1.0, the version is higher than what you can support, and you should respond with a "505 HTTP Version Not Supported"

    
2. Additional request commands:
    HTTP 1.0 has multiple additional request commands. You however only need to support HEAD and POST for this version. For this version, treat POST the same as GET.
    
    If you receive a valid HTTP/1.0 request command that you do not support, you should respond with "501 Not Implemented". If you receive request command that is not in HTTP/1.0, you should respond with a "400 Bad Request", so even if you do not implement all the commands in HTTP 1.0, you must still recognize them.
 

3. Header fields:
    HTTP 1.0 responses are much more textured and contain a series of fields that provide additional information about the data. You should be sure to add the following header fields to each "200 OK":
    
    Allow, Content-Encoding, Content-Length, Content-Type, Expires, Last-Modified
    
    HTTP 1.0 requests also have additional header fields. On the whole you can ignore them expect for:
    
    If-Modified-Since
    

    
4. MIME types:
    MIME types were originally engineered for SMTP in order to describe how data should be parsed and interpreted. The Content-Type header field in HTTP 1.0 responses is a MIME type, as defined in RFC-1521. Since MIME types describe the format of the data being delivered, they are based on a resource's file type extension and always consist of <type>/<subtype>. Regular ".html" files are, for instance "text/html". You should support delivering the following MIME types and subtypes:
 
    text/(html and plain)
    image/(gif, jpeg and png)
    application/(octet-stream, pdf, x-gzip, zip)
    
    If you ever receive a request for a resource whose MIME type you do not support or can not determine, you should default to 'application/octet-stream'.
    There are several resources that document MIME types and how they pair up with filename extensions, like this catalogue.
    

5. Response status codes:
    HTTP 1.0 has quite a few additional status codes. You should support:
    
    200    OK
    304    Not Modified
    400    Bad Request
    403    Forbidden
    404    Not Found
    408    Request Timeout
    500    Internal Server Error
    501    Not Implemented
    503    Service Unavailable
    505    HTTP Version Not Supported

6. A Request Timeout is required.

 If the client opens a connection with the server but does not send any request within 5 seconds, your server should send this response.

   408 Request Timeout
          
   

Grading Outline
    Source code organization and comments: 20%
    Correct parsing of all request commands: 10%
    Correct parsing of MIME types: 10%
    Efficient communication thread management: 20%
    Correct implementation of all response codes: 10%
    Inclusion of all response header fields: 10%
    Isolation of Thread Exceptions from server code: 10%    
    Closing all Sockets/IOStreams before Thread termination: 10%
    

    

Resources:
    RFC-1945: HTTP 1.0 alt:(RFC-1945: HTTP 1.0, RFC-1945: HTTP 1.0)
    RFC-1521: MIME alt: (RFC-1521: MIME)
    MIME type/file extension catalogue

    Sample HTTP 1.0 Response

   

 

Tester
We provide an auto tester for you to test your server. This tester has 20 test cases to check if your server satisfies all the requirements of the project instruction and HTTP/1.0 spec.

Also, we provide a tar.gz file that contains the resources that your server can server. The test cases of the auto tester are targeting these resources.

To use the auto tester:

(1) Download the resource package: doc_root.tar.gz

(2) Extract under Linux with this command: tar -zxvf doc_root.tar.gz

(3) You'll have a directory called "doc_root", which contains all the resources your server needs to server for the test cases.

(4) Put your server's compiled class file into the root directory of doc_root, and run it by: java -cp . PartialHTTP1Server 3456

(5) Download the tester: HTTPServerTester.jar

(6) Run the tester by: java -jar HTTPServerTester.jar <address> <port> . For instance: java -jar HTTPServerTester.jar localhost 3456

(7) Then you can see the test report. You can also write the report into a file for later review with a command like this: java -jar HTTPServerTester.jar localhost 3456 > report.txt
