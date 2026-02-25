package lab2.java.tcp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/*
========================================================
TCP Client Socket Example
========================================================
This program demonstrates how a TCP client connects to a
server using IP address and port number, and sends messages
to the server using a socket connection.
========================================================
*/

public class MyClientSocket {

    private Socket socket;
    private Scanner scanner;

    /*
    =====================================================
    Constructor
    =====================================================
    Create a client socket and connect to server.
    Parameters:
    - serverAddress → server IP address
    - serverPort → server port number
    =====================================================
    */
    private MyClientSocket(InetAddress serverAddress, int serverPort) throws Exception {
        // Establish TCP connection with server
        this.socket = new Socket(serverAddress, serverPort);

        // Create scanner to read user input from console
        this.scanner = new Scanner(System.in);
    }

    /*
    =====================================================
    Start client communication
    =====================================================
    Continuously read user input and send messages to server.

    This loop will run indefinitely until the program is
    terminated externally (e.g., user closes terminal or
    process is killed). No exit condition is implemented
    in the code.

    PrintWriter is used to send text data through the
    socket output stream. The println() method already
    adds a line terminator, and flush() ensures data is
    immediately sent through the network stream.
    =====================================================
    */
    private void start() throws IOException {
        String input;

        while (true) {
            // Read user input from keyboard
            input = scanner.nextLine();

            // Create output stream writer for socket
            PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);

            // Send message to server
            out.println(input);

            // Ensure data is flushed to network stream
            out.flush();
        }
    }

    /*
    =====================================================
    Program Entry
    =====================================================
    Command line arguments:
    args[0] → server IP address
    args[1] → server port number
    =====================================================
    */
    public static void main(String[] args) throws Exception {
        // Simulate command line arguments
        args = new String[]{"127.0.0.1", "50127"};

        // Create client and connect to server
        MyClientSocket client = new MyClientSocket(
                InetAddress.getByName(args[0]),
                Integer.parseInt(args[1])
        );

        System.out.println("\r\nConnected to Server: " +
                client.socket.getInetAddress());

        // Start sending messages
        client.start();
    }
}
