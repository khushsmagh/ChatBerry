package com.bowfletchers.chatberry.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bowfletchers.chatberry.Adapters.MessagesAdapter;
import com.bowfletchers.chatberry.ClassLibrary.Member;
import com.bowfletchers.chatberry.ClassLibrary.Message;
import com.bowfletchers.chatberry.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageViewer extends AppCompatActivity {
    FirebaseAuth mAuthentication;
    DatabaseReference mDatabase;
    TextView newMessage;
    ArrayList<Message> msgs;
    private RecyclerView recyclerView;
    private MessagesAdapter msgAdapter;
    private static final String LOG_TAG = MessageViewer.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_viewer);
        mAuthentication = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("chats");
        newMessage = findViewById(R.id.messageSend);


        msgs = new ArrayList<>();
        Message msg = new Message("bob", "hi");
        msgs.add(msg);
        //set up recycler view
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        msgAdapter = new MessagesAdapter(this, msgs);
        recyclerView.setAdapter(msgAdapter);


        DatabaseReference chat = mDatabase.child("chat1");
        DatabaseReference messages = chat.child("messages");

        messages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                    Log.i(LOG_TAG, "Message: " + msgSnapshot.child("content").getValue(String.class) + " Sender: " + msgSnapshot.child("senderID").getValue(Integer.class));

                    Message message = new Message(msgSnapshot.child("senderID").getValue(Integer.class).toString(), msgSnapshot.child("content").getValue(String.class));
                    Log.i(LOG_TAG, "MSG OBJ: " + message.getMessage());
                   msgs.add(message);
                }

                
               msgAdapter.notifyDataSetChanged();
            //    displayToast(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "onCancelled", databaseError.toException());
            }
        });


        //Log.i(LOG_TAG, getSenderName("KCrD3GE9xLgIguld3QnViEATd5L2"));

    }

    public String getSenderName(final String id) {
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users");
        final String[] name = {""};
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Member user = snapshot.getValue(Member.class);
                   // assert user != null;
                    Log.i(LOG_TAG, snapshot.child("id").getValue().toString());

                    if (snapshot.child("id").getValue().toString().equals(id)) {
                       name[0] = snapshot.child("name").getValue().toString();
                       Log.i(LOG_TAG,snapshot.child("name").getValue().toString());
                    }
                    //    displayToast(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, "onCancelled", databaseError.toException());
            }
        });

        return name[0];
    }



    public void displayToast(String message){
        Toast.makeText(this, message,Toast.LENGTH_SHORT).show();
    }



}