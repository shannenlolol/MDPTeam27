package ntu.scse.mdp2022.mainui;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.UUID;

public class BTConnectionService extends IntentService {

    private static final String TAG = "BTConnectionService";
    private static final String appName = "MDP Group 27";

    //UUID
    private static final UUID mdpUUID = UUID.fromString("00000000-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;

    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    public  BluetoothDevice myDevice;
    private UUID deviceUUID;

    Context context;


    public BTConnectionService() {

        super("BluetoothConnectionService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        context = getApplicationContext();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (intent.getStringExtra("serviceType").equals("listen")) {

            myDevice = (BluetoothDevice) intent.getExtras().getParcelable("device");

            Log.d(TAG, "Service Handle: startAcceptThread");

            startAcceptThread();
        } else {
            myDevice = (BluetoothDevice) intent.getExtras().getParcelable("device");
            deviceUUID = (UUID) intent.getSerializableExtra("id");

            Log.d(TAG, "Service Handle: startClientThread");

            startClientThread(myDevice, deviceUUID);
        }

    }

    private class AcceptThread extends Thread {

        //Local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, mdpUUID);
                Log.d(TAG, "AcceptThread: Setting up server using: " + mdpUUID);

            } catch (IOException e) {

            }

            mmServerSocket = tmp;
        }

        public void run() {

            Log.d(TAG, "AcceptThread: Running");

            BluetoothSocket socket = null;
            Intent connectionStatusIntent;

            try {

                Log.d(TAG, " Server socket start....");

                socket = mmServerSocket.accept();

                connectionStatusIntent = new Intent("btConnectionStatus");
                connectionStatusIntent.putExtra("ConnectionStatus", "connect");
                connectionStatusIntent.putExtra("Device", ConnectBT.getBluetoothDevice());
                LocalBroadcastManager.getInstance(context).sendBroadcast(connectionStatusIntent);
                ntu.scse.mdp2022.mainui.BluetoothCommunication.connected(socket, myDevice, context);


            } catch (IOException e) {

                connectionStatusIntent = new Intent("btConnectionStatus");
                connectionStatusIntent.putExtra("ConnectionStatus", "connectionFail");
                connectionStatusIntent.putExtra("Device",  ConnectBT.getBluetoothDevice());
            }


            Log.d(TAG, "Ended AcceptThread");

        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.d(TAG, "Cancel: Closing AcceptThread Failed. " + e.getMessage());
            }
        }


    }

    private class ConnectThread extends Thread {

        private BluetoothSocket mySocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {

            Log.d(TAG, "ConnectThread: started");
            myDevice = device;
            deviceUUID = uuid;
        }

        public void run() {
            BluetoothSocket tmp = null;
            Intent connectionStatusIntent;
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRFcommSocket using UUID: " + mdpUUID);
                tmp = myDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {

                Log.d(TAG, "ConnectThread: Could not create InsecureRFcommSocket " + e.getMessage());
            }

            mySocket = tmp;
            bluetoothAdapter.cancelDiscovery();

            try {

                Log.d(TAG, "Connecting to Device: " + myDevice);
                mySocket.connect();


                //BROADCAST CONNECTION MSG
                connectionStatusIntent = new Intent("btConnectionStatus");
                connectionStatusIntent.putExtra("ConnectionStatus", "connect");
                connectionStatusIntent.putExtra("Device", myDevice);
                LocalBroadcastManager.getInstance(context).sendBroadcast(connectionStatusIntent);

                Log.d(TAG, "run: ConnectThread connected");
                ntu.scse.mdp2022.mainui.BluetoothCommunication.connected(mySocket, myDevice, context);
                if (acceptThread != null) {
                    acceptThread.cancel();
                    acceptThread = null;
                }

            } catch (IOException e) {

                try {
                    mySocket.close();

                    connectionStatusIntent = new Intent("btConnectionStatus");
                    connectionStatusIntent.putExtra("ConnectionStatus", "connectionFail");
                    connectionStatusIntent.putExtra("Device", myDevice);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(connectionStatusIntent);
                    Log.d(TAG, "run: Socket Closed: Connection Failed!! " + e.getMessage());

                } catch (IOException e1) {
                    Log.d(TAG, "connectThread, run: Unable to close socket connection: " + e1.getMessage());
                }

            }

        }

        public void cancel() {

            try {
                Log.d(TAG, "Cancel: Closing Client Socket");
                mySocket.close();
            } catch (IOException e) {
                Log.d(TAG, "Cancel: Closing mySocket in ConnectThread Failed " + e.getMessage());
            }
        }
    }

    public synchronized void startAcceptThread() {

        Log.d(TAG, "start");

        //Cancel any thread attempting to make a connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    public void startClientThread(BluetoothDevice device, UUID uuid) {

        Log.d(TAG, "startClient: Started");

        connectThread = new ConnectThread(device, uuid);
        connectThread.start();

    }


}
