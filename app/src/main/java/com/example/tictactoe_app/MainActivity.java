package com.example.tictactoe_app;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Dialog;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_STARTER = 2;
    // Represents the internal state of the game
    private TicTacToeGame mGame;
    // Various text displayed
    private TextView mInfoTextView;
    private TextView mInfoHW;
    private TextView mInfoT;
    private TextView mInfoAW;
    private BoardView mBoardView;
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mWinHMediaPlayer;
    MediaPlayer mWinAMediaPlayer;
    MediaPlayer mTieMediaPlayer;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mInfoHW = (TextView) findViewById(R.id.human);
        mInfoT = (TextView) findViewById(R.id.ties);
        mInfoAW = (TextView) findViewById(R.id.android);

        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        showDialog(DIALOG_STARTER);
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        startNewGame();
    }

    // Set up the game board.
    private void startNewGame() {
        mGame.clearBoard();
        mBoardView.invalidate(); // Redraw the board
        // Who goes first
        if(mGame.startTurn==TicTacToeGame.HUMAN_PLAYER) {
            mInfoTextView.setText(R.string.first_human);
        }
        else if(mGame.startTurn==TicTacToeGame.COMPUTER_PLAYER){
            mInfoTextView.setText(R.string.first_android);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            mInfoTextView.setText(R.string.turn_human);
            mGame.turn=TicTacToeGame.HUMAN_PLAYER;
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

            if (mGame.mBoard[pos]==TicTacToeGame.OPEN_SPOT && mGame.turn==TicTacToeGame.HUMAN_PLAYER) {
                setMove(TicTacToeGame.HUMAN_PLAYER, pos);
                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mGame.turn=TicTacToeGame.COMPUTER_PLAYER;
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    mInfoTextView.setText(R.string.turn_human);
                    winner = mGame.checkForWinner();
                }
                mGame.counter(winner);
                if (winner == 0) {
                    mGame.turn = TicTacToeGame.HUMAN_PLAYER;
                    mInfoTextView.setText(R.string.turn_human);
                }else if (winner == 1) {
                    mInfoTextView.setText(R.string.result_tie);
                    mTieMediaPlayer.start();
                    finishedGame();
                    mInfoT.setText("Ties: "+mGame.ties);
                }else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_human_wins);
                    mWinHMediaPlayer.start();
                    finishedGame();
                    mInfoHW.setText("Human: "+mGame.humanWins);
                }else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                    mWinAMediaPlayer.start();
                    finishedGame();
                    mInfoAW.setText("Android: "+mGame.androidWins);
                }
            }
            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };


    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            if(player==TicTacToeGame.HUMAN_PLAYER) mHumanMediaPlayer.start(); // Play the sound effect
            mBoardView.invalidate(); // Redraw the board
            return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
        }
        return false;
    }


    @Override
    protected Dialog onCreateDialog(int id) {

        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};
                // TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
                int selected =2;
                // selected is the radio button that should be selected.
                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog
                                // TODO: Set the diff level of mGame based on which item was selected.
                                if(levels[item].equals(getResources().getString(R.string.difficulty_easy)))
                                    mGame.mDifficultyLevel = TicTacToeGame.DifficultyLevel.Easy;
                                else if(levels[item].equals(getResources().getString(R.string.difficulty_harder)))
                                    mGame.mDifficultyLevel = TicTacToeGame.DifficultyLevel.Harder;
                                else if(levels[item].equals(getResources().getString(R.string.difficulty_expert)))
                                    mGame.mDifficultyLevel = TicTacToeGame.DifficultyLevel.Expert;
                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],

                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                dialog = builder.create();
                break;
            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
            case DIALOG_STARTER:
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.humanmove);
        mWinHMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.humanwin);
        mWinAMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.androidwin);
        mTieMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tie);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mWinHMediaPlayer.release();
        mWinAMediaPlayer.release();
        mTieMediaPlayer.release();
    }
}