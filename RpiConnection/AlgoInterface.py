import socket
import time
import pickle
import json

WIFI_IP = "192.168.13.1"
WIFI_PORT = 3004
ALGORITHM_SOCKET_BUFFER_SIZE = 1024
from colorama import *

init(autoreset=True)


class Algo:
    def __init__(self, ip=WIFI_IP, port=WIFI_PORT):
        self.ip = ip
        self.port = port
        print(f"Listening at IP: {self.ip}")
        self.connect = None
        self.client = None
        self.address = None

        self.connect = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        self.connect.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.connect.bind((self.ip, self.port))
        self.connect.listen(1)

    def connect_ALG(self):
        while True:
            retry = False

            try:
                print(Fore.LIGHTYELLOW_EX + '[ALG-CONN] Listening for ALG connections...')

                if self.client is None:
                    self.client, self.address = self.connect.accept()
                    print(Fore.LIGHTGREEN_EX + '[ALG-CONN] Successfully connected with ALG: %s' % str(self.address))
                    retry = False

            except Exception as e:
                print(Fore.RED + '[ALG-CONN ERROR] %s' % str(e))

                if self.client is not None:
                    self.client.close()
                    self.client = None
                retry = True

            if not retry:
                break

            print(Fore.LIGHTYELLOW_EX + '[ALG-CONN] Retrying connection with ALG...')
            time.sleep(1)

    def disconnect_all(self):
        try:
            if self.connect is not None:
                self.connect.shutdown(socket.SHUT_RDWR)
                self.connect.close()
                self.connect = None
                print(Fore.LIGHTWHITE_EX + '[ALG-DCONN] Disconnecting Server Socket')

            if self.client is not None:
                self.client.shutdown(socket.SHUT_RDWR)
                self.client.close()
                self.client = None
                print(Fore.LIGHTWHITE_EX + '[ALG-DCONN] Disconnecting Client Socket')

        except Exception as e:
            print(Fore.RED + '[ALG-DCONN ERROR] %s' % str(e))

    def disconnect_ALG(self):
        try:
            if self.client is not None:
                self.client.shutdown(socket.SHUT_RDWR)
                self.client.close()
                self.client = None
                print(Fore.LIGHTWHITE_EX + '[ALG-DCONN] Disconnecting Client Socket')

        except Exception as e:
            print(Fore.RED + '[ALG-DCONN ERROR] %s' % str(e))

    def read_from_ALG(self):
        try:
            msg = self.client.recv(ALGORITHM_SOCKET_BUFFER_SIZE)
            data = msg.decode()

            if len(data) > 0:
                return data

            return None

        except Exception as e:
            print(Fore.RED + '[ALG-READ ERROR] %s' % str(e))
            raise e

    def write_to_ALG(self, message):
        try:
            self.client.send(message)

        except Exception as e:
            print(Fore.RED + '[ALG-WRITE ERROR] %s' % str(e))
            raise e