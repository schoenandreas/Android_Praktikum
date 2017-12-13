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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    private SectionsPageAdapter mSectionsPageAdapter;

    //<editor-fold desc="Dialog declarations">
    private ViewPager mViewPager;

    private List pairedList = new ArrayList();
    private List discoveredList = new ArrayList();
    private List pairedListStrings = new ArrayList();
    private List discoveredListStrings = new ArrayList();

    private ArrayAdapter pairedListViewAdapter;
    private ArrayAdapter discoveredListViewAdapter;
    //</editor-fold>

    //<editor-fold desc="Bluetooth declarations">
   // TextView myLabel;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    //</editor-fold>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tablayout = (TabLayout) findViewById(R.id.tabs);
        tablayout.setupWithViewPager(mViewPager);

        bluetoothDialog();



    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "Help");
        adapter.addFragment(new Tab2Fragment(), "SpeechRecognition");
        adapter.addFragment(new Tab3Fragment(), "DrinkLexikon");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void bluetoothDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setCancelable(false);
        View mView = getLayoutInflater().inflate(R.layout.bluetooth_dialog,null);
        Button scanButton = mView.findViewById(R.id.scanButton);
        final ListView listView = mView.findViewById(R.id.listView);
        final ListView listView2 = mView.findViewById(R.id.listView2);
        findBT();

        pairedListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pairedListStrings);
        listView.setAdapter(pairedListViewAdapter);

        //<editor-fold desc="setOnclickListeners">
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  discoverBT(listView2);

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    mmDevice = (BluetoothDevice) pairedList.get(i);
                    openBT();
                }catch(Exception e){
                    Log.w("APP",e);
                }
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    mmDevice = (BluetoothDevice) discoveredList.get(i);
                    openBT();
                }catch(Exception e){
                    Log.w("APP",e);
                }
            }
        });
        //</editor-fold>


        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
        discoverBT(listView2);

    }

    void discoverBT(ListView listView){


        discoveredList.clear();
        discoveredListStrings.clear();

        mBluetoothAdapter.startDiscovery();

        IntentFilter intentFilter = new IntentFilter((BluetoothDevice.ACTION_FOUND));
        registerReceiver(myReceiver,intentFilter);
        discoveredListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, discoveredListStrings);
        listView.setAdapter(discoveredListViewAdapter);
        Toast.makeText(this, "discover", Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discoveredListStrings.add(device.getName());
                discoveredList.add(device);
                discoveredListViewAdapter.notifyDataSetChanged();
            }
        }
    };

    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
           // myLabel.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {

                //text.append(device.getName()+ " / "+ device.getBluetoothClass().getMajorDeviceClass()+"\n");
                pairedListStrings.add(device.getName()+ " / "+ device.getBluetoothClass().getMajorDeviceClass()+"\n");
                pairedList.add(device);
                if(device.getName().equals("HMSoft"))
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        //myLabel.setText("Bluetooth Device Found");
    }


    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

       // myLabel.setText("Bluetooth Opened");
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                          //  myLabel.setText(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData(String msg) throws IOException
    {
        mmOutputStream.write(msg.getBytes());
      //  myLabel.setText("Data Sent");
    }



    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        //myLabel.setText("Bluetooth Closed");
    }

}
