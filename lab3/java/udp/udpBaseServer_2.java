package lab3.java.udp;

import java.io.*;
import java.net.*;

import lab3.java.model.Employee;

/**
 * UDP Server Demo
 *
 * Important Concepts:
 * -------------------
 * 1. UDP is connectionless.
 * 2. Each datagram packet contains client address + port.
 * 3. This implementation is SINGLE-THREAD blocking server.
 * 4. Server CAN communicate with multiple clients,
 *    BUT only one packet is processed at a time.
 */
public class udpBaseServer_2 {

    private static final int PORT = 1234;
    private static final int BUFFER_SIZE = 65535;

    public static void main(String[] args) {
        udpBaseServer_2 server = new udpBaseServer_2();
        server.startServer();
    }

    // =====================================================
    // Server main loop (Blocking UDP Server)
    // =====================================================
    public void startServer() {

        System.out.println("UDP Server started on port " + PORT);

        /*
         * Blocking behavior explanation:
         * --------------------------------
         * socket.receive(packet)
         * will block current thread until:
         * - A UDP packet arrives
         *
         * This means server cannot process other tasks
         * while waiting for network data.
         */
        try (DatagramSocket socket = new DatagramSocket(PORT)) {

            byte[] buffer = new byte[BUFFER_SIZE];

            while (true) {

                System.out.println("Waiting for datagram...");

                DatagramPacket packet =
                        new DatagramPacket(buffer, buffer.length);

                // BLOCKING CALL
                // Server waits here until a client sends data
                socket.receive(packet);

                /*
                 * Even though UDP is connectionless,
                 * packet still contains:
                 * - Client IP address
                 * - Client port number
                 *
                 * So server can reply directly.
                 */
                handleRequest(socket, packet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Server closed");
    }

    // =====================================================
    // Request Handler
    // =====================================================
    private void handleRequest(
            DatagramSocket socket,
            DatagramPacket packet
    ) throws Exception {

        String message = parseMessage(packet);
        System.out.println("Client: " + message);

        InetAddress clientAddress = packet.getAddress();
        int clientPort = packet.getPort();

        byte[] responseBytes = null;

        // Business logic
        if (message.equalsIgnoreCase("bye")) {
            System.out.println("Client sent bye... EXITING");
            System.exit(0);
        }

        else if (message.equalsIgnoreCase("hello")) {
            responseBytes = "send".getBytes();
        }

        else {
            responseBytes = handleObjectRequest(packet, message);
        }

        sendResponse(socket, responseBytes, clientAddress, clientPort);
    }

    // =====================================================
    // Parse message from UDP packet
    // =====================================================
    private String parseMessage(DatagramPacket packet) {

        return new String(
                packet.getData(),
                0,
                packet.getLength()
        ).trim();
    }

    // =====================================================
    // Handle serialized object request
    // =====================================================
    private byte[] handleObjectRequest(
            DatagramPacket packet,
            String fallbackMessage
    ) {

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

                Employee emp = (Employee) obj;
                System.out.println("Received Employee: " + emp);

                // Echo serialized object back
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);

                oos.writeObject(emp);
                oos.flush();

                return baos.toByteArray();
            }

        } catch (Exception ignored) {
        }

        return ("Echo: " + fallbackMessage).getBytes();
    }

    // =====================================================
    // Send response packet
    // =====================================================
    private void sendResponse(
            DatagramSocket socket,
            byte[] data,
            InetAddress address,
            int port
    ) throws IOException {

        DatagramPacket response =
                new DatagramPacket(data, data.length, address, port);

        socket.send(response);

        System.out.println("Replied to client");
    }
}
