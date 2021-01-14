import java.io.*;
import java.net.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.lang.String;
import java.lang.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.nio.file.Files;


//Quick Change

//client_handler class
class client_handler extends Thread
{
    final BufferedReader inFromClient;
    final DataOutputStream outToClient;
    final Socket s;
    private ArrayList<client_handler> clients;
    final String crlf = "\r\n"; //Carriage return line feed
    private String server_response;
    private int timeout = 5000; //in ms

    //constructor
    public client_handler(Socket s, ArrayList<client_handler> clients, BufferedReader inFromClient, DataOutputStream outToClient)  
    { 
        this.s = s; 
        this.clients = clients;
        this.inFromClient = inFromClient; 
        this.outToClient = outToClient;  
    }

    public boolean unSupportedVersion(String s){
        if (s.equals("1.1") || s.equals("2.0") || s.equals("3.0") || s.equals("2") || s.equals("3")) {
            return true;
        } else {
            return false;
        }
    }

    //Returns true if method field is GET, POST, HEAD and HTTP version is 1.0. False if anything else. If it returns False, write response to socket and close connection
    public boolean validRequestLine(String[] client_request){
        if(client_request.length != 3){
            System.out.println("Check 1");
            server_response = "HTTP/1.0 400 Bad Request" + crlf + crlf;
            return false;
        } else {
            String command = client_request[0];
            String resource = client_request[1];
            String version = client_request[2];
            //check if the version of HTTP is valid ("HTTP/1.0")
            if(version.compareTo("HTTP/1.0") != 0 && !version.equals("HTTP/0.9")) {
                if(version.length() > 5)
                {
                    if(version.substring(0,5).compareTo("HTTP/") == 0 && unSupportedVersion(version.substring(5)) ){ 
                        server_response = "HTTP/1.0 505 HTTP Version Not Supported" + crlf + crlf;
                    } else {
                        server_response = "HTTP/1.0 400 Bad Request" + crlf + crlf;
                    }
                    return false;
                } else{
                    server_response = "HTTP/1.0 400 Bad Request" + crlf + crlf;
                    return false;
                }
            } else if(command.compareTo("GET") != 0 && command.compareTo("POST") != 0 && command.compareTo("HEAD") != 0){
                //command is valid for 1.0 but not supported
                if(command.compareTo("DELETE") == 0 || command.compareTo("PUT") == 0 || command.compareTo("LINK") == 0 || command.compareTo("UNLINK") == 0){
                    server_response = "HTTP/1.0 501 Not Implemented" + crlf + crlf;
                    return false;
                }
                else {
                    server_response = "HTTP/1.0 400 Bad Request" + crlf + crlf;
                    return false;
                }
            } else if(command.compareTo("GET") == 0){
                return true;
            }
            else if(command.compareTo("POST") == 0){
                return true;
            }
            else if(command.compareTo("HEAD") == 0){
                return true;
            }

            return false; //Just here as placeholder Haven't made sure it is correct
        }
    }

    //Return true if the object has been modified since the date, and false otherwise. If false, write 304 Not Modified to server response
    public boolean hasBeenModified(String url, String dateLastModified){ 
        //try{
            try{
                URL resource = getClass().getResource(url);
                if(resource == null){
                    server_response = ("HTTP/1.0 404 Not Found" + crlf + crlf);
                    return false;
                }else{
                    File f = new File(resource.getPath());
                    SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                    Date result = df.parse(dateLastModified);    
                    long lastModified = result.getTime();
                        if(lastModified > f.lastModified() && lastModified <= System.currentTimeMillis()){ //FILE NOT MODIFIED SINCE and since date less than current system time
                            server_response = ("HTTP/1.0 304 Not Modified" + crlf + "Expires: Sat, 21 Jul 2021 11:00:00 GMT" + crlf + crlf);
                            return false;
                        }
                }   
            }catch(ParseException pe){
                //pe.printStackTrace(); 
                //server_response = ("HTTP/1.0 400 Bad Request" + crlf + crlf); //invalid date = Bad request? No @66 If date invalid, ignore the conditional
                return true; //Assume has been "modified", or date is in invalid form and proceed like GET
            }
        //}catch(IOException e) {
        //    e.printStackTrace();
        //}

        return true;
    }

