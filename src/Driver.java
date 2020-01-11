import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Driver {

	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(8888);
			System.out.println("1111");
		    while (true) {
				System.out.println("2222");
	            Socket socket = serverSocket.accept();
				System.out.println("3333");
	            ClientProgram h = new ClientProgram(socket);
				System.out.println("4444");
	            h.start();
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
   }
	
	
}
