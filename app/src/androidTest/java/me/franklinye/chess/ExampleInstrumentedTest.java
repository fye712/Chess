package me.franklinye.chess;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import me.franklinye.chess.game.ChessGame;
import me.franklinye.chess.game.GameMove;
import me.franklinye.chess.game.Position;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("users");
    DatabaseReference chatsRef = database.getReference("chats");
    DatabaseReference gamesRef = database.getReference("games");
    final String userOneKey = "firstUser";
    final String userTwoKey = "secondUser";
    String combinedChatKey = userOneKey + userTwoKey;

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("me.franklinye.chess", appContext.getPackageName());
    }

    @Test
    public void pushUser() throws Exception {

    }

    @Test
    public void createChat() throws Exception {
        final CountDownLatch writeSignal = new CountDownLatch(1);
        final String message = "Creating the room.";
        final String userKey = "secondUser";
        ChatMessage testMessage = new ChatMessage();
        testMessage.message = message;
        testMessage.author = userKey;

        chatsRef.child(combinedChatKey).push().setValue(testMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                writeSignal.countDown();
            }
        });

        writeSignal.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void addMessage() throws Exception {
        final CountDownLatch writeSignal = new CountDownLatch(1);
        final String message = "This is the message I want to push.";
        final String userKey = "firstUser";
        ChatMessage testMessage = new ChatMessage();
        testMessage.message = message;
        testMessage.author = userKey;

        chatsRef.child(combinedChatKey).push().setValue(testMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                writeSignal.countDown();
            }
        });

        writeSignal.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void addChessGame() throws Exception {
        final CountDownLatch writeSignal = new CountDownLatch(1);
        ChessGame chessGame = new ChessGame(userOneKey, userTwoKey);
        // chessGame.init();
        // chessGame.addMove(new GameMove(ChessGame.Side.WHITE, "e4"));
        // chessGame.addMove(new GameMove(ChessGame.Side.BLACK, "e5"));

        String firstGameKey = combinedChatKey + 1;
        gamesRef.child(firstGameKey).setValue(chessGame).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                writeSignal.countDown();
            }
        });

        writeSignal.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void btwobfour() throws Exception {
        final CountDownLatch writeSignal = new CountDownLatch(1);
        ChessGame chessGame = new ChessGame("b2", "b4");
        chessGame.init();
        chessGame.doCommand(ChessGame.Side.WHITE, "b2", "b4");

        String firstGameKey = "b2b4";
        ArrayList<GameMove> moves = new ArrayList<>();
        moves.add(new GameMove(ChessGame.Side.WHITE, new Position(1, 1).toString(), new Position(3, 1).toString()));
        gamesRef.child(firstGameKey).setValue(new GameMove(ChessGame.Side.WHITE, new Position(1, 1).toString(), new Position(3, 1).toString())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                writeSignal.countDown();
            }
        });

        writeSignal.await(10, TimeUnit.SECONDS);
    }
}

