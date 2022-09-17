package com.example.tictactoe_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MultiPlayerOfflineActivity extends AppCompatActivity {

    static final int DIALOG_DIFFICULTY_ID = 0;
    // Represents the internal state of the game
    private TicTacToeGame mGame;
    // Various text displayed
    private TextView mInfoTextView;
    private TextView mInfoH1W;
    private TextView mInfoT;
    private TextView mInfoH2W;
    private BoardView mBoardView;
    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mWinHMediaPlayer;
    private MediaPlayer mTieMediaPlayer;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mInfoH1W = (TextView) findViewById(R.id.human);
        mInfoT = (TextView) findViewById(R.id.ties);
        mInfoH2W = (TextView) findViewById(R.id.android);

        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        // Restore the scores
        mGame.humanWins = mPrefs.getInt("mHumanWins", 0);
        mGame.androidWins = mPrefs.getInt("mComputerWins", 0);
        mGame.ties = mPrefs.getInt("mTies", 0);

        displayScores();
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
                mGame.restartCount();
                displayScores();
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.humanmove);
        mWinHMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.humanwin);
        mTieMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tie);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mWinHMediaPlayer.release();
        mTieMediaPlayer.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
// Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", mGame.humanWins);
        ed.putInt("mComputerWins", mGame.androidWins);
        ed.putInt("mTies", mGame.ties);
        ed.commit();
    }

    // Set up the game board.
    private void startNewGame() {
        mGame.clearBoard();
        mBoardView.invalidate(); // Redraw the board
        // Who goes first
        if(mGame.startTurn==TicTacToeGame.HUMAN_PLAYER) {
            mInfoTextView.setText(R.string.first_player1);
        }
        else if(mGame.startTurn==TicTacToeGame.COMPUTER_PLAYER){
            mInfoTextView.setText(R.string.first_player2);
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
            mGame.turn = TicTacToeGame.HUMAN_PLAYER;;
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
                    mInfoTextView.setText(R.string.result_player1_wins);
                    mWinHMediaPlayer.start();
                    finishedGame();
                    mInfoH1W.setText("Player 1: "+mGame.humanWins);
                }else {
                    mInfoTextView.setText(R.string.result_player2_wins);
                    finishedGame();
                    mInfoH2W.setText("Player 2: "+mGame.androidWins);
                }
            }
            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    private void displayScores() {
        mInfoH1W.setText("Player 1: "+mGame.humanWins);
        mInfoH2W.setText("Player 2: "+mGame.androidWins);
        mInfoT.setText("Ties: "+mGame.ties);
    }


    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            mHumanMediaPlayer.start(); // Play the sound effect
            mBoardView.invalidate(); // Redraw the board
            return true;
        }
        return false;
    }
}