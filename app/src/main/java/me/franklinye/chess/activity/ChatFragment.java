package me.franklinye.chess.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.franklinye.chess.ChatMessage;
import me.franklinye.chess.ChessGame;
import me.franklinye.chess.ChessVisualizer;
import me.franklinye.chess.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private RecyclerView mChatRecycler;
    private EditText mTextField;
    private ImageButton mSendButton;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mAllChats = mDatabase.getReference("chats");
    private DatabaseReference mAllGames = mDatabase.getReference("games");
    private DatabaseReference mAllUsers = mDatabase.getReference("users");
    private DatabaseReference mChatRef;
    private FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder> mChatAdapter;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String mUID = mUser.getUid();
    private String mOtherUid;

    private ChessGame mActiveGame;
    private String mActiveGameKey;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        mChatRecycler = (RecyclerView) rootView.findViewById(R.id.chat_recycler);
        mTextField = (EditText) rootView.findViewById(R.id.message_form);
        mSendButton = (ImageButton) rootView.findViewById(R.id.send_message);

        Bundle arguments = getArguments();
        String chatKey = arguments.getString("chatKey");
        mChatRef = mAllChats.child(chatKey);

        mOtherUid = arguments.getString("uid");

        mChatRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        mChatRecycler.setLayoutManager(layoutManager);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mChatAdapter = new FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>
                (ChatMessage.class, R.layout.chat_message_left, ChatMessageViewHolder.class,
                        mChatRef.child("messages")) {
            @Override
            public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch(viewType) {
                    // left aligned
                    case 0:
                        View leftMessage = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_message_left, parent, false);
                        return new ChatMessageViewHolder(leftMessage);

                    // right aligned
                    case 1:
                        View rightMessage = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_message_right, parent, false);
                        return new ChatMessageViewHolder(rightMessage);
                    default:
                        return new ChatMessageViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_message_right, parent, false));
                }
                // return super.onCreateViewHolder(parent, viewType);
            }

            @Override
            public int getItemViewType(int position) {
                if (!getItem(position).author.matches(mUID)) {
                    return 0;
                }
                return 1;
            }

            @Override
            protected void populateViewHolder(ChatMessageViewHolder viewHolder, ChatMessage model, int position) {
                viewHolder.bind(model, mUID);
                mChatRecycler.scrollToPosition(mChatRecycler.getAdapter().getItemCount());
            }
        };

        ValueEventListener gameKeyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ValueEventListener gameListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mActiveGame = new ChessGame(dataSnapshot);
                        if (mActiveGame.isOfferedStalemate() && !mActiveGame.getOfferedStalemateBy().matches(mUser.getUid())) {
                            // do something to accept or decline stalemate
                            StalemateFragment f = StalemateFragment.newInstance(mActiveGameKey);
                            f.show(getFragmentManager(), "STALEMATE");
                        }
                        if (mActiveGame.getWinner() != null) {
                            ValueEventListener winnerListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int winnerWins = dataSnapshot.child("wins").getValue(int.class);
                                    winnerWins++;
                                    mAllUsers.child(mActiveGame.getWinner()).child("wins").setValue(winnerWins);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };

                            ValueEventListener loserListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int loserLosses = dataSnapshot.child("losses").getValue(int.class);
                                    loserLosses++;
                                    String loserUid = mActiveGame.getWinner() ==
                                            mActiveGame.getBlackUser() ? mActiveGame.getWhiteUser()
                                            : mActiveGame.getBlackUser();
                                    mAllUsers.child(loserUid).child("losses").setValue(loserLosses);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };

                            ValueEventListener tieListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int ties = dataSnapshot.child("ties").getValue(int.class);
                                    ties++;
                                    mAllUsers.child(dataSnapshot.getKey()).child("ties").setValue(ties);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };

                            if (mActiveGame.getWinner().matches("stalemate")) {
                                mAllUsers.child(mActiveGame.getWhiteUser()).addListenerForSingleValueEvent(tieListener);
                                mAllUsers.child(mActiveGame.getBlackUser()).addListenerForSingleValueEvent(tieListener);
                            } else {
                                String loserUid = mActiveGame.getWinner() ==
                                        mActiveGame.getBlackUser() ? mActiveGame.getWhiteUser()
                                        : mActiveGame.getBlackUser();
                                mAllUsers.child(mActiveGame.getWinner()).addListenerForSingleValueEvent(winnerListener);
                                mAllUsers.child(loserUid).addListenerForSingleValueEvent(loserListener);
                            }

                            mChatRef.child("active_game").setValue(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                mActiveGameKey = dataSnapshot.getValue(String.class);
                if (mActiveGameKey != null) {
                    mAllGames.child(mActiveGameKey).addValueEventListener(gameListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mChatRef.child("active_game").addValueEventListener(gameKeyListener);

        mChatRecycler.setAdapter(mChatAdapter);
    }

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView mMessageText;

        public ChatMessageViewHolder(View v) {
            super(v);
            mMessageText = (TextView) v.findViewById(R.id.message);
        }

        public void bind(ChatMessage chatMessage, String uid) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mMessageText.getLayoutParams();
            mMessageText.setText(chatMessage.message);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mChatAdapter != null) {
            mChatAdapter.cleanup();
        }
    }

    public void sendMessage(View v) {
        String newMessage = mTextField.getText().toString();
        mTextField.setText("");

        if (mChatRef != null) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.message = newMessage;
            chatMessage.author = mUID;

            if (!newMessage.matches("")) {
                mChatRef.child("messages").push().setValue(chatMessage);
                parseMessage(newMessage);
                ValueEventListener chatListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, ChatMessage> messages = dataSnapshot.getValue(
                                new GenericTypeIndicator<Map<String, ChatMessage>>() {
                                }
                        );

                        mChatAdapter.notifyItemInserted(messages.size() - 1);
                        mChatRecycler.scrollToPosition(mChatAdapter.getItemCount() - 1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                mChatRef.child("messages").addValueEventListener(chatListener);
            }
        } else {
            Toast.makeText(getContext(), "Database not available", Toast.LENGTH_SHORT).show();
        }
    }

    public void parseMessage(String newMessage) {
        if (newMessage.matches("startgame")) {
            ValueEventListener keyListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        ChessGame game = new ChessGame(mUser.getUid(), mOtherUid);
                        game.init();
                        String activeGameKey = mChatRef.child("active_game").push().getKey();
                        mChatRef.child("active_game").setValue(activeGameKey);
                        mAllGames.child(activeGameKey).setValue(game);
                        String visual = ChessVisualizer.visualize(game.getBoard());
                        ChatMessage visualMessage = new ChatMessage();
                        visualMessage.author = "system";
                        visualMessage.message = visual + game.getStatus();
                        mChatRef.child("messages").push().setValue(visualMessage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT);
                    // ...
                }
            };
            mChatRef.child("active_game").addListenerForSingleValueEvent(keyListener);
        } else if (newMessage.matches("[a-h][1-8][a-h][1-8]")) {
            parseCommand(mUser.getUid(), newMessage);
        } else if (newMessage.matches("forfeit")) {
            if (mActiveGame != null) {
                ChessGame.Side side;
                if (mActiveGame.getBlackUser().matches(mUser.getUid())) {
                    side = ChessGame.Side.WHITE;
                } else {
                    side = ChessGame.Side.BLACK;
                }
                mActiveGame.endGame(side);
                mAllGames.child(mActiveGameKey).setValue(mActiveGame);
            }
        } else if (newMessage.matches("offerstalemate")) {
            if (mActiveGame != null) {
                mActiveGame.setOfferedStalemate(true);
                mActiveGame.setOfferedStalemateBy(mUser.getUid());
                mAllGames.child(mActiveGameKey).setValue(mActiveGame);
            }
        }
    }

    public void parseCommand(String uid, String command) {
        ChessGame.Side side;
        if (mActiveGame != null) {
            if (mActiveGame.getBlackUser().matches(uid)) {
                side = ChessGame.Side.BLACK;
            } else {
                side = ChessGame.Side.WHITE;
            }

            mActiveGame.doCommand(side, command.substring(0, 2), command.substring(2));
            mAllGames.child(mActiveGameKey).setValue(mActiveGame);
            String visual = ChessVisualizer.visualize(mActiveGame.getBoard());
            ChatMessage visualMessage = new ChatMessage();
            visualMessage.author = "system";
            visualMessage.message = visual + mActiveGame.getStatus();
            mChatRef.child("messages").push().setValue(visualMessage);
        }

    }
}
