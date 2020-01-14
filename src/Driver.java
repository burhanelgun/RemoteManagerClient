import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Driver {

	public static void main(String[] args) {
		
		try {
			ServerSocket serverSocket = new ServerSocket(8888);
		    while (true) {
				System.out.println("Listening the socket on port 8888...");
	            Socket socket = serverSocket.accept();
				System.out.println("New client connected...");

	            ClientProgram h = new ClientProgram(socket);
	            h.start();
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
   }
	
	
}
