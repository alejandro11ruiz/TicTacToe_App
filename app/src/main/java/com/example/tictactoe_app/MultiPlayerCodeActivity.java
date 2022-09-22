package com.example.tictactoe_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MultiPlayerCodeActivity extends AppCompatActivity {

    public static final String PATH_CODES = "Codes";
    public static boolean ISCODEMAKER;
    public static String CODE;
    public static String KEYVALUE;

    private EditText mCodeET;
    private Button mJoinBtn;
    private Button mCreateBtn;
    private ProgressBar mLoadingPB;

    private boolean isCodeMaker=false;
    private String code = "null";
    //private boolean codeFound = false;
    //private boolean checkTemp = false;
    private String keyValue="null";

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_code);

        mCodeET = findViewById(R.id.etCode);
        mJoinBtn = findViewById(R.id.btnJoin);
        mCreateBtn = findViewById(R.id.btnCreate);
        mLoadingPB = findViewById(R.id.pbLoading);

        mJoinBtn.setOnClickListener(view -> {
            code = "null";
            //codeFound = false;
            //checkTemp = true;
            keyValue = "null";
            code = mCodeET.getText().toString();
            CODE = code;
            if (!code.equals("null") && !code.equals("")) {
                mCreateBtn.setVisibility(View.GONE);
                mJoinBtn.setVisibility(View.GONE);
                mCodeET.setVisibility(View.GONE);
                mLoadingPB.setVisibility(View.VISIBLE);
                isCodeMaker = false;
                ISCODEMAKER = false;
                FirebaseDatabase.getInstance().getReference().child(PATH_CODES).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean data = isValueAvailable(snapshot, code);
                        handler.postDelayed(() -> {
                            if (data){
                                //codeFound = true;
                                accepted();
                            }else{
                                mCreateBtn.setVisibility(View.VISIBLE);
                                mJoinBtn.setVisibility(View.VISIBLE);
                                mCodeET.setVisibility(View.VISIBLE);
                                mLoadingPB.setVisibility(View.GONE);
                                Toast.makeText(MultiPlayerCodeActivity.this, "Invalid code", Toast.LENGTH_SHORT).show();
                            }
                        }, 2000);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(MultiPlayerCodeActivity.this, "Please enter a valid code", Toast.LENGTH_SHORT).show();
            }
        });

        mCreateBtn.setOnClickListener(view -> {
            code = "null";
            keyValue="null";
            //codeFound = false;
            //checkTemp = true;
            code = mCodeET.getText().toString();
            CODE = code;
            if (!code.equals("null") && !code.equals("")) {
                mCreateBtn.setVisibility(View.GONE);
                mJoinBtn.setVisibility(View.GONE);
                mCodeET.setVisibility(View.GONE);
                mLoadingPB.setVisibility(View.VISIBLE);
                isCodeMaker = true;
                ISCODEMAKER = true;
                FirebaseDatabase.getInstance().getReference(PATH_CODES).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean check = isValueAvailable(snapshot, code);
                        handler.postDelayed(() -> {
                            if (check) {
                                Toast.makeText(MultiPlayerCodeActivity.this, "This code is already used, please enter another", Toast.LENGTH_SHORT).show();
                                mCreateBtn.setVisibility(View.VISIBLE);
                                mJoinBtn.setVisibility(View.VISIBLE);
                                mCodeET.setVisibility(View.VISIBLE);
                                mLoadingPB.setVisibility(View.GONE);
                            } else {
                                FirebaseDatabase.getInstance().getReference(PATH_CODES).push().setValue(code);
                                //checkTemp = false;
                                handler.postDelayed(() -> {
                                    isValueAvailable(snapshot, code);
                                    accepted();
                                }, 2000);
                            }
                        }, 2000);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(MultiPlayerCodeActivity.this, "Please enter a valid code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void accepted() {
        code = "null";
        keyValue="null";
        isCodeMaker=false;
        mCreateBtn.setVisibility(View.VISIBLE);
        mJoinBtn.setVisibility(View.VISIBLE);
        mCodeET.setVisibility(View.VISIBLE);
        mLoadingPB.setVisibility(View.GONE);
        startActivity(new Intent(this, MultiPlayerOnlineActivity.class));
        //this.mCodeET.setText("");
        //Toast.makeText(MultiPlayerCodeActivity.this, "Please don't go back", Toast.LENGTH_SHORT).show();
    }

    public boolean isValueAvailable(DataSnapshot snapshot, String code) {
        Iterable<DataSnapshot> data = snapshot.getChildren();
        for (DataSnapshot value : data) {
            String val = (String) value.getValue();
            if (val.equals(code)) {
                this.keyValue = value.getKey();
                KEYVALUE=keyValue;
                return true;
            }
        }
        return false;
    }
}