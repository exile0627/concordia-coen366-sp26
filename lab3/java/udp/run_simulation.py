import os
import socket
import subprocess
import sys
import threading
import time

# ============================================================
# Path Configuration
# ============================================================

# Current file directory
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# Workspace root directory (3 levels up)
WORKSPACE_ROOT = os.path.abspath(os.path.join(BASE_DIR, "..", "..", ".."))

# Compiled Java binary output directory
BIN_DIR = os.path.join(BASE_DIR, "_bin")

# Java class entry points
SERVER_CLASS = "lab3.java.udp.udpBaseServer_2"
CLIENT_CLASS = "lab3.java.udp.udpBaseClient_2"

# Java source files to compile
JAVA_FILES = [
    os.path.join(WORKSPACE_ROOT, "lab3", "java", "udp", "model", "Employee.java"),
    os.path.join(WORKSPACE_ROOT, "lab3", "java", "udp", "udpBaseServer_2.java"),
    os.path.join(WORKSPACE_ROOT, "lab3", "java", "udp", "udpBaseClient_2.java"),
]


# ============================================================
# Utility: Stream process output
# ============================================================

def stream_output(process, prefix):
    """
    Read subprocess stdout stream line by line
    and print with prefix label.
    """
    if process.stdout is None:
        return

    for line in process.stdout:
        print(f"{prefix} {line.rstrip()}")


# ============================================================
# Compile Java source code
# ============================================================

def compile_java():
    """
    Compile Java source files into BIN_DIR.
    Equivalent to:
        javac -d _bin *.java
    """

    os.makedirs(BIN_DIR, exist_ok=True)

    cmd = ["javac", "-d", BIN_DIR] + JAVA_FILES

    result = subprocess.run(cmd, capture_output=True, text=True)

    if result.returncode != 0:
        print("[BUILD] javac failed")
        print(result.stdout)
        print(result.stderr)
        raise SystemExit(result.returncode)

    print("[BUILD] javac success")


# ============================================================
# Start server process
# ============================================================

def start_server(env):
    """
    Start Java UDP server process.
    """

    cmd = ["java", "-cp", BIN_DIR, SERVER_CLASS]

    return subprocess.Popen(
        cmd,
        cwd=WORKSPACE_ROOT,
        stdin=subprocess.DEVNULL,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        bufsize=1,
        env=env,
    )


# ============================================================
# Start client process
# ============================================================

def start_client(env):
    """
    Start Java UDP client process.
    """

    cmd = ["java", "-cp", BIN_DIR, CLIENT_CLASS]

    return subprocess.Popen(
        cmd,
        cwd=WORKSPACE_ROOT,
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        bufsize=1,
        env=env,
    )


# ============================================================
# Send commands to client via stdin
# ============================================================

def send_inputs(client_process, steps):
    """
    Simulate user typing into client console.

    steps:
        list of (delay_seconds, message)
    """

    if client_process.stdin is None:
        return

    for delay, message in steps:
        time.sleep(delay)

        try:
            client_process.stdin.write(message + "\n")
            client_process.stdin.flush()
        except BrokenPipeError:
            return


# ============================================================
# Send UDP datagram to server to stop server
# ============================================================

def send_bye_datagram():
    """
    Send termination signal to server.
    """

    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as sock:
        sock.sendto(b"bye", ("127.0.0.1", 1234))


# ============================================================
# Main simulation
# ============================================================

def main():

    # Enable Python unbuffered output
    env = os.environ.copy()
    env["PYTHONUNBUFFERED"] = "1"

    # Compile Java source files
    compile_java()

    # Start server process
    server = start_server(env)

    # Print server output asynchronously
    threading.Thread(
        target=stream_output,
        args=(server, "[SERVER]"),
        daemon=True
    ).start()

    # Wait for server to initialize
    time.sleep(1.0)

    # Start two client processes (simulate multiple clients)
    client1 = start_client(env)
    client2 = start_client(env)

    # Print client outputs
    threading.Thread(
        target=stream_output,
        args=(client1, "[CLIENT 1]"),
        daemon=True
    ).start()

    threading.Thread(
        target=stream_output,
        args=(client2, "[CLIENT 2]"),
        daemon=True
    ).start()

    # ========================================================
    # Simulate multiple clients sending interleaved requests
    # ========================================================
    # This demonstrates:
    #
    # UDP server is blocking.
    # Server processes packets sequentially.
    # But can still serve multiple clients logically.
    #
    # Because:
    # - Each UDP packet contains client IP + port
    # - Server can reply to any client
    #
    # ========================================================

    t1 = threading.Thread(
        target=send_inputs,
        args=(client1, [(0.2, "hello"), (0.8, "employee"), (0.8, "hello")]),
        daemon=True,
    )

    t2 = threading.Thread(
        target=send_inputs,
        args=(client2, [(0.4, "hello"), (0.8, "employee"), (0.8, "hello")]),
        daemon=True,
    )

    t1.start()
    t2.start()

    t1.join()
    t2.join()

    time.sleep(2.0)

    # Cleanup clients
    for client in (client1, client2):
        if client.poll() is None:
            client.terminate()
            try:
                client.wait(timeout=2)
            except subprocess.TimeoutExpired:
                client.kill()

    # Stop server
    send_bye_datagram()

    try:
        server.wait(timeout=5)
    except subprocess.TimeoutExpired:
        server.terminate()

    print("[SIM] done")


if __name__ == "__main__":
    main()
    