import socket
import pickle
import threading
import time

# =====================================================
# Employee Class
# Python objects can be serialized using pickle
# =====================================================
class Employee:
    def __init__(self, id, name):
        self.id = id
        self.name = name

    def __str__(self):
        return f"Name: {self.name}, ID: {self.id}"


# =====================================================
# Server Logic
# =====================================================
def server():
    HOST = "0.0.0.0"
    PORT = 8080
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Bind socket to IP address and port
    # This allows the server to listen on the specified network interface
    server_socket.bind((HOST, PORT))

    # Start listening for client connections
    # listen(1) means:
    # - 1 connection can wait in the queue (backlog queue)
    # - Server is now in passive mode waiting for client requests
    server_socket.listen(1)
    print("[Server] Waiting for connection...")

    # Accept() blocks until a client connects
    # Returns:
    # conn → communication socket dedicated to this client
    # addr → client address information
    conn, addr = server_socket.accept()
    print("[Server] Connected from", addr)

    # =====================================================
    # Deserialization (Byte Stream → Python Object)
    # =====================================================
    # Server receives byte stream from client
    data = conn.recv(4096) # 4KB

    # deserialization: Convert byte stream back to Python object
    employee = pickle.loads(data)
    print("[Server] Received:", employee)

    # =====================================================
    # Serialization (Python Object → Byte Stream)
    # =====================================================
    # Convert Python object into bytes before sending
    # Network transmission can only send byte streams
    conn.sendall(pickle.dumps(employee))

    conn.close()
    server_socket.close()


# =====================================================
# Client Logic
# =====================================================
def client():
    while True:
        try:
            client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            client_socket.connect(("127.0.0.1", 8080))
            break
        except ConnectionRefusedError:
            # Server not ready yet
            time.sleep(0.5)

    print("[Client] Connected to server")

    joe = Employee(123456789, "Joe B.")
    print("[Client] Sending:", joe)

    # =====================================================
    # Serialization (Object → Bytes)
    # =====================================================
    # Before sending data over network,
    # Python object must be converted into byte stream
    client_socket.sendall(pickle.dumps(joe))

    # =====================================================
    # Deserialization (Bytes → Object)
    # =====================================================
    # Receive byte stream from server
    data = client_socket.recv(4096)

    # Convert bytes back to Python object
    response = pickle.loads(data)

    print("[Client] Received:", response)

    client_socket.close()


# =====================================================
# Main
# =====================================================
if __name__ == "__main__":
    # Use threads to simulate client-server communication
    t1 = threading.Thread(target=server)
    t2 = threading.Thread(target=client)

    t1.start()
    t2.start()

    t1.join()
    t2.join()

    print("Program finished")
