package lab3.java.tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import lab3.java.model.Employee;

/*
========================================================
TCP Client (Protocol Driven Version)
========================================================
Protocol with server:
--------------------------------
1. Send "hello"
   → Server replies "send"

2. Send serialized Employee object
   → Server echoes object back

3. Send normal text
   → Server echoes text

4. Send "bye"
   → Server may terminate connection
========================================================
*/

public class MyClientSocket {

    private final Socket socket;
    private final Scanner scanner;

    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;

    /*
    =====================================================
    Constructor
    =====================================================
    Establish TCP connection and create object streams.
    IMPORTANT:
    ObjectOutputStream must be created BEFORE
    ObjectInputStream to avoid stream deadlock.
    =====================================================
    */
    private MyClientSocket(InetAddress serverAddress, int serverPort) throws Exception {

        this.socket = new Socket(serverAddress, serverPort);
        this.scanner = new Scanner(System.in);

        /*
        Order matters:
        ----------------------------
        Server and client must create ObjectOutputStream
        before ObjectInputStream on opposite sides.
        Otherwise handshake may block.
        */

        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.oos.flush();

        this.ois = new ObjectInputStream(socket.getInputStream());
    }

    /*
    =====================================================
    Start communication loop
    =====================================================
    Supports protocol based messaging.
    =====================================================
    */
    private void start() throws Exception {

        System.out.println("Type messages (hello / employee / text / bye)");

        while (true) {

            System.out.print("Input: ");
            String input = scanner.nextLine().trim();

            /*
            -------------------------------------------------
            Exit condition
            -------------------------------------------------
            */
            if (input.equalsIgnoreCase("bye")) {

                oos.writeObject("bye");
                oos.flush();

                System.out.println("Client exiting...");
                break;
            }

            /*
            -------------------------------------------------
            Send data according to protocol
            -------------------------------------------------
            */

            if (input.equalsIgnoreCase("hello")) {

                // Send handshake message
                oos.writeObject("hello");
                oos.flush();

            } else if (input.equalsIgnoreCase("employee")) {

                // Serialize and send Employee object
                Employee joe = new Employee(123456789, "Joe B.");

                oos.writeObject(joe);
                oos.flush();

            } else {

                // Send normal text message
                oos.writeObject(input);
                oos.flush();
            }

            /*
            -------------------------------------------------
            Receive server response
            -------------------------------------------------
            */

            try {
                Object response = ois.readObject();
                System.out.println("Server reply: " + response);

            } catch (EOFException e) {
                System.out.println("Server closed connection");
                break;
            }
        }

        close();
    }

    /*
    =====================================================
    Cleanup
    =====================================================
    */
    private void close() throws IOException {
        socket.close();
        scanner.close();
    }

    /*
    =====================================================
    Program Entry
    =====================================================
    */
    public static void main(String[] args) throws Exception {

        args = new String[]{"127.0.0.1", "8080"};

        MyClientSocket client = new MyClientSocket(
                InetAddress.getByName(args[0]),
                Integer.parseInt(args[1])
        );

        System.out.println("Connected to Server: "
                + client.socket.getInetAddress());

        client.start();
    }
}