    public String OK_headers(File f){
        //Allow, Content-Encoding, Content-Length, Content-Type, Expires, Last-Modified
        String ret = "HTTP/1.0 200 OK" + crlf;
        try
        {
            Date d = new Date(f.lastModified());
            if(Files.probeContentType(f.toPath()) != null)
                ret = ret + "Content-Type: " + Files.probeContentType(f.toPath()) + crlf;
            else
                ret = ret + "Content-Type: application/octet-stream" + crlf;
            ret = ret + "Content-Length: " + f.length() + crlf;
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            ret = ret + "Last-Modified: " + sdf.format(d) + crlf;
            ret = ret + "Content-Encoding: identity" + crlf; 
            ret = ret + "Allow: GET, POST, HEAD" + crlf; //no sure if this is right
            ret = ret + "Expires: Sat, 21 Jul 2021 11:00:00 GMT" + crlf + crlf; 
        }catch (IOException e) {
            System.out.println("Something went wrong trying to create headers/probe content");
            ret = "HTTP/1.0 500 Internal Server Error" + crlf + crlf;
            e.printStackTrace();
        }
        return ret;      
    }

    /*
    //File the file starting from the current working directory all the way down. If file missing, return null
    public File returnFile(String url){
        return null;
    }
    */
    public String handleRequest(String command, String url, String version) throws IOException {
        //Code that handles a GET or POST or HEAD command goes here
        //Check if there is a Conditional GET
        String next = inFromClient.readLine();
        String[] parseHeader = next.split(": "); // was (" ", 2) (?)
        boolean proceedWithGET = true;

        if (parseHeader.length == 2 && parseHeader[0].equals("If-Modified-Since") && !command.equals("HEAD")){
            if(!hasBeenModified(url, parseHeader[1])){
                proceedWithGET = false; //Means object hasn't been modified so we don't need to get the object
                
            }
            if(proceedWithGET){
                URL resource = getClass().getResource(url);
                File f = new File(resource.getPath());
                if(!f.canRead()){
                    return ("HTTP/1.0 403 Forbidden" + crlf + crlf);
                }else{
                    String tempServerResponse = OK_headers(f);
                    if (tempServerResponse.equals("HTTP/1.0 500 Internal Server Error" + crlf + crlf)) return tempServerResponse; //Error occurred while making headers
                    //Read file into byte array
                    FileInputStream fileStream = null;
                    try {
                        fileStream = new FileInputStream(f);
                        long length = f.length();
                        if (length > Integer.MAX_VALUE){
                            throw new IOException("File is too big");
                        }
                        byte[] byteArray = new byte[(int)length];
                        int offset = 0;
                        int alreadyRead = 0;

                        while (offset < byteArray.length && (alreadyRead = fileStream.read(byteArray, offset, byteArray.length - offset)) >= 0){
                            offset += alreadyRead;
                        }

                        if (offset < byteArray.length){
                            throw new IOException("Couldn't read complete file");
                        }

                        //Read all bytes of file into array, transfer info into socket
                        outToClient.writeBytes(tempServerResponse);
                        outToClient.write(byteArray, 0, byteArray.length); //Writes byte array (file) to socket

                    } catch (IOException e) {
                        //Return Internal Server Error
                        tempServerResponse = "HTTP/1.0 500 Internal Server Error" + crlf + crlf;
                        return tempServerResponse;

                    } finally {
                        if (fileStream != null){
                            fileStream.close();
                        }
                    }
                    return "";
                    //need body of file and 500 Internal Server Error
                }
            }
        }else{
            URL resource = getClass().getResource(url);
            if(resource == null)
                return ("HTTP/1.0 404 Not Found" + crlf + crlf);
            else{
                File f = new File(resource.getPath());
                if(!f.canRead()){
                    return ("HTTP/1.0 403 Forbidden" + crlf + crlf);
                }else{
                    String tempServerResponse = OK_headers(f);
                    if (tempServerResponse.equals("HTTP/1.0 500 Internal Server Error" + crlf + crlf)) return tempServerResponse; //Error occurred while making headers
                    //ONLY DO SO IF THERE IS NO HEAD
                    if (!command.equals("HEAD")){
                        FileInputStream fileStream = null;
                        try {
                            fileStream = new FileInputStream(f);
                            long length = f.length();
                            if (length > Integer.MAX_VALUE){
                                throw new IOException("File is too big");
                            }
                            byte[] byteArray = new byte[(int)length];
                            int offset = 0;
                            int alreadyRead = 0;

                            while (offset < byteArray.length && (alreadyRead = fileStream.read(byteArray, offset, byteArray.length - offset)) >= 0){
                                offset += alreadyRead;
                            }

                            if (offset < byteArray.length){
                                throw new IOException("Couldn't read complete file");
                            }

                            //Read all bytes of file into array, transfer info into socket
                            outToClient.writeBytes(tempServerResponse);
                            outToClient.write(byteArray, 0, byteArray.length); //Writes byte array (file) to socket

                        } catch (IOException e) {
                            //Return Internal Server Error
                            tempServerResponse = "HTTP/1.0 500 Internal Server Error" + crlf + crlf;
                            return tempServerResponse;

                        } finally {
                            if (fileStream != null){
                                fileStream.close();
                            }
                        }
                        return "";
                        /*
                        FileInputStream fileStream = new FileInputStream(f);
                        outToClient.writeBytes(tempServerResponse);
                        int b;
                        while ((b = fileStream.read()) != -1){
                            outToClient.write(b);
                        }
                        return "";
                        */ 
                    }
                    return tempServerResponse;
                    //need body of file and 500 Internal Server Error
                }
            }
            
        }
        
        return server_response;
    }

