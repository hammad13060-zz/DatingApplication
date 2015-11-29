package com.hammad13060.datingapplication.BroadcastRecievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hammad13060.datingapplication.Fragments.DisplayMatchFragment;
import com.hammad13060.datingapplication.Interfaces.UpdateLayoutInterface;
import com.hammad13060.datingapplication.helper.MessageClientHelper;

/**
 * Created by Hammad on 17-11-2015.
 */
public class NewMessageBroadcastReceiver extends BroadcastReceiver {

    public static final String EVENT_NEW_MESSAGE = "com.hammad13060.datingapplication.BroadcastRecievers.NewMessageBroadcastReceiver";


    UpdateLayoutInterface module = null;
    String chat_id = null;
    public NewMessageBroadcastReceiver(UpdateLayoutInterface module, String chat_id) {
        super();
        this.module = module;
        this.chat_id = chat_id;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String expected_chat_id = intent.getStringExtra(DisplayMatchFragment.EXTRA_CHAT_ID);
        String message = intent.getStringExtra(MessageClientHelper.EXTRA_MESSAGE);
        boolean myMessage = intent.getBooleanExtra(MessageClientHelper.EXTRA_MY_MESSAGE, false);

        if (chat_id.equals(expected_chat_id)) {
            module.updateLayoutOnEvent();
            if (myMessage) {
                module.updateLayoutOnEvent(message);
            } else {
                module.updateLayoutOnEvent();
            }
        }
    }
}
