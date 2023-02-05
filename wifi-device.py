from socket_communication import socket_communication 
import config
import socket 
from PIL import Image
import numpy as np

hostname = socket.gethostname()   
host = socket.gethostbyname(hostname)  
port = 0

client = config.socket_rpi_ip
address = config.socket_port_number

s = socket_communication(host, port, config.socket_buffer_size)
s.initiate_connection(client, address)


msg = s.receive_message()
np_msg = np.frombuffer(msg, dtype=np.uint8)
np_msg = np_msg.reshape(config.image_height,config.image_width,3)
image = Image.fromarray(np_msg, "RGB")
image.save("images/result.jpg")


s.disconnect()






