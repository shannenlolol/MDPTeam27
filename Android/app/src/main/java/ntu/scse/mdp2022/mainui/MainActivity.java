package ntu.scse.mdp2022.mainui;

import static ntu.scse.mdp2022.mainui.Robot.ROBOT_COMMAND_POS;
import static ntu.scse.mdp2022.mainui.Robot.ROBOT_MOTOR_BACKWARD;
import static ntu.scse.mdp2022.mainui.Robot.ROBOT_MOTOR_FORWARD;
import static ntu.scse.mdp2022.mainui.Robot.ROBOT_MOTOR_STOP;
import static ntu.scse.mdp2022.mainui.Robot.ROBOT_WHEEL_CENTRE;
import static ntu.scse.mdp2022.mainui.Robot.ROBOT_WHEEL_LEFT;
import static ntu.scse.mdp2022.mainui.Robot.ROBOT_WHEEL_RIGHT;
import static ntu.scse.mdp2022.mainui.Robot.STM_COMMAND_CENTRE;
import static ntu.scse.mdp2022.mainui.Robot.STM_COMMAND_STOP;
import static ntu.scse.mdp2022.mainui.Robot.STM_COMMAND_FORWARD;
import static ntu.scse.mdp2022.mainui.Robot.STM_COMMAND_BACKWARD;
import static ntu.scse.mdp2022.mainui.Robot.STM_COMMAND_LEFT;
import static ntu.scse.mdp2022.mainui.Robot.STM_COMMAND_RIGHT;
import static ntu.scse.mdp2022.mainui.Target.BLUETOOTH_TARGET_IDENTIFIER;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;
import ntu.scse.mdp2022.mainui.Chat.Message;
import ntu.scse.mdp2022.mainui.Chat.ChatAdapter;


public class MainActivity extends AppCompatActivity {
    MapCanvas mapCanvas;
    private BoardMap map = new BoardMap();
    Button btnReset;
    ImageButton btnForward;
    ImageButton btnBackward;
    ImageButton btnLeft;
    ImageButton btnRight;
    TextView topTitle;
    TextView txtTimer;
    Button btnTarget;
    Button btnImage;
    Button btnFastest;
    public static final String RPI_COMMAND_READ_OBS = "OBS";

    ArrayList longpress = new ArrayList();

    //Bluetooth Components
    BluetoothAdapter bluetoothAdapter;
    Button btnConnect;

    //Chat Components
    List<Message> chat = new ArrayList<Message>();
    RecyclerView chatboxLv;
    ChatAdapter adapter;
    EditText chatboxinput;
    LinearLayoutManager linearLayoutManager;
    static final int SENT_BY_RPI = 6;
    static final int SENT_BY_RELEASING_BUTTON = 5;
    static final int SENT_BY_HOLDING_BUTTON = 4;
    static final int SENT_BY_MAP = 3;
    static final int SENT_BY_ROBOT = 2;
    static final int SENT_BY_REMOTE = 1;
    PersistentFiles files;
    ChatHandler chatHandler;

    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;
    boolean timerStarted = false;
    boolean secondClick = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        bluetoothAdapter = bluetoothAdapter.getDefaultAdapter();
        mapCanvas = findViewById(R.id.mapCanvas);

        btnForward = (ImageButton) this.findViewById(R.id.btn_forward);
        btnBackward = (ImageButton) this.findViewById(R.id.btn_backward);
        btnLeft = (ImageButton) this.findViewById(R.id.btn_left);
        btnRight = (ImageButton) this.findViewById(R.id.btn_right);
        topTitle = (TextView) this.findViewById(R.id.top_title);
        btnReset = (Button) this.findViewById(R.id.btn_reset);
        //btnTarget = (Button) this.findViewById(R.id.btn_target);
        btnImage = (Button) this.findViewById(R.id.btn_image);
        btnFastest = (Button) this.findViewById(R.id.btn_fastest);
        txtTimer = (TextView) this.findViewById(R.id.text_legend);
        timer = new Timer();
        map = mapCanvas.getFinder();

