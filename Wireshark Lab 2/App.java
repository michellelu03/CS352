/*
package com.github.username;

//Hello World program
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
*/

// App.java
package com.github.username;

import java.io.IOException;
import java.net.Inet4Address;

import com.sun.jna.Platform;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapDumper;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapStat;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.packet.IpV4Packet.IpV4Header;
import org.pcap4j.packet.TcpPacket.TcpHeader;
import org.pcap4j.packet.UdpPacket.UdpHeader;
import org.pcap4j.packet.IcmpV4CommonPacket;
import java.text.*;	
import java.net.*; 	
import java.util.*;

//TCP flow object type
class TCPflow{
	//int packets = 1;
	int complete_packets = 0;
	int incomplete_packets = 0;
	int tbd_packets = 0;
	float total_bytes = 0;
	float complete_bytes = 0;
	double arrival_time = 0;
	double dest_time = 0;
	boolean syn = false;
	boolean fin = false;
	boolean complete = false;
}

public class App {
/*
	static int check_number = 0;
    static int UDP_number = 0;
	static int TCP_number = 0;
	static float total_byte = 0;

	static double first_pack_time = 0;
	static double last_pack_time = 0;
	static boolean first_packet_time= false;
	static boolean last_packet_time=false;
*/
	static int UDP_number = 0;
	static float UDP_bytes = 0;
	static int ICMP_number = 0;
	static float ICMP_bytes = 0;
	static int OTHER_number = 0;
	static float OTHER_bytes = 0;


    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        //System.out.println("Let's start analysis ");
        // New code below here
		if(args.length != 1){
			System.out.println("Error: Expecting one argument for pcapfile name.");
			return;
		}
       	final PcapHandle handle;
		try{
			handle = Pcaps.openOffline(args[0]);
		}catch(PcapNativeException p){
			System.out.println("Error: File not found or system unable to create PcapHandle object given argument.");

		return;
		}
		//HASHTABLE TO HOLD ALL TCP FLOWS
		HashMap<List, TCPflow> map = new HashMap<>(); 

