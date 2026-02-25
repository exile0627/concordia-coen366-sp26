import subprocess
import time
import os
import sys

# Get current script directory
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# Construct absolute paths
SERVER_SCRIPT = os.path.join(BASE_DIR, "udp_server.py")
CLIENT_SCRIPT = os.path.join(BASE_DIR, "udp_client.py")

def main():

    # Start server
    server_process = subprocess.Popen(
        [sys.executable, "-u", SERVER_SCRIPT]
    )

    time.sleep(1)
    start_at = time.time() + 1.0

    # Start two clients
    clients = []
    for i in range(2):
        p = subprocess.Popen(
            [
                sys.executable,
                CLIENT_SCRIPT,
                "--name",
                f"C{i + 1}",
                "--count",
                "10",
                "--interval",
                "0.02",
                "--start-at",
                str(start_at),
            ]
        )
        clients.append(p)

    for p in clients:
        p.wait()

    server_process.terminate()
    server_process.wait(timeout=2)


if __name__ == "__main__":
    main()
    