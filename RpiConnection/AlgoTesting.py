from AlgoInterface import Algo 
import time
import json

ser = Algo()
#    ser.__init__()
ser.connect_ALG()
time.sleep(3)
print('Connection established')
count = 1
while True: 
    try:            
        if(count == 1):
            obstacles = {
                "obstacle1": [15, 185, -90, 0],
                "obstacle2": [65, 125, 90, 1],
                "obstacle3": [105, 75, 0, 2],
                "obstacle4": [155, 165, 180, 3],
                "obstacle5": [185, 95, 180, 4],
                "obstacle6":[135, 25, 0, 5]
            } 
            data = json.dumps(obstacles).encode()
            ser.write_to_ALG(data)
            count+=1
        else:
            writeMsg = "CMPLT"
            ser.write_to_ALG(writeMsg.encode())
        jsonMsg = ser.read_from_ALG()
        msg = json.loads(jsonMsg)
        print(msg)
#            if(msg["OK"] == 1):
#                break 
    except KeyboardInterrupt:
        print('Communication interrupted')
        ser.disconnect_ALG()
        ser.disconnect_all()
        break
