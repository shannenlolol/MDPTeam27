
import socket
import time

class socket_communication:
    def __init__(self, host, port, buffer_size):
        self.host = host
        self.port = port
        self.socket = None
        self.client = None
        self.buffer_size = buffer_size

        return
    
    def initiate_connection(self, client, address):
        retry = True
        while retry:
            try: 
                self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                self.socket.connect((client,address))
                self.client = (client,address) 
                print("Successful Connection with {}".format(self.client))
                retry = False
            except Exception as error:
                print("Initalise Connection: Could not connect to {}".format(client))
                retry = True
                time.sleep(1)
        return 

    def accept_connection(self):
        retry = True 
        while retry:
            try:
                listening_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                listening_socket.bind((self.host, self.port))
                listening_socket.listen(5)
                print("Waiting For Connection...")
                if self.client is None:
                    self.socket, self.client = listening_socket.accept()
                    print("Successful Connection with {}".format(self.client))
                else:
                    print("Already Connected To {}".format(self.client))
                retry = False
            except Exception as error:
                print('Connection failed: ')
                retry = True
                time.sleep(1)
        return 
    
    def disconnect(self):
        retry = True
        while retry:
            try: 
                if self.client is not None:
                    print("Disconnecting connection to {}".format(self.client))
                    self.socket.close()
                    self.client = None
                    print("Successfully Disconnected")
                    retry = False
            except Exception as error:
                print("Could not Disconnect")
                retry = True
                time.sleep(1)
    
    def send_message(self, message):
        try:
            if self.client is not None:
                print("Sending message...")
                self.socket.sendall(message)
                print("Message Sent")
            return
        except Exception as error:
            print('Send Failed')
            raise error

    def receive_frame(self):
        recv_txt = b''
        try: 
            recv_txt = self.socket.recv(self.buffer_size)
        except:
            print("Receive Failed")
            pass
        return recv_txt
    
    def receive_message(self):
        msg_txt = b''
        count = 1
        frame_txt = self.receive_frame()
        while len(frame_txt) > 0:
            msg_txt = msg_txt + frame_txt
            if count%5 == 0:
                print("{} frames received".format(count))
            frame_txt = self.receive_frame()
            count += 1
        else:
            return msg_txt
    

    

    


    





    
        


