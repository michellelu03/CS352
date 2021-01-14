import java.io.*;
import java.net.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.lang.String;
import java.lang.*;
import java.io.IOException;
import java.io.PrintWriter;
  
// Client class 
public class Client  
{
    final static private String crlf = "\r\n";

    public static void main(String[] args) throws IOException  
    {
        if (args.length != 1) {
            System.out.println("Please enter a port number");
            System.exit(1);
        }

        String sentence;
        String modifiedSentence;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        //Socket clientSocket = new Socket("hostname", 3456);
        //DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        //PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
        Socket clientSocket = new Socket("localhost", Integer.parseInt(args[0]));
        clientSocket.setSoTimeout(2000);
/*
        for (int i = 0; i < 10; i++) {
            Socket client = new Socket("localhost", Integer.parseInt(args[0]));
        }
*/      InputStream in = null;
        try {
            in = clientSocket.getInputStream();
        } catch (IOException e) {
            System.out.println("Couldn't make input stream");
        }
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        //BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("connected to server! Enter command:");
        //sentence = inFromUser.readLine();
        //sentence = sentence + "\r";
        //outToServer.println(sentence);
        //while(true)
        //{
        sentence = "HEAD /index.html HTTP/1.0"; //Edit command here \r\nIf-modified-since: Last January
        System.out.println("Sending command: [" + sentence + "]");
        outToServer.writeBytes(sentence + crlf + crlf);
        //modifiedSentence = inFromServer.readLine();
        System.out.println("From Server [");
        

        byte[] b = new byte[8192];
        int count;
        try {
            while ((count = in.read(b)) > 0){
                System.out.write(b, 0, count);
            }  
        } catch (SocketTimeoutException e) {
            
        }
        
        System.out.write('\n');

        //System.out.println(modifiedSentence);
        //}
        clientSocket.close();
    } 

} 
