package com.hammad13060.datingapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NSDService extends Service {
    public NSDService() {
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
