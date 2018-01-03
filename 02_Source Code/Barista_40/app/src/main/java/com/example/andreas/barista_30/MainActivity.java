package com.example.andreas.barista_30;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

public class MainActivity extends AppCompatActivity {

    //Fragmentverwaltung
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    //Deklaration der Variablen fuer BT-dialog
    //<editor-fold desc="Dialog declarations">
    private AlertDialog dialog;

    private List pairedList = new ArrayList();
    private List discoveredList = new ArrayList();
    private List pairedListStrings = new ArrayList();
    private List discoveredListStrings = new ArrayList();

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
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        TabLayout tablayout = (TabLayout) findViewById(R.id.tabs);
        tablayout.setupWithViewPager(mViewPager);

        //BT Dialog, fuer Entwicklung ohne BT connection zum Shield hier auskommentieren
       // bluetoothDialog();
    }

    private void setupViewPager(ViewPager viewPager) {
        //Fragemnts zu Adapter hinzufuegen
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "Help");
        adapter.addFragment(new Tab2Fragment(), "SpeechRecognition");
        adapter.addFragment(new Tab3Fragment(), "DrinkLexikon");
        //Adapter auf Viewpager setzen
        viewPager.setAdapter(adapter);
        //Tab2Fragment als Start setzen
        viewPager.setCurrentItem(1, true);
    }

    //Notwendig fuer onActivityResult aus der Speechrecognition in Tab2Fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Erstellt den BT Dialog
    private void bluetoothDialog() {
        //Builder fuer Dialog
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setCancelable(false);
        //Views initialisieren
        View mView = getLayoutInflater().inflate(R.layout.bluetooth_dialog, null);
        ImageView imgLoad = mView.findViewById(R.id.imageLoad);
        progressBar = mView.findViewById(R.id.progressBar);
        final ListView listView = mView.findViewById(R.id.listView);
        final ListView listView2 = mView.findViewById(R.id.listView2);

        //findet paired BT devices
        findBT();
        //paired Devices anzeigen
        pairedListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pairedListStrings);
        listView.setAdapter(pairedListViewAdapter);

        //OnClickListener fuer ListViews und refreshButton setzen
        //<editor-fold desc="setOnclickListeners">
        imgLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Lade icon sichtbar
                progressBar.setVisibility(View.VISIBLE);
                //nach BT devices suchen
                discoverBT(listView2);
            }
        });
        //paired Devices
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    //device zuweisen auf den geklickt wurde und versuchen mit dem Device zu verbinden
                    mmDevice = (BluetoothDevice) pairedList.get(i);
                    openBT();
                } catch (Exception e) {
                    Log.w("APP", e);
                }
            }
        });
        //discovered Devices
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    //device zuweisen auf den geklickt wurde und versuchen mit dem Device zu verbinden
                    mmDevice = (BluetoothDevice) discoveredList.get(i);
                    openBT();
                } catch (Exception e) {
                    Log.w("APP", e);
                }
            }
        });
        //</editor-fold>

        //Dialog initialisieren und zeigen
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        //Devices suchen
        discoverBT(listView2);
    }

    private void discoverBT(ListView listView) {

        //alten gefundenen Geraete loeschen
        discoveredList.clear();
        discoveredListStrings.clear();

        //BT discovery starten
        mBluetoothAdapter.startDiscovery();

        IntentFilter intentFilter = new IntentFilter((BluetoothDevice.ACTION_FOUND));
        //Broadcastreceiver der auf BT found Intent reagiert registrieren
        registerReceiver(myReceiver, intentFilter);
        //discovered Devices liste in listView zeigen
        discoveredListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, discoveredListStrings);
        listView.setAdapter(discoveredListViewAdapter);
    }
    //Broadcastreceiver der auf BT found Intent reagiert
    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //wenn BT found intent
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Device und dessen Name in Listen hinzufuegen
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discoveredListStrings.add(device.getName());
                discoveredList.add(device);
                //crashfix?
                discoveredListStrings.removeAll(Collections.singleton(null));
                //listView aktualisieren
                discoveredListViewAdapter.notifyDataSetChanged();
                //Lade icon unsichtbar
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    };

    //findet paired BT devices
    private void findBT() {
        //checken ob Handy BT hat
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // myLabel.setText("No bluetooth adapter available");
        }
        //checkt ob BT an ist, schaltet an falls nicht
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        //Paired BT Geraete
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        //Fuegt alle Geraete und deren Namen in Listen ein
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedListStrings.add(device.getName() + "\n");
                pairedList.add(device);
                //HMSoft als Standarddevice wenn paired da als BT Shield bekannt
                if (device.getName().equals("HMSoft")) {
                    mmDevice = device;
                    break;
                }
            }
        }
    }


    private void openBT() throws IOException {
        //Socket inkl. input/outpustream mit device erstellen
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        //BT dialog schließen
        dialog.cancel();
        //auf daten von device hoeren
        beginListenForData();
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        //ASCII newline
        final byte newline = 10;

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        //neuen thread erstellen
        workerThread = new Thread(new Runnable() {
            public void run() {
                //solange kein interrupt und keine exception
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        //wenn auf dem inputstream etwas anliegt
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            //bytes von inputstream durchgehen
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                //wenn newline, dann aktuelles lesen fertig
                                if (b == newline) {
                                    //readbuffer in String konvertieren
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    //readBUferPosition reset fuer naechste auslesung
                                    readBufferPosition = 0;
                                    //ausfuehren was mit data passieren soll
                                    handler.post(new Runnable() {
                                        public void run() {
                                            //  myLabel.setText(data);
                                        }
                                    });
                                //ansonsten naechstes byte
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
        //thread starten
        workerThread.start();
    }

    //String an BT device schicken
    protected void sendData(String msg) throws IOException {
        Toast.makeText(this, "sending data" + msg, Toast.LENGTH_SHORT).show();
        mmOutputStream.write(msg.getBytes());
    }

    //BT socket schließen
    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        //myLabel.setText("Bluetooth Closed");
    }

}
