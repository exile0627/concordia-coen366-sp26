package lab3.java.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class udpBaseClient_2 {

    public static void main(String args[]) throws IOException {

        // Scanner to read user input from console
        Scanner sc = new Scanner(System.in);

        // Create a DatagramSocket
        // No port is specified → OS assigns an ephemeral (temporary) port
        DatagramSocket ds = new DatagramSocket();

        // Get the IP address of the local machine (localhost)
        InetAddress ip = InetAddress.getLocalHost();

        byte buf[] = null;

        /*
        ============================================================
        This is a UDP client.
        UDP is connectionless:
        - No connect() call is required
        - No handshake
        - Each send() is independent
        ============================================================
        */

        // Loop until user enters "bye"
        while (true) {

            // Read a line of input from user
            String inp = sc.nextLine();

            // Convert the String into a byte array
            buf = inp.getBytes();

            // Create a DatagramPacket containing the message
            // Destination: (ip, port 1234)
            DatagramPacket DpSend =
                    new DatagramPacket(buf, buf.length, ip, 1234);

            // Send the packet to the server
            ds.send(DpSend);

            // If user types "bye", stop sending messages
            if (inp.equals("bye"))
                break;
        }

        // Close the socket after finishing communication
        ds.close();
        sc.close();
    }
}