        //Bluetooth Components
        btnConnect = findViewById(R.id.btnConnect);
        chatboxLv = findViewById(R.id.chatboxlv);
        chatboxinput = findViewById(R.id.chatboxinput);
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("IncomingMsg"));
        initChat();

        mapCanvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                updateRobotStatus();
                return false;
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapCanvas.setSolving(false);
                map.resetBoardMap();
                mapCanvas.invalidate();
                topTitle.setText("");
                if(timerTask != null)
                {
                    timerTask.cancel();
                    time = 0.0;
                    txtTimer.setText("PRESS + HOLD TO ADD TARGET   |   TAP TO ROTATE   |   DRAG TO MOVE   |   REMOVE - DRAG OUT OF GRID");
                }
            }
        });

//        btnTarget.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendChat("RESET", SENT_BY_ROBOT);
//                int n = 0;
//                while (n < map.getTargets().size()) {
//                    String message;
//                    message = "OBS|[" + (n+1) + "," + map.getTargets().get(n).getX() + "," + (21-map.getTargets().get(n).getY()) + "," + map.getTargets().get(n).getPos() + "]";
//                    sendChat(message, SENT_BY_ROBOT);
//                    n++;
//                }
//            }
//        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerStarted==false){
                    if(secondClick==false){
                        secondClick = true;
                        //Toast.makeText(getApplicationContext(), "Sending Obstacles to RPI...", Toast.LENGTH_LONG).show();
                        int n = 0;
                        String msg = "";
                        while (n < map.getTargets().size()) {
                            msg += ("OBS__" + (n + 1) + "," + map.getTargets().get(n).getX() + "," + (21 - map.getTargets().get(n).getY()) + "," + map.getTargets().get(n).getPos() + " \n");
                            n++;
                        }
                        sendChat(msg, SENT_BY_ROBOT);
                        topTitle.setText("IMAGE TASK READY");

                    }else{
                        timerStarted = true;
                        secondClick = false;
                        //topTitle.setText("START");
                        Toast.makeText(getApplicationContext(), "Start Image Recognition...", Toast.LENGTH_LONG).show();
                        sendChat("BANANAS\n",SENT_BY_ROBOT);
                        startTimer();
                    }
                }else{
                    timerStarted = false;
                    timerTask.cancel();
                    time = 0.0;
                    map.getRobot().setX(1);
                    map.getRobot().setY(19);
                    map.getRobot().setPos(Robot.ROBOT_POS_NORTH);
                    map.defaceTargets();
                    topTitle.setText("READY FOR RERUN");
                }
            }
        });

        btnFastest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Fastest Car Task...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, taskTimer.class);
                startActivity(intent);
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                searchDevices(view);
            }
        });

        buttonLongHold(btnForward, ROBOT_MOTOR_FORWARD);
        buttonLongHold(btnBackward, ROBOT_MOTOR_BACKWARD);
        buttonLongHold(btnLeft, ROBOT_WHEEL_LEFT);
        buttonLongHold(btnRight, ROBOT_WHEEL_RIGHT);
    }

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
                        txtTimer.setText(getTimerText());
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

    private void buttonLongHold(View btn, int direction) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longpress.size() < 1) {
                    switch (v.getId()) {
                        case R.id.btn_forward:
                        case R.id.btn_backward:
                            map.getRobot().robotRotate(direction);
                            break;
                        case R.id.btn_right:
                            map.getRobot().robotClockwiseRotate();
                            break;
                        case R.id.btn_left:
                            map.getRobot().robotAnticlockwiseRotate();
                    }
                }
                updateRobotStatus();
                longpress.clear();
            }
        });
        btn.setOnTouchListener(new View.OnTouchListener() {
            private int delay = 250;
            private Handler mHandler;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) {
                            return true;
                        }
                        mHandler = new Handler();
                        switch (view.getId()) {
                            case R.id.btn_forward:
                                sendChat("w010", SENT_BY_ROBOT);
                                break;
                            case R.id.btn_backward:
                                sendChat("s010", SENT_BY_ROBOT);
                                //sendChat((direction == ROBOT_MOTOR_FORWARD ? STM_COMMAND_FORWARD : STM_COMMAND_BACKWARD), SENT_BY_ROBOT);
                                //Log.d("ROBOT TOUCH DOWN", map.getRobot().toString());
                                break;
                            case R.id.btn_left:
                                sendChat("a000", SENT_BY_ROBOT);
                                break;
                            case R.id.btn_right:
                                sendChat("d000", SENT_BY_ROBOT);
                                //sendChat((direction == ROBOT_WHEEL_LEFT ? STM_COMMAND_LEFT : STM_COMMAND_RIGHT), SENT_BY_ROBOT);
                                //Log.d("ROBOT TOUCH DOWN", map.getRobot().toString());
                                break;
                        }
                        mHandler.postDelayed(mAction, delay);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) {
                            return true;
                        }
                        switch (view.getId()) {
                            case R.id.btn_forward:
                            case R.id.btn_backward:
                                //sendChat(STM_COMMAND_STOP, SENT_BY_RELEASING_BUTTON);
                                map.getRobot().setMotor(ROBOT_MOTOR_STOP);
                                //Log.d("ROBOT TOUCH UP", map.getRobot().toString());
                                break;
                            case R.id.btn_left:
                            case R.id.btn_right:
                                //sendChat(STM_COMMAND_CENTRE, SENT_BY_RELEASING_BUTTON);
                                map.getRobot().setWheel(ROBOT_WHEEL_CENTRE);
                                //Log.d("ROBOT TOUCH UP", map.getRobot().toString());
                                break;
                        }
                        //MessageFragment?
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                updateRobotStatus();
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    switch (btn.getId()) {
                        case R.id.btn_forward:
                        case R.id.btn_backward:
                            map.getRobot().robotRotate(direction);
                            //Log.d("ROBOT RUNNABLE", map.getRobot().toString());
                            break;
                        case R.id.btn_left:
                        case R.id.btn_right:
                            map.getRobot().wheelPos(direction);
                            //Log.d("ROBOT RUNNABLE", "TOUCH " + map.getRobot().toString());
                            break;
                    }
                    longpress.add(1);
                    mHandler.postDelayed(this, delay);
                }
            };
        });
    }

    public void updateRobotStatus() {
        topTitle.setText("X: " + map.getRobot().getX() + " Y: " + (20 - map.getRobot().getY()) + "\t\t" + map.getRobot().getPosText());
    }


    //Bluetooth and Chat
    private void initChat() {
        try {
            chat.add(new Message("MDP27", 2));
            adapter = new ChatAdapter(this, chat);
            chatboxLv.setAdapter(adapter);
            linearLayoutManager = new LinearLayoutManager(this);
            chatboxLv.setLayoutManager(linearLayoutManager);
            chatboxLv.scrollToPosition(chat.size() - 1);
            chatHandler = new ChatHandler();
        } catch (Exception e) {
            Log.d("InitChat", e.toString());
        }
    }


    public void searchDevices(View view) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            try {
                Intent intent = new Intent(MainActivity.this, ConnectBT.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                System.out.println(e.getMessage());
            }
        } else {
            String BTnotONtext = "Please switch on bluetooth first";
            sendChat(BTnotONtext, SENT_BY_ROBOT);
        }
    }



    public void sendChat(String msg, int sender) {
        chat.add(new Message(msg, sender));
        adapter.notifyDataSetChanged();
        chatboxLv.smoothScrollToPosition(chat.size() - 1);
        BluetoothCommunication.writeMsg(msg.getBytes(Charset.defaultCharset()));


//        String[] info = msg.replace("<", "").replace(">", "").replace(" ", "").split(",");
//        switch (info[0]) {
//            case BLUETOOTH_TARGET_IDENTIFIER:
//                int targetNo = Integer.parseInt(info[1]);
//                int imageID = Integer.parseInt(info[2]);
//                Target obs = map.getTargets().get(targetNo - 1);
//                obs.setImg(imageID);
//                if(map.hasAllTargets()) timerTask.cancel();
//
//                break;
//            case ROBOT_COMMAND_POS:
//                int posX = Integer.parseInt(info[1]);
//                int posY = 20 - Integer.parseInt(info[2]);
//                int posF = -1;
//                switch (info[3]) {
//                    case "N":
//                        posF = Integer.parseInt("0");
//                        break;
//                    case "E":
//                        posF = Integer.parseInt("1");
//                        break;
//                    case "S":
//                        posF = Integer.parseInt("2");
//                        break;
//                    case "W":
//                        posF = Integer.parseInt("3");
//                        break;
//                }
//                map.getRobot().setX(posX);
//                map.getRobot().setY(posY);
//                map.getRobot().setPos(posF);
//                updateRobotStatus();
//        }
    }


    public void saveData(String name, String text) {
        files.saveData(name, text);
    }

    boolean canUpdateValue1 = false;
    boolean canUpdateValue2 = false;

    //to send text to robot
    public void sendText(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        //Do something from the text input
        String str = chatboxinput.getText().toString();
//
//        String[] info = msg.replace("<", "").replace(">", "").replace(" ", "").split(",");
//        switch (info[0]) {
//            case BLUETOOTH_TARGET_IDENTIFIER:
//                int targetNo = Integer.parseInt(info[1]);
//                int imageID = Integer.parseInt(info[2]);
//                Target obs = map.getTargets().get(targetNo - 1);
//                obs.setImg(imageID);
//
//                break;
//            case ROBOT_COMMAND_POS:
//                int posX = Integer.parseInt(info[1]);
//                int posY = 20 - Integer.parseInt(info[2]);
//                int posF = -1;
//                switch (info[3]) {
//                    case "N":
//                        posF = Integer.parseInt("0");
//                        break;
//                    case "E":
//                        posF = Integer.parseInt("1");
//                        break;
//                    case "S":
//                        posF = Integer.parseInt("2");
//                        break;
//                    case "W":
//                        posF = Integer.parseInt("3");
//                        break;
//                }
//                map.getRobot().setX(posX);
//                map.getRobot().setY(posY);
//                map.getRobot().setPos(posF);
//                updateRobotStatus();}
        chatboxinput.setCursorVisible(false);
        sendChat(str, SENT_BY_ROBOT);
        //BluetoothCommunication.writeMsg(str.getBytes(Charset.defaultCharset()));
//        if (!canUpdateValue1 && !canUpdateValue2) {
//            sendChat(str, SENT_BY_REMOTE);
//            BluetoothCommunication.writeMsg(str.getBytes(Charset.defaultCharset()));
//        }
//        if (canUpdateValue1) {
//            sendChat(str, SENT_BY_REMOTE);
//            saveData("v1", str);
//            sendChat("Value 1 changed to \"" + str + "\"", SENT_BY_ROBOT);
//        }
//        if (canUpdateValue2) {
//            sendChat(str, SENT_BY_REMOTE);
//            saveData("v2", str);
//            sendChat("Value 2 changed to \"" + str + "\"", SENT_BY_ROBOT);
//        }
//        chatboxinput.setText("");
    }

    //to enable cursor once the text input is clicked
    public void enableCursor(View view) {
        chatboxinput.setCursorVisible(true);
    }

    BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("receivingMsg");
            //if (chatHandler.chatIsCommand(msg)) {
