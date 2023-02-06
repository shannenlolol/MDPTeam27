import numpy as np
from PIL import Image, ImageDraw, ImageFont 
import config

def bytes_to_np_array(msg):
    np_msg = np.frombuffer(msg, dtype=np.uint8)
    np_msg = np_msg.reshape(config.image_height,config.image_width,3)
    return np_msg

def np_array_to_image(np_msg):
    image = Image.fromarray(np_msg, "RGB")
    return image

def draw_bbox(image,bb_array,label):
    bb_image = ImageDraw.Draw(image)  
    bb_image.rectangle(bb_array, outline ="red")
    font = ImageFont.truetype(config.label_font, config.label_size)
    bb_image.text(bb_array,label, font=font)
    return image

def image_to_np_array(image):
    np_msg = np.asarray(image)
    return np_msg

def np_array_to_bytes(np_msg):
    msg = np_msg.tobytes()
    return msg
