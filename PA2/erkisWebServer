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
		Socket listenSocket = new Socket("hostname", 5020);

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

	private void processRequest() throws Exception
	{
		// get a reference to the socket's input and output streams
		InputStream is = socket.getInputStream();
		DataOutputStream os = socket.getOutputStream();

		// set up input stream filters
		InputStreamReader inReader = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(inReader);

		// get the request line of the HTTP request message
		String requestLine = br.readLine();

		// display the request line
		System.out.println();
		System.out.println(requestLine);

		// get and display the header lines
		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) 
		{
			System.out.println(headerLine);
		}

		//close streams and socket
		os.close();
		br.close();
		socket.close();
	}
}
