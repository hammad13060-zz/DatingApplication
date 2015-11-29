package com.hammad13060.datingapplication.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.hammad13060.datingapplication.BroadcastRecievers.NewMessageBroadcastReceiver;
import com.hammad13060.datingapplication.Fragments.DisplayMatchFragment;
import com.hammad13060.datingapplication.R;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Hammad on 11-11-2015.
 */

//class made singleton bcause we need only one message client per app user
public class MessageClientHelper {

    private static final String TAG = "MessageClientHelper";

    public static final String EXTRA_MESSAGE = "com.hammad13060.datingapplication.helper.MESSAGE";
    public static final String EXTRA_MY_MESSAGE = "com.hammad13060.datingapplication.helper.EXTRA_MY_MESSAGE";

    public static final String HEADER_CHAT_ID = "com.hammad13060.datingapplication.helper.CHAT_ID";
    public static final String HEADER_SENDER_ID = "com.hammad13060.datingapplication.helper.SENDER_ID";

    //class and key description of ChatData class
    public static final String CLASS_CHAT_DATA = "ChatData";
    public static final String CHAT_DATA_MESSAGES = "messages";

    //class and key description of Message class
    public static final String CLASS_MESSAGE = "Message";
    public static final String MESSAGE_SENDER_ID = "sender_id";
    public static final String MESSAGE_TEXT_MESSAGE = "text_message";

    private Context context = null;
    private static MessageClientHelper instance = null;


    SinchClient sinchClient = null;
    MessageClient messageClient = null;

    private String appKey = null;
    private String appSecret = null;
    private String userId = null;

    private DisplayMatchFragment displayMatchFragment = null;

    private MessageClientHelper() {

    }

    private MessageClientHelper(Context context) {
        this.context = context;
        appKey = context.getString(R.string.sinch_app_key);
        appSecret = context.getString(R.string.sinch_app_secret);
        userId = AccessToken.getCurrentAccessToken().getUserId();
        this.displayMatchFragment = null;
    }

