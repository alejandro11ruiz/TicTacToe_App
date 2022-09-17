package com.example.tictactoe_app;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    static final int DIALOG_STARTER = 2;
    private Button mMultiBtn;
    private Button mSingleBtn;
    public boolean singleUser=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showDialog(DIALOG_STARTER);

        mMultiBtn=findViewById(R.id.btnMulti);
        mSingleBtn=findViewById(R.id.btnSingle);
        mMultiBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                singleUser=false;
                startActivity(new Intent(v.getContext(), MultiPlayerGameSelectionActivity.class));
            }
        });
        mSingleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                singleUser=true;
                startActivity(new Intent(v.getContext(), SinglePlayerActivity.class));
            }
        } );
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
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

}