package me.franklinye.chess.activity;


import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.franklinye.chess.R;
import me.franklinye.chess.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class DirectoryFragment extends Fragment {

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mUsersRef = mDatabase.getReference("users");
    private DatabaseReference mChatsRef = mDatabase.getReference("chats");
    private FirebaseUser mUser;
    private DatabaseReference mUserData;

    private RecyclerView mDirectoryRecycler;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<User, UserHolder> mDirectoryAdapter;

    public DirectoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_directory, container, false);
        mDirectoryRecycler = (RecyclerView) rootView.findViewById(R.id.directory);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mDirectoryRecycler.setLayoutManager(mLinearLayoutManager);


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserData = mUsersRef.child(mUser.getUid());
        mDirectoryAdapter = new FirebaseRecyclerAdapter<User, UserHolder>(User.class, R.layout.list_item_user,
                UserHolder.class, mUsersRef) {

            // TODO: add chat preview
            @Override
            protected void populateViewHolder(UserHolder viewHolder, final User model, int position) {
                if (model.getUid().matches(mUser.getUid())) {
                    ViewGroup.LayoutParams params = viewHolder.itemView.getLayoutParams();
                    params.height = 0;
                    viewHolder.itemView.setLayoutParams(params);
                }

                viewHolder.bindUser(model);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch chatFragment with arguments
                        ValueEventListener keyListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String chatKey = dataSnapshot.getValue(String.class);
                                if (chatKey == null) {
                                    chatKey = mChatsRef.push().getKey();
                                    mUserData.child("chat_keys").child(model.getUid()).setValue(chatKey);
                                    mUsersRef.child(model.getUid()).child("chat_keys").child(mUser.getUid()).setValue(chatKey);
                                }
                                Intent intent = new Intent(getContext(), ChatActivity.class);
                                intent.putExtra("chatKey", chatKey);
                                intent.putExtra("uid", model.getUid());
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT);
                                // ...
                            }
                        };

                        mUserData.child("chat_keys").child(model.getUid()).addListenerForSingleValueEvent(keyListener);
                    }
                });
            }
        };
        mDirectoryRecycler.setAdapter(mDirectoryAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDirectoryAdapter != null) {
            mDirectoryAdapter.cleanup();
        }
    }

    public static class UserHolder extends RecyclerView.ViewHolder {
        private ImageView mUserImage;
        private TextView mUserName;
        private TextView mChatPreview;

        private final static String USER_KEY = "USER";

        public UserHolder(View v) {
            super(v);

            mUserImage = (ImageView) v.findViewById(R.id.user_picture);
            mUserName = (TextView) v.findViewById(R.id.user_name);
            mChatPreview = (TextView) v.findViewById(R.id.chat_preview);
        }

        public void bindUser(User user) {
            Picasso.with(mUserImage.getContext()).load(user.getPhotoUrl()).into(mUserImage);
            mUserName.setText(user.getName());
        }

    }

}
