package com.example.tictactoe_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MultiPlayerOnlineActivity extends AppCompatActivity {

    public static final String PATH_DATA = "Data";

    // Represents the internal state of the game
    private TicTacToeGame mGame;
    // Various text displayed
    private TextView mInfoTextView;
    private TextView mInfoY;
    private TextView mInfoT;
    private TextView mInfoH;
    private BoardView mBoardView;
    private MediaPlayer mYouMediaPlayer;
    private MediaPlayer mWinYouMediaPlayer;
    private MediaPlayer mTieMediaPlayer;

    private TextView mTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_online);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mInfoY = (TextView) findViewById(R.id.you);
        mInfoT = (TextView) findViewById(R.id.ties);
        mInfoH = (TextView) findViewById(R.id.he);

        mTest = (TextView) findViewById(R.id.tester);

        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        displayScores();
    }

    // Set up the game board.
    private void startNewGame() {
        mGame.clearBoard();
        mBoardView.invalidate(); // Redraw the board

        // Who goes first
        if(mGame.startTurn==TicTacToeGame.HUMAN_PLAYER) {
            mInfoTextView.setText(R.string.first_you);
        }
        else if(mGame.startTurn==TicTacToeGame.COMPUTER_PLAYER){
            mInfoTextView.setText(R.string.first_he);
        }
    } // End of startNewGame


    private void finishedGame(){
        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
            if (mGame.mBoard[i] == TicTacToeGame.OPEN_SPOT)
                mGame.mBoard[i] = TicTacToeGame.OPEN_SPOT_NE;
        }
        if (mGame.startTurn == TicTacToeGame.HUMAN_PLAYER) {
            mGame.startTurn = TicTacToeGame.COMPUTER_PLAYER;
            mGame.turn = TicTacToeGame.COMPUTER_PLAYER;
        }else{
            mGame.startTurn = TicTacToeGame.HUMAN_PLAYER;
            mGame.turn = TicTacToeGame.HUMAN_PLAYER;
        }
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (mGame.mBoard[pos]==TicTacToeGame.OPEN_SPOT) {
                setMove(mGame.turn, pos);
                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                mGame.counter(winner);
                if (winner == 0) {
                    if(mGame.turn==TicTacToeGame.HUMAN_PLAYER){
                        mGame.turn = TicTacToeGame.COMPUTER_PLAYER;
                        mInfoTextView.setText(R.string.turn_player2);
                    }else{
                        mGame.turn = TicTacToeGame.HUMAN_PLAYER;
                        mInfoTextView.setText(R.string.turn_player1);
                    }
                }else if (winner == 1) {
                    mInfoTextView.setText(R.string.result_tie);
                    mTieMediaPlayer.start();
                    finishedGame();
                    mInfoT.setText("Ties: "+mGame.ties);
                }else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_you_win);
                    mWinYouMediaPlayer.start();
                    finishedGame();
                    mInfoY.setText("You: "+mGame.humanWins);
                }else {
                    mInfoTextView.setText(R.string.result_he_wins);
                    finishedGame();
                    mInfoH.setText("He: "+mGame.androidWins);
                }
            }
            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    private void displayScores() {
        mInfoY.setText("Player 1: "+mGame.humanWins);
        mInfoH.setText("Player 2: "+mGame.androidWins);
        mInfoT.setText("Ties: "+mGame.ties);
        mTest.setText(MultiPlayerCodeActivity.KEY + " " + MultiPlayerCodeActivity.CODE);
    }


    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            mYouMediaPlayer.start(); // Play the sound effect
            mBoardView.invalidate(); // Redraw the board
            return true;
        }
        return false;
    }

    private void removeCode(){
        //if(MultiPlayerCodeActivity.ISCODEMAKER) FirebaseDatabase.getInstance().getReference().child(MultiPlayerCodeActivity.PATH_CODES).child(MultiPlayerCodeActivity.KEYVALUE).removeValue();
    }

    @Override
    public void onBackPressed(){
        removeCode();
        //if(MultiPlayerCodeActivity.ISCODEMAKER) FirebaseDatabase.getInstance().getReference().child(PATH_DATA).child(MultiPlayerCodeActivity.CODE).removeValue();
        MultiPlayerOnlineActivity.this.finish();

    }
}