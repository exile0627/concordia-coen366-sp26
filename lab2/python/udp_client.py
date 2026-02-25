import socket
import sys
import time
import argparse


parser = argparse.ArgumentParser()
parser.add_argument("--name", default="C")
parser.add_argument("--count", type=int, default=10)
parser.add_argument("--interval", type=float, default=0.1)
parser.add_argument("--start-at", type=float, default=0.0)
args = parser.parse_args()

# Create a UDP socket (IPv4, UDP)
try:
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
except socket.error as e:
    print("Failed to create socket:", e)
    sys.exit()

# Server address and port
host = 'localhost'
port = 8888

try:
    if args.start_at > 0:
        while True:
            remaining = args.start_at - time.time()
            if remaining <= 0:
                break
            time.sleep(min(remaining, 0.01))

    for i in range(1, args.count + 1):
        msg = f"{args.name} Message {i}"
        print("Sending:", msg)
        s.sendto(msg.encode('utf-8'), (host, port))
        time.sleep(args.interval)

except socket.error as e:
    print("Error:", e)

finally:
    print("Closing socket.")
    s.close()
