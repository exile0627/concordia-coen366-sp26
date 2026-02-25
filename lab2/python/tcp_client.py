import socket
import sys

# ============================================================
# Create a TCP/IP socket using IPv4 and TCP protocol
# ============================================================
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Bind the client socket to a specific local address and port
# This is optional for clients.
# If not specified, the OS will assign an ephemeral port automatically.
sock.bind(('localhost', 10010))

# Define the server address and port to connect to
server_address = ('localhost', 10000)

print('Connecting to %s port %s' % server_address, file=sys.stderr)

# Establish a TCP connection to the server
# This performs the TCP three-way handshake
sock.connect(server_address)

try:
    # ========================================================
    # Send data to the server
    # ========================================================

    message = 'This is the message. It will be repeated.'
    print('Sending "%s"' % message, file=sys.stderr)

    # TCP requires bytes, so we encode the string
    sock.sendall(message.encode('utf-8'))

    # ========================================================
    # Receive the response from the server
    # ========================================================

    amount_received = 0
    amount_expected = len(message)

    # Keep receiving data until we get back all expected bytes
    # TCP is a stream protocol, so data may arrive in chunks
    while amount_received < amount_expected:
        data = sock.recv(16)  # Receive up to 16 bytes
        amount_received += len(data)

        print('Received "%s"' % data.decode('utf-8'), file=sys.stderr)

finally:
    # ========================================================
    # Close the socket to release system resources
    # ========================================================
    print('Closing socket', file=sys.stderr)
    sock.close()
    