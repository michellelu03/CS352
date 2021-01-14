Background
Wireshark, tshark, and tcpdump are programs that use the pcap library. Pcap (for Packet Capture) is a library which works with the operation system to make copies of packets inside the operating system and deliver them to user-level programs for analysis. The pcap library, originally written in C, has been ported to numerous languages, including Java, Python, and Go.
 

Assignment

You will work individually on this assignment. The goal of this assignment is for you to familiarize yourself with the Java version of pcap. Your task will be to get the App.java to build and run correctly. App.java is already written, the goal is to make sure you have the development environment working for the Wireshark 2 project. You have to hand in the App.java as a reminder to actually get it working. (You could just hand it in, but then you may get stuck trying to get pcap4j applications working before the next deadline.

App.java accepts a file in the pcap format, which is generated with the tcpdump wireshark or tshark   commands.  The report is an example of simple packet analysis, for example, detecting possible port-scanners and attacks on a set of local IP addresses. 
Given a pcap file as input, App.java program prints out the following contents. The contents of each section will be:  

  
The number of all packets, which includes all types. 

  
  
The number of UDP packets seen in the file. 

  
  
The number of TCP packets seen in the file.

  
  
The total bandwidth of the packet trace. That is, the total number of bytes seen divided by the total elapsed time. The bandwidth includes all the headers as well as data. 

  
The header strings for each section are:

  
"Total number of packets,<COUNT>", where <count> is an integer.

  
  
"Total number of UDP packets,<COUNT>", where count is an integer

  
  
"Total number of TCP packets,<COUNT>", where count is an integer

  
  
"Total bandwidth of the packet trace in Mbps,<Bandwidth>", where bandwidth is a floating point number of the megabits per second of the trace. 

  
 

How to get started: 

The Pcap4j examples use Maven as the Java build environment. Follow the instructions in the attached tutorial to get the Pcap4j examples working using Maven on the ilab machines. You will modify the App.java code (included in the attachments) and hand that in.
 
 The strategy App.java to generate the report is to write a main loop that reads in all the packets one at a time, extracts the needed information from them, and then sums up the types (TCP or UDP). 

In order to read the pcap file and count the packets, look at the example source code -- see the tutorial an attachments). 

File Attachments: 

  
The App.java file.

  
  
Tutorial for starting project.

  
  
2 test pcap files (small.pcap , http.pcap )

  
  
2 example outputs on the pcap files. Your program should match the counts in these files. (small_pcap_out.txt , http_pcap_out.txt)

  
Use the following commands to run and test the code. (Put the pcap files in the compiled folder)

java -jar uber-pcap-1.1.0.jar small.pcap

java -jar uber-pcap-1.1.0.jar http.pcap


What to Hand in: 

You must hand in a single file called App.java. (Yes, you could just hand this in, but get it to run first!) 
 
 Do not hand any zip files, tar files, or additional packaging (e.g. Maven pom files, etc). 

Grading: 

You handed in a working App.java code:

  
Compile success: 20%

  
  
Correct number of all packets: 20% 

  
  
Correct number of UDP packets: 20% 

  
  
Correct number of TCP packets: 20%

  
  
Correct total bandwidth of the packet trace: 20% 

