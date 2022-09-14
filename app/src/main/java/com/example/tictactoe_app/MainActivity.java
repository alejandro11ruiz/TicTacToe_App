package com.example.tictactoe_app;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button mMultiBtn;
    private Button mSingleBtn;
    public boolean singleUser=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMultiBtn=findViewById(R.id.btnMulti);
        mSingleBtn=findViewById(R.id.btnSingle);
        mMultiBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                singleUser=false;
                Intent intent1 = new Intent(v.getContext(), MultiPlayerGameSelectionActivity.class);
                startActivity(intent1);
            }
        });
        mSingleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
            }
        } );
    }

}