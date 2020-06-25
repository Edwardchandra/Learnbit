package com.example.learnbit.launch.reusableactivity.adapter;

import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.Chat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Chat> chatArrayList;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final int sender = 0;
    private static final int receiver = 1;

    public ChatAdapter(ArrayList<Chat> chatArrayList) {
        this.chatArrayList = chatArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == sender){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sender, parent, false);
            return new SenderViewHolder(view);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == sender){
            ((SenderViewHolder) holder).senderChat.setText(chatArrayList.get(position).getMessage());
            ((SenderViewHolder) holder).senderDateTime.setText(chatArrayList.get(position).getDateTime());
        }else{
            ((ReceiverViewHolder) holder).receiverChat.setText(chatArrayList.get(position).getMessage());
            ((ReceiverViewHolder) holder).receiverDateTime.setText(chatArrayList.get(position).getDateTime());

            if (chatArrayList.get(position).getUserUid()!=null){
                FirebaseStorage.getInstance().getReference("Users").child(chatArrayList.get(position).getUserUid()).child("profileimage").getDownloadUrl()
                        .addOnSuccessListener(uri -> Glide.with(((ReceiverViewHolder) holder).itemView.getContext()).load(uri).into(((ReceiverViewHolder) holder).receiverImageView));

                FirebaseDatabase.getInstance().getReference("Users").child(chatArrayList.get(position).getUserUid()).child("name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.getValue(String.class);
                        if (name!=null){
                            ((ReceiverViewHolder) holder).receiverName.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return (chatArrayList == null) ? 0 : chatArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatArrayList.get(position).getUserUid().equals(user.getUid())){
            return sender;
        }else{
            return receiver;
        }
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder{
        private TextView senderChat, senderDateTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderChat = itemView.findViewById(R.id.senderChat);
            senderDateTime = itemView.findViewById(R.id.senderDateTime);
        }
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder{
        private TextView receiverChat, receiverDateTime, receiverName;
        private ImageView receiverImageView;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverImageView = itemView.findViewById(R.id.receiverImageView);
            receiverChat = itemView.findViewById(R.id.receiverChat);
            receiverName = itemView.findViewById(R.id.receiverName);
            receiverDateTime = itemView.findViewById(R.id.receiverDateTime);

            receiverImageView.setClipToOutline(true);
        }
    }
}
