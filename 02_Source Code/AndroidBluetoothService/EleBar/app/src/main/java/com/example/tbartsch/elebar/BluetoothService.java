package com.example.tbartsch.elebar;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by Andreas on 05.12.2017.
 */

public class BluetoothService extends Service {

    private final IBinder myBinder = new MyLocalBinder();

    private int i = 0;
    @Override
    public IBinder onBind(Intent arg0) {
        Toast.makeText(BluetoothService.this, "ServiceOnBind", Toast.LENGTH_LONG).show();
        return myBinder;
    }

    public class MyLocalBinder extends Binder {
            BluetoothService getService() {
                Toast.makeText(BluetoothService.this, "ServiceGetService", Toast.LENGTH_LONG).show();
            return BluetoothService.this;
        }
    }

    public void toast(){
        ++i;
        Toast.makeText(BluetoothService.this, "ServiceToast: "+i, Toast.LENGTH_LONG).show();
    }
}