from socket_communication import socket_communication 
import config
#import picam
import socket
from PIL import Image
import numpy as np

# hostname=socket.gethostname()   
# host = socket.gethostbyname(hostname)  
host = config.socket_rpi_ip
port = config.socket_port_number

s = socket_communication(host, port, config.socket_buffer_size)
s.accept_connection()

new_image = Image.open("images/test.jpg")
new_image_array = np.asarray(new_image)
data = new_image_array.tobytes()
#data = picam.take_picture().tobytes()
s.send_message(data)

s.disconnect()