//                String[] arrayOfCommand = chatHandler.splitCommand(msg);
//                for (String cmd : arrayOfCommand) {
//                    selectAction(cmd);
//                }
            //TODO:For Commands
            chat.add(new Message(msg, SENT_BY_REMOTE));
            adapter.notifyDataSetChanged();
            chatboxLv.smoothScrollToPosition(chat.size() - 1);

            String[] info = msg.replace("<", "").replace(">", "").replace(" ", "").split(",");
            switch (info[0]) {
                case BLUETOOTH_TARGET_IDENTIFIER:
                    int targetNo = Integer.parseInt(info[1]);
                    int imageID = Integer.parseInt(info[2]);
                    Target obs = map.getTargets().get(targetNo - 1);
                    obs.setImg(imageID);
//                    if(map.hasAllTargets()){
//                        timerTask.cancel();
//                        topTitle.setText("IMAGE TASK DONE");
//                    }
                    break;
                case ROBOT_COMMAND_POS:
                    int posX = Integer.parseInt(info[1]);
                    int posY = 20 - Integer.parseInt(info[2]);
                    int posF = -1;
                    switch (info[3]) {
                        case "N":
                            posF = Integer.parseInt("0");
                            break;
                        case "E":
                            posF = Integer.parseInt("1");
                            break;
                        case "S":
                            posF = Integer.parseInt("2");
                            break;
                        case "W":
                            posF = Integer.parseInt("3");
                            break;
                    }
                    map.getRobot().setX(posX);
                    map.getRobot().setY(posY);
                    map.getRobot().setPos(posF);
                    updateRobotStatus();
                    break;
                case "END" :
                    timerTask.cancel();
                    topTitle.setText("IMAGE TASK DONE");
                    break;
                default:

            }
        }

