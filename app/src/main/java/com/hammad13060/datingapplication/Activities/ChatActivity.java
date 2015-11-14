package com.hammad13060.datingapplication.Activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;

import com.hammad13060.datingapplication.Adapters.SwipeMessageListAdapter;
import com.hammad13060.datingapplication.Fragments.DisplayMatchFragment;
import com.hammad13060.datingapplication.R;
import com.hammad13060.datingapplication.helper.MessageClientHelper;
import com.parse.ParseObject;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.messaging.MessageClientListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private String recipient_user_id = null;
    private String recipient_user_name = null;
    private String chat_id = null;

    private ListView messageListView = null;
    //SwipeRefreshLayout swipeRefreshLayout = null;
    //ScrollView scrollView = null;


    private List<ParseObject> messages = null;

    MessageClientHelper messageClientHelper = null;

    private SwipeMessageListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_1);

        Intent intent = getIntent();
        recipient_user_id = intent.getStringExtra(DisplayMatchFragment.EXTRA_RECIPIENT_USER_ID);
        recipient_user_name = intent.getStringExtra(DisplayMatchFragment.EXTRA_RECIPIENT_USER_NAME);
        chat_id = intent.getStringExtra(DisplayMatchFragment.EXTRA_CHAT_ID);

        //swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        //scrollView = (ScrollView)findViewById(R.id.scrollView);

        messageListView = (ListView)findViewById(R.id.listView);

        messageClientHelper = MessageClientHelper.getInstance(this);

        //messages = messageClientHelper.fetchMessages(chat_id);

        messages = new ArrayList<>(0);
        adapter = new SwipeMessageListAdapter(this, messages);

        messageListView.setAdapter(adapter);

        //swipeRefreshLayout.setOnRefreshListener(this);

        /*swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //swipeRefreshLayout.setRefreshing(true);
                fetchMessages();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchMessages();

        //scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void fetchMessages() {
        //swipeRefreshLayout.setRefreshing(true);
        messages = messageClientHelper.fetchMessages(chat_id);
        adapter.setMessages(messages);
        adapter.notifyDataSetChanged();
        messageListView.refreshDrawableState();
        //swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        fetchMessages();
    }

    public void onSendButtonClicked(View view) {
        EditText message_box = (EditText)findViewById(R.id.message_text_box);
        String message = message_box.getText().toString();

        messageClientHelper.sendTextMessage(recipient_user_id, message, chat_id);

        message_box.setText("");
        message_box.setHint("enter message here");
    }
}
