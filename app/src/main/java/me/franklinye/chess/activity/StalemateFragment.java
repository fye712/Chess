package me.franklinye.chess.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.franklinye.chess.ChessGame;
import me.franklinye.chess.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StalemateFragment extends DialogFragment {

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mGames = mDatabase.getReference("games");

    private String mGameKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGameKey = getArguments().getString("gameKey");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.stalemate_offer)
                .setPositiveButton(R.string.accept_stalemate, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        resolveStalemate(true);
                    }
                })
                .setNegativeButton(R.string.decline_stalemate, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        resolveStalemate(false);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public static StalemateFragment newInstance(String gameKey) {
        StalemateFragment f = new StalemateFragment();

        Bundle args = new Bundle();
        args.putString("gameKey", gameKey);
        f.setArguments(args);

        return f;
    }

    private void resolveStalemate(boolean accepted) {
        if (accepted) {
            ValueEventListener gameListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ChessGame game = new ChessGame(dataSnapshot);
                    game.stalemate();
                    mGames.child(mGameKey).setValue(game);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mGames.child(mGameKey).addListenerForSingleValueEvent(gameListener);
        } else {
            mGames.child(mGameKey).child("offerdStalemate").setValue(false);
        }
    }


}
