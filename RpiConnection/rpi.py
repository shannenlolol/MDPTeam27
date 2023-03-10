from RpiConnection.socket_communication import socket_communication 
import config
#import picam
import image_handling
from PIL import Image


# hostname=socket.gethostname()   
# host = socket.gethostbyname(hostname)  
host = config.socket_rpi_ip
port = config.socket_port_number

s = socket_communication(host, port, config.socket_buffer_size)
s.accept_connection()

image = Image.open("images/test_1.jpg")
msg = image_handling.np_array_to_bytes(image_handling.image_to_np_array(image))

#msg = image_handling.np_array_to_bytes(picam.take_picture())

s.send_message(msg)

result_msg = s.receive_message()

image = image_handling.np_array_to_image(image_handling.bytes_to_np_array(result_msg))

image.save("images/result.jpg")

s.disconnect()