    public void printHTTPLine(String[] client_request){
        //Just to make sure client_request has valid input (ONLY FOR DEBUGGING , DELETE FOR SUBMISSION)
        if (client_request != null) {
            System.out.println("Just for testing, print the length of line: " + client_request.length);
            for (String value : client_request) {
                System.out.println("[" + value + "]");
            }
            System.out.println("End of test");
        }
    }


    public void run()  
    {  
        System.out.println("client connected!");
        String client_sentence;
        server_response = "";
        try{

            s.setSoTimeout(timeout); //Sets the timeout for the socket to 5 seconds
            try{
                client_sentence = inFromClient.readLine();
            } catch (SocketTimeoutException e){
                System.out.println("Socket did not response in " + timeout + " milliseconds");
                outToClient.writeBytes("HTTP/1.0 408 Request Timeout" + crlf + crlf);
                s.close();
                return;
            }

            String client_request[] = client_sentence.split(" ");; // "[ \\r\\n]"


            printHTTPLine(client_request);//DELETE FOR SUBMISSION



            if(validRequestLine(client_request)){
                //Request line is OK, and first line in http response is "HTTP/1.0 200 OK"
                //We know the method is going to be either HEAD POST or GET
                server_response = handleRequest(client_request[0], client_request[1], client_request[2]);
            }
             
            if (server_response.length() != 0){
                System.out.println("Writing to client: " + server_response);
                outToClient.writeBytes(server_response);
            }
            
            System.out.println("Done writring");

        }catch (IOException e) {
            e.printStackTrace();
        }
    } 
}

// PartialHTTP1 Server Class
class PartialHTTP1Server 
{
    //initializing the thread pool - starting with 5 
    //private static ExecutorService pool = Executors.newFixedThreadPool(5);
    //Will limit number of threads that can run at same time to 5 and total number of threads queued and run to 50
    private static ExecutorService pool = new ThreadPoolExecutor(5, 49, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
    //arraylist to keep track of client threads
    private static ArrayList<client_handler> clients = new ArrayList<>();
    
    public static void main(String args[]) throws Exception 
    {
        //check if there is one argument (port number)
        if (args.length != 1) 
        {
            System.err.println("Usage: java PartialHTTP1Server <port number>");
            System.exit(1);
        }
        //create server socket given port number
        int portNumber = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(portNumber);
        System.out.println("Server has been initialized on port " + portNumber);
        
        //wait for clients to connect
        while (true) 
        {
            //when client connects to server, obtain input and out streams, and create thread
            Socket connectionSocket = serverSocket.accept();

            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            client_handler clientThread  = new client_handler(connectionSocket, clients, inFromClient, outToClient);
            
            clients.add(clientThread);

            try{
                pool.execute(clientThread);
            } catch (RejectedExecutionException e){
                System.out.println("Number of threads is over 50, rejecting accept");
                outToClient.writeBytes("HTTP/1.0 503 Service Unavailable\r\n\r\n");
                connectionSocket.close();
            }

            //pool.shutdown();
        }
    }
}