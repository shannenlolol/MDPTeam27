from STMInterface import STM

stm = STM()
#    ser.__init__()
stm.connect_STM()
while True:
    try:
        msg = input("Enter message to send to STM: ")
        msg = str(msg).encode()
        stm.write_to_STM(msg)
#            time.sleep(5)
        stm.read_from_STM()
    except KeyboardInterrupt:
        print('Serial Communication Interrupted.')
        stm.disconnect_STM()
        break   
