package me.franklinye.chess.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.Map;

import me.franklinye.chess.ChatMessage;
import me.franklinye.chess.game.ChessGame;
import me.franklinye.chess.game.ChessVisualizer;
import me.franklinye.chess.R;

/**
 * The fragment which contains all the materials for chatting with another user. It contains a
 * recycler view of messages and a form at the bottom of the screen for sending messages.
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


    /**
     * This method gets the arguments passed to the fragment and sets them to member variables.
     * @param inflater inflater for the view
     * @param container container for the view
     * @param savedInstanceState saved instance state
     * @return view
     */
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
        if (chatKey != null) {
            mChatRef = mAllChats.child(chatKey);
        }

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

    /**
     * The onStart() method attaches the adapter to the recycler view and sets up
     * ValueEventListeners for the active game and messages.
     */
    @Override
    public void onStart() {
        super.onStart();

        // FirebaseRecyclerAdapter for the chat
        mChatAdapter = new FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>
                (ChatMessage.class, R.layout.chat_message_left, ChatMessageViewHolder.class,
                        mChatRef.child("messages")) {
            @Override
            public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
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
                    // system left (for system messages)
                    case 2:
                        View systemMessage = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_message_system_left, parent, false);
                        return new ChatMessageViewHolder(systemMessage);
                    default:
                        return new ChatMessageViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_message_right, parent, false));
                }
                // return super.onCreateViewHolder(parent, viewType);
            }

            // override this so that we can align and color the messages differently based on the
            // author of the message
            @Override
            public int getItemViewType(int position) {
                ChatMessage message = getItem(position);
                if (message.author != null) {
                    if (message.author.matches("system")) {
                        return 2;
                    } else if (!message.author.matches(mUID)) {
                        return 0;
                    } else if (message.author.matches(mUID)) {
                        return 1;
                    }
                }
                return super.getItemViewType(position);
            }

            @Override
            protected void populateViewHolder(ChatMessageViewHolder viewHolder, ChatMessage model,
                                              int position) {
                viewHolder.bind(model, mUID);
            }
        };
        mChatRecycler.setAdapter(mChatAdapter);

        // This listens to the active game for changes
        ValueEventListener gameKeyListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ValueEventListener gameListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mActiveGame = new ChessGame(dataSnapshot); // loads the game from Firebase
                        if (mActiveGame.isOfferedStalemate() && !mActiveGame.getOfferedStalemateBy()
                                .matches(mUser.getUid())) {
                            // do something to accept or decline stalemate
                            StalemateFragment f = StalemateFragment.newInstance(mActiveGameKey);
                            f.show(getFragmentManager(), "STALEMATE");
                        }
                        // checks to see if someone won
                        if (mActiveGame.getWinner() != null) {
                            ValueEventListener winnerListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int winnerWins = dataSnapshot.child("wins").getValue(int.class);
                                    winnerWins++;
                                    mAllUsers.child(mActiveGame.getWinner()).child("wins")
                                            .setValue(winnerWins);
                                    ChatMessage message = new ChatMessage();
                                    message.author = "system";
                                    message.message = dataSnapshot.child("name")
                                            .getValue(String.class) +
                                            " has " + Integer.toString(winnerWins) + " wins.";
                                    mChatRef.child("messages").push().setValue(message);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };

                            ValueEventListener loserListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int loserLosses = dataSnapshot.child("losses")
                                            .getValue(int.class);
                                    loserLosses++;
                                    String loserUid = mActiveGame.getWinner() ==
                                            mActiveGame.getBlackUser() ? mActiveGame.getWhiteUser()
                                            : mActiveGame.getBlackUser();
                                    mAllUsers.child(loserUid).child("losses").setValue(loserLosses);

                                    ChatMessage message = new ChatMessage();
                                    message.author = "system";
                                    message.message = dataSnapshot.child("name")
                                            .getValue(String.class) + " has " +
                                            Integer.toString(loserLosses) + " losses.";
                                    mChatRef.child("messages").push().setValue(message);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };

                            ValueEventListener tieListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    ChatMessage stalemateMessage = new ChatMessage();
                                    stalemateMessage.message = "The stalemate was accepted!";
                                    stalemateMessage.author = "system";
                                    mChatRef.child("messages").push().setValue(stalemateMessage);
                                    int ties = dataSnapshot.child("ties").getValue(int.class);
                                    ties++;
                                    mAllUsers.child(dataSnapshot.getKey()).child("ties").setValue(ties);
                                    ChatMessage message = new ChatMessage();
                                    message.author = "system";
                                    message.message = dataSnapshot.child("name").getValue(String.class) +
                                            " has " + Integer.toString(ties) + " ties.";
                                    mChatRef.child("messages").push().setValue(message);
                                    mChatRef.child("active_game").setValue(null);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };

                            if (mActiveGame.getWinner().matches("stalemate")) { // stalemate
                                mAllUsers.child(mActiveGame.getWhiteUser()).addListenerForSingleValueEvent(tieListener);
                                mAllUsers.child(mActiveGame.getBlackUser()).addListenerForSingleValueEvent(tieListener);
                            } else { // someone won
                                String loserUid = mActiveGame.getWinner() ==
                                        mActiveGame.getBlackUser() ? mActiveGame.getWhiteUser()
                                        : mActiveGame.getBlackUser();
                                mAllUsers.child(mActiveGame.getWinner()).addListenerForSingleValueEvent(winnerListener);
                                mAllUsers.child(loserUid).addListenerForSingleValueEvent(loserListener);
                                mChatRef.child("active_game").setValue(null);
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
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mChatAdapter != null) {
            mChatAdapter.cleanup();
        }
    }

    /**
     * This method is an onClick method which sends a message to the chat and updates the active
     * game if necessary
     * @param v View clicked (ImageButton)
     */
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

                        // mChatAdapter.notifyItemInserted(messages.size() - 1);
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

    /**
     * This method parses a message to see what to do.
     * @param newMessage message string
     */
    public void parseMessage(String newMessage) {
        // starts a new game
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
                        ChatMessage whoMessage = new ChatMessage();
                        whoMessage.message = mUser.getDisplayName() + " is WHITE.";
                        whoMessage.author = "system";
                        mChatRef.child("messages").push().setValue(whoMessage);
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
        } else if (newMessage.matches("[a-h][1-8][a-h][1-8]")) { // a command
            parseCommand(mUser.getUid(), newMessage);
        } else if (newMessage.matches("forfeit")) { // forfeit
            if (mActiveGame != null) {
                ChessGame.Side otherSide;
                if (mActiveGame.getBlackUser().matches(mUser.getUid())) {
                    otherSide = ChessGame.Side.WHITE;
                } else {
                    otherSide = ChessGame.Side.BLACK;
                }
                mActiveGame.endGame(otherSide);
                mAllGames.child(mActiveGameKey).setValue(mActiveGame);
                ChatMessage message = new ChatMessage();
                message.message = mUser.getDisplayName() + " has forfeited!";
                message.author = "system";
                mChatRef.child("messages").push().setValue(message);
            }
        } else if (newMessage.matches("offerstalemate")) { // offer stalemate
            if (mActiveGame != null) {
                mActiveGame.setOfferedStalemate(true);
                mActiveGame.setOfferedStalemateBy(mUser.getUid());
                mAllGames.child(mActiveGameKey).setValue(mActiveGame);
                ChatMessage message = new ChatMessage();
                message.message = mUser.getDisplayName() + " has offered a stalemate!";
                message.author = "system";
                mChatRef.child("messages").push().setValue(message);
            }
        }
    }

    /**
     * Parses a command of two positions
     * @param uid the uid of the one giving the command
     * @param command the command given as a string
     */
    public void parseCommand(String uid, String command) {
        ChessGame.Side side;
        if (mActiveGame != null) {
            if (mActiveGame.getBlackUser().matches(uid)) {
                side = ChessGame.Side.BLACK;
            } else {
                side = ChessGame.Side.WHITE;
            }
            if (mActiveGame.getPlayerToMove() == side) {
                mActiveGame.doCommand(side, command.substring(0, 2), command.substring(2));
                mAllGames.child(mActiveGameKey).setValue(mActiveGame); // pushes game to Firebase
                String visual = ChessVisualizer.visualize(mActiveGame.getBoard());
                ChatMessage visualMessage = new ChatMessage();
                visualMessage.author = "system";
                visualMessage.message = visual + mActiveGame.getStatus();
                mChatRef.child("messages").push().setValue(visualMessage);
            }
        }

    }

    /**
     * View holder for text message which just contains the message text.
     */
    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView mMessageText;

        public ChatMessageViewHolder(View v) {
            super(v);
            mMessageText = (TextView) v.findViewById(R.id.message);
        }

        public void bind(ChatMessage chatMessage, String uid) {
            mMessageText.setText(chatMessage.message);
        }

    }
}