    public static MessageClientHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MessageClientHelper(context);
            instance.setupClient();
        }
        return instance;
    }

    public void setupClient() {
        enableParse();
        //setting up sinch client
        sinchClient = Sinch.getSinchClientBuilder()
                .context(context)
                .applicationKey(context.getString(R.string.sinch_app_key))
                .applicationSecret(context.getString(R.string.sinch_app_secret))
                .environmentHost("sandbox.sinch.com")
                .userId(userId)
                .build();

        //enable messaging and push service
        sinchClient.setSupportMessaging(true);
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection();

        //adding sinch listener. Used for listening the sinch startup
        sinchClient.addSinchClientListener(initSinchClientListener());

        //starting the sinch client
        sinchClient.start();

        //retrieving message client
        messageClient = sinchClient.getMessageClient();
        //adding message listener
        messageClient.addMessageClientListener(initMessageClientListener());

    }

    private void enableParse() {
        // Enable Local Datastore.
        if (Constants.parseDisabled) {
            Parse.enableLocalDatastore(context);
            Parse.initialize(context, "Oi06rcMuTImq7ZolKPfanXUZTZBDhl23a91xvEQR", "Up5UgrEybCvkQDaUStZFLGOCOO7NrV1sMOa2Vbsm");
            Constants.parseDisabled = false;
        }
    }

    public void terminateMessageClient() {
        if (instance != null) {
            sinchClient.stopListeningOnActiveConnection();
            sinchClient.terminate();
            instance = null;
        }
    }

    private SinchClientListener initSinchClientListener() {
        return new SinchClientListener() {
            @Override
            public void onClientStarted(SinchClient sinchClient) {
                Log.d(TAG, "sinch client tarted successfully !!!");
            }

            @Override
            public void onClientStopped(SinchClient sinchClient) {
                Log.d(TAG, "sinch client stopped !!!");
            }

            @Override
            public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {
                Log.d(TAG, "sinch client failed !!!");
            }

            @Override
            public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {
                Log.d(TAG, "sinch client tarted successfully !!!");
            }

            @Override
            public void onLogMessage(int i, String s, String s1) {
                Log.d(TAG, s + " !!!");
            }
        };
    }

    private MessageClientListener initMessageClientListener() {
        return new MessageClientListener() {

            @Override
            public void onIncomingMessage(MessageClient messageClient, com.sinch.android.rtc.messaging.Message message) {
                Log.d(TAG, "onIncomingMessage called");
                String chat_id = message.getHeaders().get(HEADER_CHAT_ID);
                String textMessage = message.getTextBody();
                String sender_id = message.getSenderId();
                saveTextMessageToParseCloud(textMessage, sender_id, chat_id, false);
            }

            @Override
            public void onMessageSent(MessageClient messageClient, com.sinch.android.rtc.messaging.Message message, String s) {
                Log.d(TAG, "onMessageSent callback called");
                String textMessage = message.getTextBody();
                String chat_id = message.getHeaders().get(HEADER_CHAT_ID);
                String sender_id = message.getSenderId();

                /*saveTextMessageToParseCloud(textMessage, sender_id, chat_id);*/
                sendNewMessageBroadcast(chat_id, textMessage, true);
            }

            @Override
            public void onMessageFailed(MessageClient messageClient, com.sinch.android.rtc.messaging.Message message, MessageFailureInfo messageFailureInfo) {
                Log.d(TAG, "onMessageFailed");
                String textMessage = message.getTextBody();
                String chat_id = message.getHeaders().get(HEADER_CHAT_ID);
                String sender_id = message.getSenderId();

                saveTextMessageToParseCloud(textMessage, sender_id, chat_id, true);
                //sendNewMessageBroadcast(chat_id, textMessage, true);
            }

            @Override
            public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {
                Log.d(TAG, "onMessageSent callback called");
            }

            @Override
            public void onShouldSendPushData(MessageClient messageClient, com.sinch.android.rtc.messaging.Message message, List<PushPair> list) {

            }
        };
    }


    private void saveTextMessageToParseCloud(final String textMessage, String sender_id, final String chat_id, boolean broadcast) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS_CHAT_DATA);

        final ParseObject textMessageObject = new ParseObject(CLASS_MESSAGE);
        textMessageObject.put(MESSAGE_SENDER_ID, sender_id);
        textMessageObject.put(MESSAGE_TEXT_MESSAGE, textMessage);


        try {
            textMessageObject.save();
            ParseObject object = query.get(chat_id);
            object.add("messages", textMessageObject);
            object.save();
            sendNewMessageBroadcast(chat_id, textMessage, broadcast);
        } catch (ParseException e) {
            Toast.makeText(context, "error while sending/receiving messages" + e.toString(), Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    public void sendTextMessage(String recipientUserId, String msg, String chat_id) {
        WritableMessage message = new WritableMessage(recipientUserId, msg);
        message.addHeader(HEADER_CHAT_ID, chat_id);
        message.addHeader(HEADER_SENDER_ID, AccessToken.getCurrentAccessToken().getUserId());
        messageClient.send(message);
    }

    public void registerDisplayMatchFragment(DisplayMatchFragment displayMatchFragment) {
        this.displayMatchFragment = displayMatchFragment;
    }

    public List<ParseObject> fetchMessages(String chat_id) {
        //query for retrieving chat data
        ParseQuery<ParseObject> chat_data_query = ParseQuery.getQuery(CLASS_CHAT_DATA);

        //list to be returned with chat messages
        List<ParseObject> chat_messages = new ArrayList<>(0);

        try {
            //actually retrieving chat data on the basis of chat id
            ParseObject chat_data = chat_data_query.get(chat_id);
            //retrieving the list of object containing pointer to messages
            List<ParseObject> chat_message_id_list = chat_data.getList(CHAT_DATA_MESSAGES);

            //query for retrieving relavent list of messages
            ParseQuery<ParseObject> message_query = ParseQuery.getQuery(CLASS_MESSAGE);
            //sorting on the basis of createdAt time stamp
            chat_messages = (List<ParseObject>) message_query.whereContainedIn("objectId", getObjectIdList(chat_message_id_list))
                    .addAscendingOrder("createdAt").find();
            Log.d(TAG, "chat messages: " + chat_messages);
            return chat_messages;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return chat_messages;
    }

    private List<String> getObjectIdList(List<ParseObject> dataList) {
        List<String> objectIdList = new ArrayList<>(0);
        for (ParseObject object: dataList) {
            objectIdList.add(object.getObjectId());
        }

        return objectIdList;
    }

    private void sendNewMessageBroadcast(String chat_id, String textMessage, boolean myMessage) {
        Intent newMessageIntent = new Intent();
        newMessageIntent.setAction(NewMessageBroadcastReceiver.EVENT_NEW_MESSAGE);
        newMessageIntent.putExtra(DisplayMatchFragment.EXTRA_CHAT_ID, chat_id);
        newMessageIntent.putExtra(MessageClientHelper.EXTRA_MESSAGE, textMessage);

        if (myMessage) {
            newMessageIntent.putExtra(EXTRA_MY_MESSAGE, true);
        } else {
            newMessageIntent.putExtra(EXTRA_MY_MESSAGE, false);
        }

        context.sendBroadcast(newMessageIntent);
    }
}