//    public void receiveMessage(Activity activity, String msg) {
//            map = mapCanvas.getFinder();
//            String[] parts = msg.replace("\n", "").replace("\r", "").split("\\|");
//            switch(parts[0]) {
//                case STM_COMMAND_FORWARD:
//                case STM_COMMAND_BACKWARD:
//                    map.getRobot().robotRotate(msg.equals("f") ? ROBOT_MOTOR_FORWARD : ROBOT_MOTOR_BACKWARD);
//                    sendChat(STM_COMMAND_STOP, SENT_BY_RPI);
//                    map.getRobot().setMotor(ROBOT_MOTOR_STOP);
//                    break;
//                case RPI_COMMAND_READ_OBS:
//                    SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
//                    String obss = "";
//                    for(int i=0; i < parts.length; i++) {
//                        String obs = parts[i].replace("OBS", "").replace("[", "").replace("]", "|");
//                        if (!obs.equals(""))
//                            obss += obs;
//                    }
//                    SharedPreferences.Editor editor = sharedPref.edit();
//                    editor.putString(obss, obss);
//                    editor.apply();
//                    break;
//                case ROBOT_COMMAND_POS:
//                    map.getRobot().setX(Integer.parseInt(parts[1]));
//                    map.getRobot().setY(20-Integer.parseInt(parts[2]));
//                    map.getRobot().setPos(Integer.parseInt(parts[3]));
//                    updateRobotStatus();
//                    break;
//            }
//            messageReceivedTextView.setText(message);
//    }

//    public void selectAction (String command) {
//        try {
//            String[] arr = command.split(":", 2);
//            String commandWhat = arr[0];
//            String commandContent = arr[1];
//            if (commandWhat.equals("status")) {
//                receiveMessageStatus(commandContent);
//            } else if (commandWhat.equals("s")) {
//                receiveMessageObstacle(commandContent, true);
//            } else if(commandWhat.equals("M")) { //it is map descriptor
//                receiveMessageDescriptor(commandContent);
//            } else if(commandWhat.equals("m")){ //it is robot movement
//                receiveMessageMovement(commandContent);
//            }
//        } catch (Exception e) {
//            return;
//        }
//    }
    };}

