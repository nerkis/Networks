/* First steps toward a multithreaded webserver
 * Nicole Erkis
 * 2/16/12
 */

import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class WebServer
{
	// you can choose any port higher than 1024, but remember to use
	// the same port number when making requests to your web server
	// from your browser

	public static void main(String args[]) throws Exception
	{
		// set the port number
		int port = 5020;

		// establish the listen socket
		ServerSocket listenSocket = new ServerSocket(5020);	

		// process HTTP service requests in an infinite loop
		while (true) {
			// listen for a tcp connection request
			Socket connectSocket = listenSocket.accept();

			// construct an object to process the HTTP request message
			HttpRequest request = new HttpRequest(connectSocket);

			// create a new thread to process the request
			Thread thread = new Thread(request);

			// start the thread
			thread.start();
		}
	}
}

final class HttpRequest implements Runnable
{
	final static String CRLF = "\r\n";
	Socket socket;

	// constructor
	public HttpRequest(Socket socket) throws Exception
	{
		this.socket = socket;
	}

	// implement the run() method of the Runnable interface
	public void run()
	{
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception
	{
		//construct a 1K buffer to hold bytes on their way to the socket
		byte[] buffer = new byte[1024];
		int bytes = 0;

		//copy requested file into the socket's output stream
		while((bytes = fis.read(buffer)) != -1)
		{
			os.write(buffer, 0, bytes);
		}
	}

	private static String contentType(String fileName)
	{
		if (fileName.endsWith(".htm") || fileName.endsWith(".html"))
		{
			return "text/html";
		}
		if (fileName.endsWith(".gif"))
		{
			return "image/gif";
		}
		if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg"))
		{
			return "image/jpeg";
		}
		return "application/octet-stream";
	}

	private void processRequest() throws Exception
	{
		//get a reference to the socket's input and output streams
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());

		//set up input stream filters
		InputStreamReader inReader = new InputStreamReader(is);
		BufferedReader bufferInput = new BufferedReader(inReader);

		//get the request line of the HTTP request message
		String requestLine = new String(bufferInput.readLine()); 

		//display the request line
		System.out.println();
		System.out.println(requestLine);

		//get and display the header lines
		String headerLine = null;
		while ((headerLine = bufferInput.readLine()).length() != 0) 
		{
			System.out.println(headerLine);
		}

		//extract the filename from the request line
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();	//skip over the method, which should be "GET"
		String fileName = tokens.nextToken();

		//prepend a "." so that file request is within the current directory
		fileName = "." + fileName;

		//open the requested file
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}

		//construct the response message
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists)
		{
			statusLine = "HTTP/1.0 200 OK" + CRLF;
			contentTypeLine = "Content-type: " +
				contentType(fileName) + CRLF;
		}
		else 
		{
			statusLine = "HTTP/1.0 404 Not Found" + CRLF;
			contentTypeLine = "Content not found" + CRLF;
			entityBody = "<HTML>" +
				"<HEAD><TITLE>Not Found</TITLE></HEAD>" +
				"<BODY>Not Found</BODY></HTML>";
		}
		
		//send the status line
		os.writeBytes(statusLine);

		//send the content type
		os.writeBytes(contentTypeLine);

		//send a blank line to indicate the end of the header lines
		os.writeBytes(CRLF);

		//send the entity body
		if (fileExists)
		{
			sendBytes(fis, os);
			fis.close();
		}
		else {
			os.writeBytes(entityBody);
		}

		//close streams and socket
		os.close();
		bufferInput.close();
		socket.close();
	}
}
