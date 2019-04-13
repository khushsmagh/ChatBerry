package com.bowfletchers.chatberry.Adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bowfletchers.chatberry.Activities.MessageViewer;
import com.bowfletchers.chatberry.ClassLibrary.Member;
import com.bowfletchers.chatberry.R;

import java.security.PrivateKey;
import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder> {

    private List<Member> friendList;

    public FriendListAdapter(List<Member> friendList) {
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public FriendListAdapter.FriendListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FriendListViewHolder
                (LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.FriendListViewHolder friendListViewHolder, final int i) {
        Member currentMember = friendList.get(i);
        friendListViewHolder.bindTo(currentMember);

        friendListViewHolder.friendChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Member selectedMember = friendList.get(i);
                Intent goToChatIntent = new Intent(v.getContext(), MessageViewer.class);
                goToChatIntent.putExtra("chatMember", selectedMember);
                v.getContext().startActivity(goToChatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    class FriendListViewHolder extends RecyclerView.ViewHolder {

        private TextView friendName;
        private Button friendChatButton;
        private Button friendDetailButton;
        private int currentPosition;

        public FriendListViewHolder(@NonNull final View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friend_item_name);
            friendChatButton = itemView.findViewById(R.id.buttonChat);
            friendDetailButton = itemView.findViewById(R.id.buttonDetail);
            currentPosition = getAdapterPosition();

            friendChatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // open chat window with clicked friend
                    Toast.makeText(v.getContext(), currentPosition + "", Toast.LENGTH_SHORT).show();
                }
            });

            friendDetailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // display clicked friend profile info
                    Toast.makeText(v.getContext(), "display profile info of this friend", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void bindTo(Member memberData) {
            friendName.setText(memberData.getName());
        }
    }
}
