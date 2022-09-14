package com.example.tictactoe_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class MultiPlayerGameSelectionActivity extends AppCompatActivity {

    private Button mOnlineBtn;
    private Button mOfflineBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_game_selection);

        mOnlineBtn=findViewById(R.id.btnOnline);
        mOfflineBtn=findViewById(R.id.btnOffline);
        mOnlineBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
            }
        });
        mOfflineBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
            }
        } );
    }
}