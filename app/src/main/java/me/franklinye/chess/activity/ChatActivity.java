package me.franklinye.chess.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.franklinye.chess.R;

/**
 * This activity is the screen in which you are chatting with another user.
 */
public class ChatActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // listener to set the title of the action bar
        ValueEventListener userNameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child(getIntent().getExtras().getString("uid"))
                        .child("name").getValue(String.class);
                getSupportActionBar().setTitle(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.getReference("users").addListenerForSingleValueEvent(userNameListener);

        // putting arguments in the fragment
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString("chatKey", getIntent().getExtras().getString("chatKey"));
            arguments.putString("uid", getIntent().getExtras().getString("uid"));

            ChatFragment chatFragment = new ChatFragment();
            chatFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.chat_container, chatFragment).commit();
        }
    }
}
