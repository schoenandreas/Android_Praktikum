package com.example.tbartsch.elebar;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Andreas on 05.12.2017.
 */

public class secondActivity extends Activity {

    BluetoothService myService;
    boolean isBound = false;


    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            BluetoothService.MyLocalBinder binder = (BluetoothService.MyLocalBinder) service;
            myService = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondactivity);

        Button bindButton = (Button) findViewById(R.id.bindButton2);

        try{
            Intent intent = new Intent(this, BluetoothService.class);

            bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
        }catch(Exception e){

            Toast.makeText(secondActivity.this, "Binding Error", Toast.LENGTH_LONG).show();
            Log.e("MYAPP", "exception", e);
        }
    }

    public void onClickBind2(View v){
        try{
            myService.toast();
        }catch(Exception e){
            String a = String.valueOf(isBound);
            Toast.makeText(secondActivity.this, a, Toast.LENGTH_LONG).show();
            Log.e("MYAPP", "exception", e);
        }


    }
}
