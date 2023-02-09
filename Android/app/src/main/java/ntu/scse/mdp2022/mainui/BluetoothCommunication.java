package ntu.scse.mdp2022.mainui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;


public class BluetoothCommunication {

    private static final String TAG = "BluetoothCommunication";
    //private static Context mmContext;
    private static BluetoothSocket mmSocket;
    private static InputStream inputStream;
    private static OutputStream outPutStream;
    private static BluetoothDevice BTConnectionDevice;

    public static void startCommunication(BluetoothSocket socket, Context mmContext) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputStream = tmpIn;
        outPutStream = tmpOut;

        byte[] buffer = new byte[1024];

        int numbytes;

        while (true) {

            try {
                numbytes = inputStream.read(buffer);
                String incomingMessage = new String(buffer, 0, numbytes);
                Intent incomingMsgIntent = new Intent("IncomingMsg");
                incomingMsgIntent.putExtra("receivingMsg", incomingMessage);
                LocalBroadcastManager.getInstance(mmContext).sendBroadcast(incomingMsgIntent);

            } catch (IOException e) {
                Intent connectionStatusIntent = new Intent("btConnectionStatus");
                connectionStatusIntent.putExtra("ConnectionStatus", "disconnect");
                connectionStatusIntent.putExtra("Device",BTConnectionDevice);
                LocalBroadcastManager.getInstance(mmContext).sendBroadcast(connectionStatusIntent);
                e.printStackTrace();
                break;

            } catch (Exception e){
                e.printStackTrace();

            }

        }
    }

    public static void write(byte[] bytes) {

        String text = new String(bytes, Charset.defaultCharset());

        try {
            outPutStream.write(bytes);
        } catch (IOException e) {

        } catch (NullPointerException e) {

        }
    }

    static void connected(BluetoothSocket mmSocket, BluetoothDevice BTDevice, Context context) {

        BTConnectionDevice = BTDevice;
        Context mmContext = context;
        startCommunication(mmSocket, context);
    }

    public static void writeMsg(byte[] out) {
        write(out);
    }
}
