#!/usr/bin
import socket
import os
from bluetooth import *

UUID = "f274ed34-6fb5-4c69-88f1-6b643378fda5"
ANDROID_SOCKET_BUFFER_SIZE = 2048
PORT_NUM = PORT_ANY


class Android:
    # Initialisation 
    def __init__(self):
        self.server_sock = None
        self.client_sock = None

        os.system("sudo hciconfig hci0 piscan")
        self.server_sock = BluetoothSocket(RFCOMM)
        self.server_sock.bind(("", PORT_NUM))
        self.server_sock.listen(PORT_NUM)
        port = self.server_sock.getsockname()[1]

        advertise_service( self.server_sock, "MDP-Team27",
        service_id = UUID,
        service_classes = [UUID, SERIAL_PORT_CLASS],
        profiles = [SERIAL_PORT_PROFILE],
        # protocols = [OBEX_UUID]
        )

        print("Waiting for connection on RFCOMM channel %d" % port)



#  to do: add in while loop for the connection

    # Connection to Android
    def connect_android(self):
        try: 
            # if self.client_sock is None:
            self.client_sock, client_info = self.server_sock.accept()
            print("Accepted Android connection from ", client_info, UUID)

        except Exception as e:
            print("Connection failed %s" % str(e))

            if self.client_sock is not None:
                self.client_sock.close()
                self.client_sock = None



    # Reading from Android
    def reading_from_android(self):
        try:
            msg = self.client_sock.recv(ANDROID_SOCKET_BUFFER_SIZE).strip()
            print("Message from Android:", msg)
            if msg is None:
                print(msg)
#               return None
            if len(msg) > 0:
                print(len(msg))
#               return msg
    #       return None

        except BluetoothError as e:
            print("Failed to read from Android %s" % str(e))


    # Writing to Android, message will be from image rec
    def writing_to_android(self):
        try:
            self.client_sock.send(msg)
            print("Succesfully wrote to Android", msg)

        except BluetoothError as e:
            print("Failed to write to Android %s" % str(e))



    # Disconnection
    def disconnect_android(self):
        try: 
            if self.client_sock is not None:
                self.client_sock.close()
                self.client_sock = None
                print("Client socket disconnected")
            if self.server_sock is not None:
                self.server_sock.close()
                self.server_sock = None
                print("Server socket disconnected")

        except Exception as e:
            print("Failed to disconnect from Android %s" % str(e))