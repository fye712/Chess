package me.franklinye.chess;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by franklinye on 11/27/16.
 */

public class User {
    private String name;
    private String photoUrl;
    private int wins;
    private int losses;
    private int ties;
    private int rating;
    private boolean matchmaking;

    public User() {

    }

    public User(FirebaseUser user) {
        this.name = user.getDisplayName();
        this.photoUrl = user.getPhotoUrl().toString();
        wins = 0;
        losses = 0;
        ties = 0;
        rating = 0;
        matchmaking = false;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getTies() {
        return ties;
    }

    public int getRating() {
        return rating;
    }

    public boolean getMatchmaking() {
        return matchmaking;
    }
}
