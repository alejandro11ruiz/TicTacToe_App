package com.example.tictactoe_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MultiPlayerOnlineActivity extends AppCompatActivity {

    public static final String PATH_DATA = "Data";

    private char IAM = MultiPlayerCodeActivity.ME;
    private char HIS = MultiPlayerCodeActivity.HE;

    // Represents the internal state of the game
    private TicTacToeGame mGameO;
    // Various text displayed
    private TextView mInfoWIA;
    private TextView mInfoTextView;
    private TextView mInfoH1W;
    private TextView mInfoT;
    private TextView mInfoH2W;
    private BoardView mBoardViewO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_online);

        mInfoTextView = findViewById(R.id.informationO);
        mInfoT = (TextView) findViewById(R.id.tiesO);
        mInfoWIA=(TextView) findViewById(R.id.tester);
        if(IAM==TicTacToeGame.HUMAN_PLAYER) {
            mInfoWIA.setText("Creator");
            mInfoH1W = (TextView) findViewById(R.id.youO);
            mInfoH2W = (TextView) findViewById(R.id.heO);
        }else if(IAM==TicTacToeGame.COMPUTER_PLAYER){
            mInfoWIA.setText("Joined");
            mInfoH1W = (TextView) findViewById(R.id.heO);
            mInfoH2W = (TextView) findViewById(R.id.youO);
        }

        FirebaseDatabase.getInstance().getReference(PATH_DATA).child(MultiPlayerCodeActivity.CODE).child(String.valueOf(HIS)).setValue("-1");
        FirebaseDatabase.getInstance().getReference(PATH_DATA).child(MultiPlayerCodeActivity.CODE).child(String.valueOf(IAM)).setValue("-1");

        mGameO = new TicTacToeGame();
        mBoardViewO = findViewById(R.id.boardO);
        mBoardViewO.setGame(mGameO);
        // Listen for touches on the board
        mBoardViewO.setOnTouchListener(mTouchListener);

        displayScores();
        startNewGame();

        FirebaseDatabase.getInstance().getReference(PATH_DATA).child(MultiPlayerCodeActivity.CODE).child(String.valueOf(HIS)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int rivMov = Integer.parseInt(snapshot.getValue().toString());
                if(mGameO.turn==HIS&&rivMov>=0) {
                    setMove(HIS, rivMov);
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/"+ HIS, String.valueOf(-1));
                    FirebaseDatabase.getInstance().getReference(PATH_DATA).child(MultiPlayerCodeActivity.CODE).updateChildren(childUpdates);
                    // If no winner yet, let the computer make a move
                    int winner = mGameO.checkForWinner();
                    mGameO.counter(winner);
                    if(winner == 0){
                        mGameO.toChangeTurn();
                        noticeTurn(mGameO.turn);
                    }else if (winner == 1) {
                        mInfoTextView.setText(R.string.result_tie);
                        finishedGame();
                        mInfoT.setText("Ties: " + mGameO.ties);
                    } else if (winner == 2) {
                        if (IAM == TicTacToeGame.HUMAN_PLAYER) {
                            mInfoTextView.setText(R.string.result_you_win);
                            mInfoH1W.setText("You: " + mGameO.humanWins);
                        } else {
                            mInfoTextView.setText(R.string.result_he_wins);
                            mInfoH1W.setText("He: " + mGameO.humanWins);
                        }
                        finishedGame();
                    } else if (winner == 3) {
                        if (IAM == TicTacToeGame.HUMAN_PLAYER) {
                            mInfoTextView.setText(R.string.result_he_wins);
                            mInfoH2W.setText("He: " + mGameO.androidWins);
                        } else {
                            mInfoTextView.setText(R.string.result_you_win);
                            mInfoH2W.setText("You: " + mGameO.androidWins);
                        }
                        finishedGame();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.reset_scores:
                mGameO.restartCount();
                displayScores();
                return true;
        }
        return false;
    }

    // Set up the game board.
    private void startNewGame() {
        mGameO.clearBoard();
        mBoardViewO.invalidate(); // Redraw the board

        // Who goes first
        if(mGameO.startTurn==IAM) mInfoTextView.setText(R.string.first_you);
        else if (mGameO.startTurn==HIS)mInfoTextView.setText(R.string.first_he);
    } // End of startNewGame


    private void finishedGame(){
        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
            if (mGameO.mBoard[i] == TicTacToeGame.OPEN_SPOT)
                mGameO.mBoard[i] = TicTacToeGame.OPEN_SPOT_NE;
        }
        mGameO.toChangeStartTurn();
        mGameO.setTurn(mGameO.startTurn);
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardViewO.getBoardCellWidth();
            int row = (int) event.getY() / mBoardViewO.getBoardCellHeight();
            int pos = row * 3 + col;

            if (mGameO.turn == IAM) {
                if (mGameO.mBoard[pos] == TicTacToeGame.OPEN_SPOT) {
                    mGameO.setMove(mGameO.turn,pos);

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/"+ IAM, String.valueOf(pos));
                    FirebaseDatabase.getInstance().getReference(PATH_DATA).child(MultiPlayerCodeActivity.CODE).updateChildren(childUpdates);

                    // If no winner yet, let the computer make a move
                    int winner = mGameO.checkForWinner();
                    mGameO.counter(winner);
                    if(winner == 0){
                        mGameO.toChangeTurn();
                        noticeTurn(mGameO.turn);
                    }else if (winner == 1) {
                        mInfoTextView.setText(R.string.result_tie);
                        finishedGame();
                        mInfoT.setText("Ties: " + mGameO.ties);
                    } else if (winner == 2) {
                        if (IAM==TicTacToeGame.HUMAN_PLAYER){
                            mInfoTextView.setText(R.string.result_you_win);
                            mInfoH1W.setText("You: " + mGameO.humanWins);
                        }else{
                            mInfoTextView.setText(R.string.result_he_wins);
                            mInfoH1W.setText("He: " + mGameO.humanWins);
                        }
                        finishedGame();
                    } else if (winner == 3){
                        if (IAM==TicTacToeGame.HUMAN_PLAYER){
                            mInfoTextView.setText(R.string.result_he_wins);
                            mInfoH2W.setText("He: " + mGameO.androidWins);
                        }else{
                            mInfoTextView.setText(R.string.result_you_win);
                            mInfoH2W.setText("You: " + mGameO.androidWins);
                        }
                        finishedGame();
                    }
                }
            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };


    private void displayScores() {
        if(IAM==TicTacToeGame.HUMAN_PLAYER) {
            mInfoH1W.setText("You: "+ mGameO.humanWins);
            mInfoH2W.setText("He: "+ mGameO.androidWins);
            mInfoT.setText("Ties: "+ mGameO.ties);
        }else{
            mInfoH1W.setText("You: "+ mGameO.androidWins);
            mInfoH2W.setText("He: "+ mGameO.humanWins);
            mInfoT.setText("Ties: "+ mGameO.ties);
        }
    }


    private boolean setMove(char player, int location) {
        if (mGameO.setMove(player, location)) {
            //mYouMediaPlayer.start(); // Play the sound effect
            mBoardViewO.invalidate(); // Redraw the board
            return true;
        }
        return false;
    }

    private void noticeTurn(char turn){
        if(turn==IAM) mInfoTextView.setText(R.string.turn_you);
        else mInfoTextView.setText(R.string.turn_he);
    }


}