import config 
from picamera import PiCamera
import time
import numpy as np

def take_picture():
        camera = PiCamera()
        camera.resolution = (config.image_height, config.image_width)
        camera.framerate = 80
        time.sleep(1)
        output = np.empty((640, 640, 3), dtype=np.uint8)
        camera.capture(output, 'rgb')
        return output

