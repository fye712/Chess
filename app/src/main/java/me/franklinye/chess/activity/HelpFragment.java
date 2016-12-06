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

import me.franklinye.chess.R;

/**
 * This DialogFragment launches an AlertDialog with the rules and commands of the game.
 * A simple {@link DialogFragment} subclass.
 */
public class HelpFragment extends DialogFragment {


    public HelpFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.help_dialog).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        }).setTitle("Help");
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
