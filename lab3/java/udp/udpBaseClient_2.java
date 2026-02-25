package lab3.java.udp;

import java.io.*;
import java.net.*;

import lab3.java.udp.model.Employee;

/**
 * UDP Client Demo
 * 
 * Features:
 * - Send text message
 * - Serialize and send Employee object
 * - Receive server response
 */
public class udpBaseClient_2 {

    // ================================
    // Configuration
    // ================================
    private static final int SERVER_PORT = 1234;
    private static final int BUFFER_SIZE = 65535;

    public static void main(String[] args) {

        System.out.println("UDP Client started.");
        System.out.println("Type messages (hello | employee | bye to exit)");

        try (
                DatagramSocket socket = new DatagramSocket();
                BufferedReader consoleReader =
                        new BufferedReader(new InputStreamReader(System.in))
        ) {

            InetAddress serverAddress = InetAddress.getLocalHost();
            byte[] receiveBuffer = new byte[BUFFER_SIZE];

            while (true) {

                System.out.print("Input: ");
                String input = consoleReader.readLine().trim();

                // ================================
                // Exit condition
                // ================================
                if (input.equalsIgnoreCase("bye")) {

                    sendMessage(socket, serverAddress, SERVER_PORT,
                            input.getBytes());

                    System.out.println("Client exiting...");
                    break;
                }

                // ================================
                // Prepare data to send
                // ================================
                byte[] sendData = prepareSendData(input);

                // ================================
                // Send UDP packet
                // ================================
                sendMessage(socket, serverAddress, SERVER_PORT, sendData);
                System.out.println("Sent: " + input);

                // ================================
                // Receive server response
                // ================================
                System.out.println("Waiting for server reply...");

                DatagramPacket receivePacket =
                        new DatagramPacket(receiveBuffer, receiveBuffer.length);

                socket.receive(receivePacket);

                processServerResponse(receivePacket);

                // Reset buffer
                receiveBuffer = new byte[BUFFER_SIZE];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Client closed");
    }

    // =====================================================
    // Prepare data based on user input
    // =====================================================
    private static byte[] prepareSendData(String input) throws IOException {

        // Send plain string
        if (input.equalsIgnoreCase("hello")) {
            return input.getBytes();
        }

        // Serialize Employee object
        if (input.equalsIgnoreCase("employee")) {

            Employee joe = new Employee(123456789, "Joe B.");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(joe);
            oos.flush();

            return baos.toByteArray();
        }

        // Default: send raw text
        return input.getBytes();
    }

    // =====================================================
    // Send UDP message (Reusable networking function)
    // =====================================================
    private static void sendMessage(
            DatagramSocket socket,
            InetAddress address,
            int port,
            byte[] data
    ) throws IOException {

        DatagramPacket packet =
                new DatagramPacket(data, data.length, address, port);

        socket.send(packet);
    }

    // =====================================================
    // Process server response
    // =====================================================
    private static void processServerResponse(DatagramPacket packet) {

        String reply =
                new String(packet.getData(),
                        0,
                        packet.getLength()).trim();

        System.out.println("Server replied: " + reply);

        // Try deserialize object response
        try {

            ByteArrayInputStream bais =
                    new ByteArrayInputStream(
                            packet.getData(),
                            0,
                            packet.getLength()
                    );

            ObjectInputStream ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();

            if (obj instanceof Employee) {
                System.out.println("Received Employee object: " + obj);
            }

        } catch (Exception ignored) {
            // Ignore if response is not serialized object
        }
    }
}
