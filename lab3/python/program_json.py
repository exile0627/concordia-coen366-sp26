import socket
import json
import threading
import time


# =====================================================
# Employee Class
# Demonstrates serialization and deserialization design
# =====================================================
class Employee:
    def __init__(self, id, name):
        self.id = id
        self.name = name

    # -------------------------------------------------
    # Convert object → JSON serializable dictionary
    # Serialization happens here
    # -------------------------------------------------
    def to_json(self):
        return {
            "id": self.id,
            "name": self.name
        }

    # -------------------------------------------------
    # Convert JSON data → Employee object
    # Deserialization happens here
    # This is a class method because we are creating
    # a new object instance from raw data
    # -------------------------------------------------
    @classmethod
    def from_json(cls, data):
        return cls(
            data["id"],
            data["name"]
        )

    def __str__(self):
        return f"Name: {self.name}, ID: {self.id}"


# =====================================================
# Server Logic
# =====================================================
def server():
    HOST = "0.0.0.0"
    PORT = 8080

    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # Bind socket to network interface + port
    server_socket.bind((HOST, PORT))

    # Start listening (backlog = 1 connection queue)
    server_socket.listen(1)

    print("[Server] Waiting for connection...")

    # Block until client connects
    conn, addr = server_socket.accept()
    print("[Server] Connected from", addr)

    # -------------------------------------------------
    # Receive JSON byte stream from client
    # -------------------------------------------------
    data = conn.recv(4096)

    # Convert bytes → string → JSON → Python dictionary
    json_data = json.loads(data.decode("utf-8"))

    # Convert JSON dictionary → Employee object
    employee = Employee.from_json(json_data)

    print("[Server] Received:", employee)

    # -------------------------------------------------
    # Serialize object → JSON → bytes → send back
    # -------------------------------------------------
    response_bytes = json.dumps(employee.to_json()).encode("utf-8")
    conn.sendall(response_bytes)

    conn.close()
    server_socket.close()


# =====================================================
# Client Logic
# =====================================================
def client():
    # Retry connection until server is ready
    while True:
        try:
            client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            client_socket.connect(("127.0.0.1", 8080))
            break
        except ConnectionRefusedError:
            time.sleep(0.5)

    print("[Client] Connected to server")

    # Create object
    joe = Employee(123456789, "Joe B.")
    print("[Client] Sending:", joe)

    # -------------------------------------------------
    # Serialize object → JSON → bytes → send to server
    # -------------------------------------------------
    client_socket.sendall(
        json.dumps(joe.to_json()).encode("utf-8")
    )

    # -------------------------------------------------
    # Receive response → bytes → JSON → object
    # -------------------------------------------------
    data = client_socket.recv(4096)
    response_json = json.loads(data.decode("utf-8"))
    response = Employee.from_json(response_json)

    print("[Client] Received:", response)

    client_socket.close()


# =====================================================
# Main
# =====================================================
if __name__ == "__main__":
    t1 = threading.Thread(target=server)
    t2 = threading.Thread(target=client)

    t1.start()
    t2.start()

    t1.join()
    t2.join()

    print("Program finished")
    