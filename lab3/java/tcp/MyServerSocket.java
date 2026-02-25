package lab3.java.tcp;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;

import lab3.java.model.Employee;

/**
 * Non-blocking TCP multi-client server using polling model.
 *
 * Design idea:
 * ------------------------------
 * 1. ServerSocketChannel works in non-blocking mode
 * 2. Server continuously:
 *      - Accepts new connections
 *      - Polls existing connections for data
 * 3. Each client has a state machine:
 *      - WAIT_HELLO
 *      - WAIT_OBJECT
 *      - DONE
 */
public class MyServerSocket {

    private static final int SLEEP_INTERVAL_MS = 100;

    private ServerSocketChannel serverChannel;

    private final List<SocketChannel> clients = new ArrayList<>();
    private final List<ObjectInputStream> inputs = new ArrayList<>();
    private final List<ObjectOutputStream> outputs = new ArrayList<>();

    // Client protocol state
    private final List<String> states = new ArrayList<>();

    // ============================================================
    // Constructor
    // ============================================================

    public MyServerSocket(String ipAddress, int port) throws Exception {

        serverChannel = ServerSocketChannel.open();

        InetAddress bindAddress =
                (ipAddress != null && !ipAddress.isEmpty())
                        ? InetAddress.getByName(ipAddress)
                        : InetAddress.getLocalHost();

        serverChannel.bind(new InetSocketAddress(bindAddress, port));

        // Enable non-blocking mode
        serverChannel.configureBlocking(false);

        System.out.println("TCP Non-blocking Server started on " +
                serverChannel.socket().getInetAddress().getHostAddress()
                + ":" + serverChannel.socket().getLocalPort());
    }

    // ============================================================
    // Main server loop
    // ============================================================

    public void listen() throws Exception {

        while (true) {

            acceptNewClients();
            pollClients();

            // Prevent CPU busy looping
            Thread.sleep(SLEEP_INTERVAL_MS);
        }
    }

    // ============================================================
    // Accept new client connections (non-blocking)
    // ============================================================

    private void acceptNewClients() throws IOException {

        SocketChannel clientChannel = serverChannel.accept();

        if (clientChannel == null) return;

        System.out.println("Accepted client: " + clientChannel.getRemoteAddress());

        clientChannel.configureBlocking(false);

        clients.add(clientChannel);

        // Create object streams for serialization communication
        inputs.add(new ObjectInputStream(clientChannel.socket().getInputStream()));
        outputs.add(new ObjectOutputStream(clientChannel.socket().getOutputStream()));

        // Initial protocol state
        states.add("hello");
    }

    // ============================================================
    // Poll all connected clients
    // ============================================================

    private void pollClients() {

        for (int i = 0; i < clients.size(); i++) {

            SocketChannel channel = clients.get(i);

            try {

                if (!channel.isOpen()) continue;

                // Warning:
                // TCP is stream-based.
                // available() is not 100% reliable but acceptable for demo purpose.
                if (channel.socket().getInputStream().available() > 0) {

                    handleClientMessage(i);

                }

            } catch (EOFException e) {

                // Client disconnected
                try {
                    cleanupClient(i);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                i--;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ============================================================
    // Handle client protocol logic
    // ============================================================

    private void handleClientMessage(int index) throws Exception {

        ObjectInputStream ois = inputs.get(index);
        ObjectOutputStream oos = outputs.get(index);

        String state = states.get(index);

        if ("hello".equals(state)) {

            String msg = (String) ois.readObject();
            System.out.println("Received: " + msg);

            if ("hello".equalsIgnoreCase(msg)) {
                oos.writeObject("send");
                oos.flush();

                states.set(index, "object");
            }

        } else if ("object".equals(state)) {

            Object obj = ois.readObject();

            if (obj instanceof Employee emp) {

                System.out.println("Received Employee: " + emp);

                // Echo back object
                oos.writeObject(emp);
                oos.flush();

                states.set(index, "done");
            }
        }
    }

    // ============================================================
    // Cleanup disconnected client
    // ============================================================

    private void cleanupClient(int index) throws IOException {

        SocketChannel channel = clients.get(index);

        System.out.println("Client disconnected: "
                + channel.getRemoteAddress());

        channel.close();

        clients.remove(index);
        inputs.remove(index);
        outputs.remove(index);
        states.remove(index);
    }

    // ============================================================
    // Entry
    // ============================================================

    public static void main(String[] args) throws Exception {
        MyServerSocket app = new MyServerSocket("0.0.0.0", 8080);
        app.listen();
    }
}
