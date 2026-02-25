package lab3.java.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class udpBaseServer_2 {

    public static void main(String[] args) throws IOException {

        // Create a DatagramSocket bound to port 1234
        // This allows the server to receive UDP datagrams sent to port 1234
        DatagramSocket ds = new DatagramSocket(1234);

        // Buffer used to store incoming data
        byte[] receive = new byte[65535];

        DatagramPacket DpReceive = null;

        /*
        ============================================================
        IMPORTANT:
        This is a SINGLE-THREADED UDP server.
        - It processes one datagram at a time.
        - It does NOT create new threads.
        - It does NOT handle multiple clients concurrently.
        - All incoming packets are processed sequentially.
        ============================================================
        */

        while (true) {

            // Create a DatagramPacket to receive data
            DpReceive = new DatagramPacket(receive, receive.length);

            // Block until a datagram is received
            ds.receive(DpReceive);

            // Convert received byte data into string
            System.out.println("Client:-" + data(receive));

            // If client sends "bye", terminate the server
            if (data(receive).toString().equals("bye")) {
                System.out.println("Client sent bye.....EXITING");
                break;
            }

            // Clear the buffer after every message
            receive = new byte[65535];
        }

        // Close the socket when done
        ds.close();
    }

    // ============================================================
    // Convert byte array data into a readable string
    // Stops when it encounters a null byte (0)
    // ============================================================
    public static StringBuilder data(byte[] a) {

        if (a == null)
            return null;

        StringBuilder ret = new StringBuilder();

        int i = 0;

        // Read until null terminator
        while (a[i] != 0) {
            ret.append((char) a[i]);
            i++;
        }

        return ret;
    }
}
