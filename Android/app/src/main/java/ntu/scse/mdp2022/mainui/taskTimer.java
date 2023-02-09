package ntu.scse.mdp2022.mainui;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

public class taskTimer extends AppCompatActivity {
    TextView timerText;
    Button stopStartButton;
    BluetoothAdapter bluetoothAdapter;
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;

    boolean timerStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bluetoothAdapter = bluetoothAdapter.getDefaultAdapter();
        setContentView(R.layout.task_timer);
        timerText = (TextView) findViewById(R.id.timerText);
        stopStartButton = (Button) findViewById(R.id.startStopButton);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("IncomingMsg"));
        timer = new Timer();
    }

    public void resetTapped(View view)
    {
        if(timerTask != null)
        {
            timerTask.cancel();
            time = 0.0;
            timerStarted = false;
            stopStartButton.setText("START");
            timerText.setText("00 : 00");
        }
    }

    public void startStopTapped(View view)
    {
        if(timerStarted == false)
        {
            timerStarted = true;
            String msg = "Fastest";
            BluetoothCommunication.writeMsg(msg.getBytes(Charset.defaultCharset()));
            stopStartButton.setText("STOP");
            startTimer();
        }
        else
        {
            timerStarted = false;
            stopStartButton.setText("START");
            timerTask.cancel();
        }
    }

    BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("receivingMsg");
            timerStarted = false;
            stopStartButton.setText("START");
            timerTask.cancel();
        }
    };

    private void startTimer()
    {
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        time++;
                        timerText.setText(getTimerText());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }


    private String getTimerText()
    {
        int rounded = (int) Math.round(time);
        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;

        return String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }

}
