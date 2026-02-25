import socket
import sys

# HOST = '0.0.0.0' means the server listens on all available network interfaces
HOST = '0.0.0.0'

# PORT is an arbitrary non-privileged port number
PORT = 8888

# Create a UDP socket using IPv4 (AF_INET) and UDP (SOCK_DGRAM)
try:
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    print("Socket created", flush=True)
except socket.error as msg:
    print("Failed to create socket. Error:", msg, flush=True)
    sys.exit()

# Bind the socket to the specified IP address and port number
# This allows the server to receive data sent to (HOST, PORT)
try:
    s.bind((HOST, PORT))
except socket.error as msg:
    print("Bind failed. Error:", msg, flush=True)
    sys.exit()

print("Socket bind complete", flush=True)

# Server runs indefinitely, waiting for incoming UDP datagrams
while True:

    # Receive up to 1024 bytes of data from a client
    # recvfrom() returns both the data and the client address
    data, addr = s.recvfrom(1024)

    # If data is empty, terminate the loop
    if not data:
        break

    # Decode received bytes into a string
    message = data.decode('utf-8')

    # Print received message and client information
    print(f"Message[{addr[0]}:{addr[1]}] - {message.strip()}", flush=True)

# Close the socket (usually unreachable in an infinite loop)
s.close()
