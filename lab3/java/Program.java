package lab3.java;

import java.io.*;
import java.net.*;

public class Program {
    public static void main(String[] args) throws Exception {
        int port = 8080;

        // Create server and client threads
        Thread server = new Thread(new Server(port));
        Thread client = new Thread(new Client(port));

        server.start();
        client.start();

        server.join();
        client.join();
    }
}

/*
========================================================
Server side
========================================================
*/
class Server implements Runnable {
    private ServerSocket serverSocket; // TCP server socket

    public Server(int port) throws IOException {
        // Create server socket, bind to port, and listen on the port
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        try {
            // Block and wait for client connection
            Socket client = serverSocket.accept();

            /*
            ========================================================
            Deserialization happens HERE
            ========================================================
            */
            ObjectInputStream in =
                    new ObjectInputStream(client.getInputStream());

            // Read and reconstruct Employee object from byte stream
            Employee response = (Employee) in.readObject();

            /*
            ========================================================
            Serialization happens HERE
            ========================================================
            */
            ObjectOutputStream out =
                    new ObjectOutputStream(client.getOutputStream());

            // Serialize Employee object and send back to client
            out.writeObject(response);

            client.close();
            serverSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
========================================================
Client side
========================================================
*/
class Client implements Runnable {
    private Socket socket;

    public Client(int port) throws IOException {
        // Connect to server (localhost)
        socket = new Socket(InetAddress.getLocalHost(), port);
    }

    @Override
    public void run() {
        try {
            Employee joe = new Employee(123456789, "Joe B.");

            /*
            ========================================================
            Serialization happens HERE (Client → Server)
            ========================================================
            */
            ObjectOutputStream out =
                    new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Client sending: " + joe);

            // Convert Employee object → byte stream
            out.writeObject(joe);

            /*
            ========================================================
            Deserialization happens HERE (Server → Client)
            ========================================================
            */
            ObjectInputStream in =
                    new ObjectInputStream(socket.getInputStream());
            Employee response = (Employee) in.readObject();

            // Reconstructed object from byte stream
            System.out.println("Client received: " + response);
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Serializable Employee class for object transmission
// Serializable is a Marker Interface
class Employee implements Serializable {
    private int id;
    private String name;

    // Constructor
    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // String representation of Employee object
    @Override
    public String toString() {
        return "Name: " + name + ", ID: " + id;
    }
}
