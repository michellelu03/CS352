Instructions
Wireshark Lab 2
TCP Flow reporting

In this assignment your code will identify TCP flows and product a report characterizing then, as well basic information about other protocols, such as UDP and ICMP.  Your code will take a packet capture (pcap) file of recorded packets and produce the following two tables, in csv (comma separated value) format, that summarize the traffic. The first table, the TCP Summary Table, characterizes TCP flows and the second table, the Additional Protocols Table,  summarized all other protocols.

You must work individually on this project (no project partners).
 
TCP Summary Table

The TCP summary is organized around reporting properties of TCP flows. We will define a flow in the next section. The two properties of a flow this project will report are the number of packets and the bandwidth. That is, each row of the table shows these properties between a given set of ports between a client and server (or visa-versa).
 
Definition of a TCP Flow:
A TCP flow is identified by the 4 tuple: Source IP address, Source Port, Destination IP and Destination Port. A TCP flow can be thought of as a "half" connection in that the direction between the client and server is a different row in the table than the direction between the server and client. Such reporting is useful because it allows us to see asymmetric bandwidth flows. A set of TCP connections defines a flow between a client and server.

A TCP flow begins with a packet with the SYN flag set starting the flow, and ends with a packet with the FIN bit set. Such a  flow  is called a completed. A  flow  with a SYN bit set but no matching FIN packet is called an incomplete flow. A  flow with a FIN packet and no matching SYN is also an incomplete flow. SYN and FIN packets thus form a boundary defining the TCP flows. If no matching pair of SYN/FIN is found, the flow  is marked as incomplete. Sequences of complete flows are summarized as a multiple rows in the table. For example, if a client makes multiple HTTP connections to a server those are summarized as a single flow with one row in the table per instance of a flow. For example, the the medium2_pcap.txt sample output, we see 2 TCP connections resulted in two rows in the table, with one flow having 14 packets and the 2nd flow having 16 packets:
192.168.2.220, 53096, 52.149.246.247, 443, 16, 0, 1836, 132.32432432432432 
192.168.2.220, 53098, 52.149.246.247, 443, 14, 0, 1740, 120.0 

TCP Summary Table:
 
Column number	1	2	3	4	5	6	7	8
Name	SrcIP	SrcIP	DestIP	DestPort	Total Packets in Completed flows	Total Packets in Incomplete flows	Total Bytes	Average Bandwidth
( Mbps )
 
Flow  Identifier Section (Columns 1-4)
Columns 1-4 are the key for each row. That is, every flow is unique and identified by these values in the table.
Flow Summary Section (Columns 5-8)
These columns show summary information about this flow. It shows the total number of complete flow, the total number of incomplete flows, and the total number packets, and average bandwidth of a flow. The bandwidth is computed using the time of the first and last packet of a flow when one has has at least 2 packets. Incomplete flows, e.g. a sequence of SYN packets, are not included in the bandwidth computation. 
Note: Mbps = Megabits per Second, 1 byte = 8 bits,  1,000,000 bits = 1 Megabits
Additional Protocols Summary Table
 
The additional protocols summary table is very simple. It only shows the total number of bytes (including header information) and packets for protocols other than TCP. For all packets other than TCP, the byte count should include the entire packet, including the Ethernet header.
 
Protocol	Packets	Bytes
UDP	 	 
ICMP	 	 
Other	 	 
 
Output Format
Output the TCP summary table first, with the first line with the string "TCP Summary Table". Then output a set of comma separated strings of the TCP summary table. After that table is output, the next line of output must be the string "Additional Protocols Summary Table" followed by newline and then the rows of the additional protocol summary table, which each field separated by a comma.

Recommended Strategy:

The recommended strategy is to define a TCP flow object type, which holds information on a TCP flow. The object would have various fields for identifying the flow as well as counters for the number of packet and bytes, as well as timing and completion information.

You would treat the five-tuple of source IP address, source port, destination IP and destination port, and arrival time of the SYN and an unique identifier (ID) for the TCP flow. When a new packet arrives, you can update the byte and packet counters in the TCP flow object, as well as the timing information. When a FIN packet is seen, you would update the completion status of the flow.

All other packet types would update the additional protocols summary table.

Once the TCP flow objects are created, you will organize these objects into various lists. The first lists you should create is one that grouped around the 4-tuple of srcIP, srcPort, destIP and destPort (forming another key, leaving the the arrival time as a free parameter). Each of these lists grouping the same four-tuple ID forms the basis for a row in the TCP flow summary table. 

For each of these groups, further sort the TCP flow objects by the number of bytes, as well as create a  second sorted list of these objects sorted by the bandwidth.
 
Grading: 

You will be given 2 test pcap files (small.pcap and medium2.pcap) and their sample output files (small_pcap.txt) and medium2_pcap.txt) and we will also run your code on 2 additional pcap files. Also, your code must run on the ilab machines. If you use libraries that are not on the ilab machines, your code will be considered incorrect. 

The grading percentage will be based on the percentage of correct flows you report in the table, which each incorrect flow lowering the number of correct flows by one. Suppose there were 10 flows in pcap file. If you reported 8 correctly and 2 incorrectly you would receive 60% (8-2 = 6/10).

Upload a single file only. The main to execute must be called: App.java, and it should print its report to standard out, i.e. just use regular print statements. 
