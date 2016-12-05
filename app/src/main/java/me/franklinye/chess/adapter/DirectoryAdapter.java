package me.franklinye.chess.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.franklinye.chess.R;
import me.franklinye.chess.User;

/**
 * Created by franklinye on 12/1/16.
 */

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.UserHolder> {
    private List<User> mUsers;

    public DirectoryAdapter(List<User> users) {
        mUsers = users;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user, parent, false);
        return new UserHolder(parent);
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        User itemUser = mUsers.get(position);
        holder.bindUser(itemUser);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private User mUser;
        private ImageView mUserImage;
        private TextView mUserName;
        private TextView mChatPreview;

        private final static String USER_KEY = "USER";

        public UserHolder(View v) {
            super(v);

            mUserImage = (ImageView) v.findViewById(R.id.user_picture);
            mUserName = (TextView) v.findViewById(R.id.user_name);
            mChatPreview = (TextView) v.findViewById(R.id.chat_preview);
            v.setOnClickListener(this);
        }

        public void bindUser(User user) {
            mUser = user;
            Picasso.with(mUserImage.getContext()).load(user.getPhotoUrl()).into(mUserImage);
            mUserName.setText(user.getName());
        }

        // TODO: launch ChatActivity from here
        @Override
        public void onClick(View v) {

        }
    }
}
