import socket
import sys

# Create a TCP socket using IPv4
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Define server address (localhost, port 10000)
server_address = ('localhost', 10000)

print('Starting up on %s port %s' % server_address, file=sys.stderr)

# Bind the socket to the address and port
sock.bind(server_address)

# Put the socket into listening mode
# The argument (1) is the backlog size:
# It allows at most 1 pending connection in the queue
sock.listen(1)

# ============================================================
# IMPORTANT:
# This is a SINGLE-THREADED server.
# It handles ONE client connection at a time.
# It does NOT support multiple clients simultaneously.
# ============================================================

while True:
    # Wait (block) until a client connects
    print('Waiting for a connection...', file=sys.stderr)
    connection, client_address = sock.accept()

    try:
        print('Connection from', client_address, file=sys.stderr)

        # Receive data in small chunks (16 bytes at a time)
        # and echo it back to the client
        while True:
            data = connection.recv(16)

            print('Received "%s"' % data, file=sys.stderr)

            if data:
                # Echo the received data back to the client
                print('Sending data back to the client', file=sys.stderr)
                connection.sendall(data)
            else:
                # No more data means client closed connection
                print('No more data from', client_address, file=sys.stderr)
                break

    finally:
        # Close this client connection
        # After closing, server goes back to accept() for next client
        connection.close()
        