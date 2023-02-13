from AndroidInterface import Android

android = Android()
android.connect_android()

while True:
    try:
        msg = input("Enter message to send to Android: ")
        msg = str(msg).encode()
        android.writing_to_android()
#            time.sleep(5)
        android.reading_from_android()

    except KeyboardInterrupt:
        print('Bluetooth Communication Interrupted.')
        android.disconnect_android()
        break   