	//System.out.println(handle);
        PacketListener listener = new PacketListener() {
            public void gotPacket(Packet packet) {
				/*
				//FROM GIVEN CODE
					if(first_packet_time==false)
					{
						first_pack_time = (double)handle.getTimestamp().getTime();
						first_packet_time=true;
					}
					last_pack_time = (double)handle.getTimestamp().getTime();

					check_number = 1+ check_number;
		 			total_byte = total_byte + (float)packet.length();
				*/
				if(packet.get(TcpPacket.class)!= null){
					IpV4Packet.IpV4Header IP = packet.get(IpV4Packet.class).getHeader();
					TcpHeader port = packet.get(TcpPacket.class).getHeader();
					List tempTuple = Arrays.asList(IP.getSrcAddr().toString().substring(1), port.getSrcPort().toString().split(" ")[0], IP.getDstAddr().toString().substring(1), port.getDstPort().toString().split(" ")[0]);
					if(!map.containsKey(tempTuple)){
						//this is a new flow!
						TCPflow tempFlow = new TCPflow();
						//tempFlow.arrival_time = (double)handle.getTimestamp().getTime();
						if(port.getSyn()){
							tempFlow.syn = true;
							tempFlow.complete_bytes = (float)packet.length();
							tempFlow.arrival_time = (double)handle.getTimestamp().getTime();
						}else if(port.getFin()){
							tempFlow.fin = true;
							tempFlow.dest_time = (double)handle.getTimestamp().getTime();
						}
						tempFlow.incomplete_packets = 1;
						tempFlow.total_bytes = (float)packet.length();
						map.put(tempTuple, tempFlow);
					}else{
						//already existing flow... need to update the fields
						TCPflow tempFlow = map.get(tempTuple);
						if(!port.getFin() && !port.getSyn()){ //regular packet
							tempFlow.incomplete_packets = tempFlow.incomplete_packets + 1;
							if(tempFlow.syn && !tempFlow.complete)
								tempFlow.complete_bytes = tempFlow.complete_bytes + (float)packet.length();
						}else if(port.getFin()){ 
							tempFlow.fin = true;
							tempFlow.dest_time = (double)handle.getTimestamp().getTime();
							if(tempFlow.syn){ //complete flow!
								if(tempFlow.tbd_packets == 0){
									tempFlow.complete_packets = /*tempFlow.complete_packets + */tempFlow.incomplete_packets + 1;
									tempFlow.incomplete_packets = 0;
								}else{
									tempFlow.complete_packets = /*tempFlow.complete_packets + */tempFlow.incomplete_packets - tempFlow.tbd_packets + 1;
									tempFlow.incomplete_packets = tempFlow.tbd_packets;
								}
								tempFlow.complete_bytes = tempFlow.complete_bytes + (float)packet.length();
								tempFlow.complete = true;
							}else{ //incomplete flow
								tempFlow.incomplete_packets = tempFlow.incomplete_packets + 1;
							}
						}else if(port.getSyn()){
							tempFlow.syn = true;
							tempFlow.complete_bytes = (float)packet.length();
							tempFlow.arrival_time = (double)handle.getTimestamp().getTime();
							if(tempFlow.fin){ //incomplete flow
								tempFlow.incomplete_packets = tempFlow.incomplete_packets + 1;
							}else{
								//may be incomplete packets before syn 
								tempFlow.tbd_packets = tempFlow.incomplete_packets;
								tempFlow.incomplete_packets = tempFlow.incomplete_packets + 1;
							}
						}
						tempFlow.total_bytes = tempFlow.total_bytes + (float)packet.length();
						map.put(tempTuple, tempFlow);
					}
				}else if(packet.get(UdpPacket.class) != null){
			   		UDP_number = UDP_number + 1 ;
					UDP_bytes = UDP_bytes + (float)packet.length();
				}else if(packet.get(IcmpV4CommonPacket.class) != null){
					ICMP_number = ICMP_number + 1;
					ICMP_bytes = ICMP_bytes + (float)packet.length();
				}else{
					OTHER_number = OTHER_number + 1;
					OTHER_bytes = OTHER_bytes + (float)packet.length();
				}
            }
        };
        try {
	            int maxPackets = -1;
	                handle.loop(maxPackets, listener);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
            }

		/*
		double total_time = last_pack_time - first_pack_time;
		total_time = total_time/1000.0;
		System.out.println( "Total number of packets, "+  check_number);
		System.out.println( "Total number of UDP packets, " + UDP_number);
		System.out.println( "Total number of TCP packets, " + TCP_number);
		System.out.println( "Total bandwidth of the packet trace in Mbps, " + total_byte/total_time/125000.0);
		*/
		System.out.println("TCP Summary Table");
		for (Map.Entry mapElement : map.entrySet()) { 
            List key = (List)mapElement.getKey(); 
			TCPflow value = (TCPflow)mapElement.getValue(); 
    		System.out.print(key.get(0) + ", " + key.get(1) + ", " + key.get(2) + ", " + key.get(3) + ", " + value.complete_packets + ", " + value.incomplete_packets);
			if(value.complete){
				double avg = (value.complete_bytes / ((value.dest_time - value.arrival_time)/1000000.0) /125000.0);
				System.out.print(", " + value.total_bytes + ", " + avg);
			}
			System.out.println();
        } 
		System.out.println();
		System.out.println("Additional Protocols Summary Table");
		System.out.println("UDP, " + UDP_number + ", " + UDP_bytes);
		System.out.println("ICMP, " + ICMP_number + ", " + ICMP_bytes);
		System.out.println("Other, " + OTHER_number + ", " + OTHER_bytes);

        // Cleanup when complete
        handle.close();
    }
}