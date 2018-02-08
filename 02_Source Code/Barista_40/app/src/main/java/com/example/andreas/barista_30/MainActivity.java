package com.example.andreas.barista_30;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Andreas on 06.12.2017.
 */

public class MainActivity extends AppCompatActivity {

    //declaration of variables for BT-dialog
    //<editor-fold desc="Dialog declarations">
    private AlertDialog dialog;

    private List<BluetoothDevice> pairedList = new ArrayList();
    private List<BluetoothDevice>discoveredList = new ArrayList();
    private List<String> pairedListStrings = new ArrayList();
    private List<String> discoveredListStrings = new ArrayList();
    private ListView pairedListView;

    private ArrayAdapter pairedListViewAdapter;
    private ArrayAdapter discoveredListViewAdapter;

    private ProgressBar progressBar;
    //</editor-fold>

    //<editor-fold desc="Bluetooth declarations">
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Thread workerThread;
    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;
    //</editor-fold>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup Fragments
        setupViewPager();

        // BT Dialog
        bluetoothDialog();
    }

    private void setupViewPager() {
        ViewPager viewPager = findViewById(R.id.container);
        TabLayout tablayout = findViewById(R.id.tabs);

        //add fragments to adapter
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "Help");
        adapter.addFragment(new Tab2Fragment(), "Control");
        adapter.addFragment(new Tab3Fragment(), "DrinkLexikon");
        //apply adapter on viewpager
        viewPager.setAdapter(adapter);
        //Tab2Fragment as start fragment
        viewPager.setCurrentItem(1, true);
        //setup TabLayout with viewPager
        tablayout.setupWithViewPager(viewPager);
    }

    //create BT Dialog
    protected void bluetoothDialog() {
        //Builder for Dialog
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setCancelable(false);
        //Views initialisation
        final View mView = getLayoutInflater().inflate(R.layout.bluetooth_dialog, null);
        ImageView imgLoad = mView.findViewById(R.id.imageLoad);
        final ImageView imgClear = mView.findViewById(R.id.imageClear);
        progressBar = mView.findViewById(R.id.progressBar);
        pairedListView = mView.findViewById(R.id.listView);
        final ListView listView2 = mView.findViewById(R.id.listView2);

        //enable BT Hardware
        enableBTHardware();
        //list already paired devices
        pairedDevices();


        //OnClickListener for ListViews and refreshButton
        //<editor-fold desc="setOnclickListeners">
        imgLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load icon visible
                progressBar.setVisibility(View.VISIBLE);
                //discover BT devices
                discoverBT(listView2);
            }
        });

        // exit BT dialog
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btCancel();
            }
        });

        //paired Devices onItemClickListener
        pairedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    mmDevice = pairedList.get(i);
                    openBT();
                } catch (Exception e) { //connecting failed
                    Log.w("APP", e);
                }
            }
        });
        //discovered Devices onItemClickListener
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    mmDevice = discoveredList.get(i);
                    openBT();
                } catch (Exception e) { //connecting failed
                    Log.w("APP", e);
                }
            }
        });
        //</editor-fold>

        //initialise dialog and show
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        //discover devices
        discoverBT(listView2);
    }

    //enables BT hardware
    private void enableBTHardware() {
        //check if phone has BT
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available on this device!", Toast.LENGTH_SHORT).show();
        }
        //turn on BT
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 3);
        }
    }

    //list paired BT devices
    private void pairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        //adds devices and their names in lists
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedListStrings.add(device.getName() + "\n");
                pairedList.add(device);
                //HMSoft as standard device as probably wanted
                if (device.getName().equals("HMSoft")) {
                    mmDevice = device;
                    break;
                }
            }
        }
        //show paired devices in lists
        pairedListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pairedListStrings);
        pairedListView.setAdapter(pairedListViewAdapter);

    }

    //close BT dialog
    private void btCancel() {
        Switch bluetoothSwitchButton = findViewById(R.id.btSwitch);
        TextView switchItemText = findViewById(R.id.btSwitchItem);
        bluetoothSwitchButton.setChecked(false);
        switchItemText.setText(R.string.bt_disabled);
        dialog.dismiss();
        pairedList.clear();
        discoveredList.clear();
        pairedListStrings.clear();
        discoveredListStrings.clear();
        mBluetoothAdapter.cancelDiscovery();
    }



    //BT discovery
    private void discoverBT(ListView listView) {
        //clear old discovery results
        discoveredList.clear();
        discoveredListStrings.clear();

        //start discovery
        mBluetoothAdapter.startDiscovery();

        IntentFilter intentFilter = new IntentFilter((BluetoothDevice.ACTION_FOUND));
        //register BroadcastReceiver
        registerReceiver(myReceiver, intentFilter);
        //discovered Devices list show
        discoveredListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, discoveredListStrings);
        listView.setAdapter(discoveredListViewAdapter);
    }

    //BroadcastReceiver to react to found devices
    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //if BT device found
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //save device and its name
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discoveredListStrings.add(device.getName());
                discoveredList.add(device);
                //crashfix
                discoveredListStrings.removeAll(Collections.singleton(null));
                //listView refresh
                discoveredListViewAdapter.notifyDataSetChanged();
                //load icon invisible
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    };

    //send string to Arduino
    protected void sendData(String msg) throws IOException {
        Toast.makeText(this, "Sending data: " + msg, Toast.LENGTH_SHORT).show();
        mmOutputStream.write(msg.getBytes());
    }

    private void openBT() throws IOException {
        //create socket
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        //close BT dialog
        dialog.cancel();
        //create inputstream listener thread
        beginListenForData();
    }

    //create inputstream listener thread
    private void beginListenForData() {
        final Handler handler = new Handler();
        //ASCII newline
        final byte newline = 10;

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        //create thread
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        //if data was sent
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            //read bytes from inputstream
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                //stop read at newline char
                                if (b == newline) {
                                    //convert buffer to String
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    //readBUferPosition reset
                                    readBufferPosition = 0;
                                    //execute what happens with received data
                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Returned String: " + data, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                //otherwise next byte
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });
        //thread start
        workerThread.start();
    }

    //Result handling from speechRecognition and enableBTHardware()
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //enableBTHardware
        if (requestCode == 3) {
            if (resultCode == (0)) {
                btCancel();
            } else {
                pairedDevices();
            }
        }
        //speechRecognition
        super.onActivityResult(requestCode, resultCode, data);

    }

    //close socket
    private void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
    }
}