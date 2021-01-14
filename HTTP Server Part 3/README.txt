Overview
In this assignment you will extend the capabilities of your current webserver by building a web site which uses cookies.

Background on Cookies
 
In the HTTP protocol, cookies are a simple mechanism that allows the server and client to remember state between them. The basics of cookies are covered in this tutorial. When the server returns a payload/web page back to the client, it can include a sequence of set-cookie lines in the HTTP response header. For example, a set of lines in the HTTP header returned by the server might look like this:
 

HTTP/2.0 200 OK
Content-Type: text/html
Set-Cookie: lasttime=2020-12-11%2012%3A30%3A12
Cookies are variables/value pairs. In the above example the variable name  lasttime in the header of the HTTP response is set to the value "2020-12-11%2012%3A30%3A12".  This is the URL encoded string of "2020-12-11 12:30:12".

The client will return the names and values of the cookies back to the server on every web-request. Cookies can thus be uses to build session state. See the links at the bottom of the page for a tutorial and definition of cookies.

In this assignment, your server will print out the time since the client last visited the server.

Overview

The task is to implement a web server which recalls the last time a user accessed the site and returns a page welcoming the user back the the last time the user was at the site.
the first time the browser/client/tester sends a get request for the root document ( /), the server should return the file index.html, (in the attachments), which must reside in the home directory of the project.
the server replies with index.html as the payload and a single set-cookie line. The value of the cookie as a date string that is URL encoded according to the standard encoding. The variable of the cookie must be "lasttime" and value must be a URL encoded time-stamp in the form <YEAR>-<Month>-<Day> <Hour>-<Minute>-<Second> in 24 hour format. An example is a line in the HTTP header "".
the browser/client sends another request for the root document (/), however, with the cookie set. The variable will be "lasttime" and the value will be a valid URL encoded time-stamp as above.
the server checks the cookie is valid, and if so, it creates a response payload, index_seen.html, which has a string "Welcome back! Your last visit was at: <Year>-<month>-<Day> <hour>-<minute>-<second>, where the time is the time returned in the cookie. See the template file below (index_seen.html) for an example.
The server returns a response with an updated cookie with the current time, and the HTTP body as above.
Example Encoding and Decoding the Date and Time for the cookie:
This example shows how to encode and decode the data and time for the cookie:

        import java.net.*;
        import java.util.*;
        import java.time.LocalDateTime;
        import java.time.format.DateTimeFormatter;

        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        System.out.printf("Formatted date+time %s \n",formattedDate);
        
        String encodedDateTime = URLEncoder.encode(formattedDate, "UTF-8");
        System.out.printf("URL encoded date-time %s \n",encodedDateTime);
        
        String decodedDateTime = URLDecoder.decode(encodedDateTime, "UTF-8");
        System.out.printf("URL decoded date-time %s \n",decodedDateTime);

 

Assignment Mechanics 
Recall that one third (30%) of your grade is related to your source code, so make sure to create comments and use small, well-defined methods and classes when appropriate.

This assignment is expected to take around 4-6 hours to complete, though it may take more or less time depending on your abilities and background. It is essential that you work effectively with your group to complete it in a reasonable about of time.

You must submit via the Sakai Assignments tool.  Only on-time assignments submitted through Sakai will be accepted.

Your Web server MUST run on the ilab machines. You can develop on other platforms (e.g., Windows. Mac, or your own Linux machine), but your code must run on the ilabs correctly to be considered correct. Code which does not run correctly will have points taken off at the discretion of the TA and instructor.

You make work in a team as in the previous assignments. See below for the README file format.

Specifics of Operation
Your program's main file must be named "HTTP3Server.java" without any Java packaging. This means that running your compiled program will be identical to the output below:

  java -cp . HTTP3Server 3456

You should be sparing in the use of additional classes, however you should design as you feel fit.

Note: Your server MUST run on the ilab machines correctly. You can develop on another system (e.g. Windows or Mac), but if your server does not run on the ilab machines points will be deducted at the discretion of the TA.

What to hand in.
Include a README.txt file with group members
We are NOT using Sakai groups for this assignment. Instead,  If you work in a group, you MUST include a file called README.txt that lists the names of your group members. The file must list one person per line, in the format  <netid>:<name>.  Only one person in the group needs to submit.

Compress all your source files into a '.zip' (Windows zip) or 'tar.gz' (Linux gzipped tarball) named "HTTP1Server.zip" or "HTTP1Server.tar.gz"

Submit your compressed file. Do not use any Java packaging.

Grading Outline
    Source code organization and comments: 40%
    Correct parsing of all URL-coded arguments: 30%
    Efficient multi-user management: 30%
    

Testing:
A Java based tester client  is posted. See the attachments. Untar or unzip an the CookieTester.tgz/zip and see the README.txt for a description.

I still recommend using  the curl command on the command line as the tester. It's what I used as the initial test driver of the reference implementation. Open a terminal window on the ilab machine you use for development. Or, if you have curl installed on your local machine you can use that. You should test your sever both with and without an added cookie.

Suppose you have your server listening on port 4444 on the local machine:

Download the URL for the first time and save the cookie to a local file:
     curl -c saved-cookies.txt http://localhost:4444

Make sure you server returned the correct cookie.

Download the URL and add the cookie with the -b flag:
  curl -b "lasttime=2020-12-06+20%3A48%3A50" http://localhost:4444
