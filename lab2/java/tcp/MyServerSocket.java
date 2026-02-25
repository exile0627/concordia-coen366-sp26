package lab2.java.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/*
========================================================
TCP Server Socket Example
========================================================
This program demonstrates how to create a TCP server socket,
bind to a specific IP address and port, and accept client
connections to receive messages.
========================================================
*/

public class MyServerSocket {

    private ServerSocket server;

    /*
    =====================================================
    Constructor
    =====================================================
    Create a server socket.
    If IP address is provided → bind to that address.
    Otherwise → bind to localhost.
    Port number = 0 means OS will automatically assign an available port.
    Backlog = 1 means only 1 pending connection is allowed in queue.
    =====================================================
    */
    public MyServerSocket(String ipAddress) throws Exception {
        if (ipAddress != null && !ipAddress.isEmpty())
            this.server = new ServerSocket(0, 1, InetAddress.getByName(ipAddress));
        else
            this.server = new ServerSocket(0, 1, InetAddress.getLocalHost());
    }

    /*
    =====================================================
    Listen for client connections
    =====================================================
    accept() is a blocking call:
    - The server thread will wait here until a client connects.
    - Once connected, a dedicated socket is created for communication.
    =====================================================
    */
    private void listen() throws Exception {
        String data = null;

        // Block until a client connects
        Socket client = this.server.accept();

        // Get client IP address
        String clientAddress = client.getInetAddress().getHostAddress();
        System.out.println("\r\nNew connection from " + clientAddress);

        // Read text data from client using buffered reader
        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream())
        );

        // Read messages line by line until client closes connection
        while ((data = in.readLine()) != null) {
            System.out.println("\r\nMessage from " + clientAddress + ": " + data);
        }
    }

    /*
    =====================================================
    Get server IP address
    =====================================================
    */
    public InetAddress getSocketAddress() {
        return this.server.getInetAddress();
    }

    /*
    =====================================================
    Get server port number
    =====================================================
    */
    public int getPort() {
        return this.server.getLocalPort();
    }

    /*
    =====================================================
    Program Entry
    =====================================================
    */
    public static void main(String[] args) throws Exception {
        args = new String[]{"0.0.0.0"};

        MyServerSocket app = new MyServerSocket(args[0]);

        System.out.println("\r\nRunning Server: " +
                "Host=" + app.getSocketAddress().getHostAddress() +
                " Port=" + app.getPort());

        app.listen();
    }
}
