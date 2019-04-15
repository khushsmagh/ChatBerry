package com.bowfletchers.chatberry.Activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.bowfletchers.chatberry.Adapters.ChatHistoryInfoAdapter;
import com.bowfletchers.chatberry.ClassLibrary.FirebaseInstances;
import com.bowfletchers.chatberry.ClassLibrary.Member;
import com.bowfletchers.chatberry.R;
import com.bowfletchers.chatberry.ViewModel.ChatHistoryListModel.ChatHistoryReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatHistoryList extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ChatHistoryInfoAdapter mAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private String  logInMemberName;

    private final DatabaseReference chats = FirebaseDatabase.getInstance().getReference("chats");
    private final String auth = FirebaseAuth.getInstance().getUid();
    private String chatId;
    private String friendId;
    private List<Member> memberList = new ArrayList<>();
    private List<String> chatIdList = new ArrayList<>();
    private List<String> friendIdList = new ArrayList<>();

    private ArrayList<String> chatsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseInstances.getDatabaseAuth();
        mUser = mAuth.getCurrentUser();

        if (mUser == null) {
            Intent backToSignInIntent = new Intent(ChatHistoryList.this, LoginAccount.class);
            startActivity(backToSignInIntent);
        }
        getAllChats();
        getAllChatsInfo();

        mRecyclerView = findViewById(R.id.chat_history_recycler_view);
        mAdapter = new ChatHistoryInfoAdapter( this, memberList,chatIdList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String userName = mUser.getDisplayName();
        if (userName != null && !userName.equals("")) {
            setTitle("Welcome " + userName);
        } else {
            setTitle("Welcome");
        }
    }

    private void getAllChatsInfo() {
        ChatHistoryReference chatsViewModel = ViewModelProviders.of(this).get(ChatHistoryReference.class);
        LiveData<DataSnapshot> users = chatsViewModel.getUsers();
        users.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                memberList.clear();
                for(DataSnapshot friends : dataSnapshot.getChildren()){
                    if(friendIdList.contains(friends.child("id").getValue().toString())){
                        memberList.add(new Member(friends.child("id").getValue().toString(), friends.child("name").getValue().toString(), friends.child("email").getValue().toString(),
                                                friends.child("profilePicture").getValue().toString()));
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getAllChats() {
        final ChatHistoryReference chatsViewModel = ViewModelProviders.of(this).get(ChatHistoryReference.class);
        LiveData<DataSnapshot> chats = chatsViewModel.getChats();
        chats.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                friendIdList.clear();
                for(DataSnapshot chats: dataSnapshot.getChildren()){
                    if(auth.equals(chats.child("senderID").getValue().toString())){
                        Log.d("IDNAME",chats.child("receiverID").getValue().toString() );
                        friendIdList.add(chats.child("receiverID").getValue().toString());
                    }
                    else if(auth.equals(chats.child("receiverID").getValue().toString())){
                        Log.d("IDNAME",chats.child("senderID").getValue().toString() );
                        friendIdList.add(chats.child("senderID").getValue().toString());
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_profile:
                Intent userProfileIntent = new Intent(ChatHistoryList.this, UserProfile.class);
                startActivity(userProfileIntent);
                return true;
            case R.id.my_friends:
                Intent userFriendsIntent = new Intent(ChatHistoryList.this, FriendList.class);
                startActivity(userFriendsIntent);
                return true;
            case R.id.homePage:
                Intent chatListIntent = new Intent(ChatHistoryList.this, ChatHistoryList.class);
                startActivity(chatListIntent);
            case R.id.createStory:
                Intent createNewStoryIntent = new Intent(ChatHistoryList.this, CreateUserStory.class);
                startActivity(createNewStoryIntent);
        }
        return super.onOptionsItemSelected(item);
    }

}
