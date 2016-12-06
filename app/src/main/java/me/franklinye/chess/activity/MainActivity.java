package me.franklinye.chess.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.franklinye.chess.R;
import me.franklinye.chess.User;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mUsersRef = mDatabase.getReference("users");

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private String[] mNavArray = new String[] { "Username", "wins", "losses", "ties" };

    /**
     * This onCreate method sets up the Activity and navigation drawer. It also checks to see if
     * the user is authenticated. If not, it boots them out to the Login screen.
     * NavigationDrawer code taken from Google/Android Developers documentation.
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
               // getActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            // User is signed in
            ValueEventListener userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        User user = new User(mUser);
                        mUsersRef.child(mUser.getUid()).setValue(user);
                        HelpFragment f = new HelpFragment();
                        f.show(getSupportFragmentManager(), "HELP");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mUsersRef.child(mUser.getUid()).addListenerForSingleValueEvent(userListener);

        } else {
            // User is signed out
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        if (mUser != null) {
            addDrawerItems();
        }

    }

    /**
     * This method grabs information from the users data stored in Firebase and adds it to the
     * drawer.
     */
    private void addDrawerItems() {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userString = dataSnapshot.child("name").getValue(String.class);
                String winsString = "Wins: " + dataSnapshot.child("wins").getValue(int.class).toString();
                String tiesString = "Ties: " + dataSnapshot.child("ties").getValue(int.class).toString();
                String lossesString = "Losses: " + dataSnapshot.child("losses").getValue(int.class).toString();
                mNavArray[0] = userString;
                mNavArray[1] = winsString;
                mNavArray[2] = tiesString;
                mNavArray[3] = lossesString;
                mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mNavArray);
                mDrawerList.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mUsersRef.child(mUser.getUid()).addValueEventListener(userListener);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mNavArray);
        mDrawerList.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    /**
     * This method handles the menu items
     * @param item item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            HelpFragment f = new HelpFragment();
            f.show(getSupportFragmentManager(), "HELP");
            return true;
        } else if (id == R.id.action_signout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
