package com.example.tictactoe_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MultiPlayerCodeActivity extends AppCompatActivity {

    public static final String PATH_CODES = "Codes";
    public static final String AVAILABLE = "AVAILABLE";
    public static final String UNAVAILABLE = "UNAVAILABLE";
    public static final char CREATOR = 'C';
    public static final char JOINED = 'J';

    public Code mCode = new Code();

    static String CODE;
    static String KEY;
    static char ME;
    static char HE;

    private EditText mCodeET;
    private Button mJoinBtn;
    private Button mCreateBtn;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_code);

        mCodeET = findViewById(R.id.etCode);
        mJoinBtn = findViewById(R.id.btnJoin);
        mCreateBtn = findViewById(R.id.btnCreate);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mCodeET.getText().toString().equals("null")&&!mCodeET.getText().toString().equals("")) {
                    FirebaseDatabase.getInstance().getReference(PATH_CODES).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean check = codeAlreadyExists(snapshot, mCodeET.getText().toString().trim());
                            if(check){
                                Toast.makeText(MultiPlayerCodeActivity.this, "This code already exist", Toast.LENGTH_SHORT).show();
                            }else{
                                mCode.setCode(mCodeET.getText().toString().trim());
                                mCode.setAvailability(AVAILABLE);
                                mCode.setKey(FirebaseDatabase.getInstance().getReference(PATH_CODES).push().getKey());
                                FirebaseDatabase.getInstance().getReference(PATH_CODES).child(mCode.getKey()).setValue(mCode);
                                Toast.makeText(MultiPlayerCodeActivity.this, "Code created successfully", Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance().getReference(PATH_CODES).child(mCode.getKey()).child("availability").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String disp = snapshot.getValue().toString();
                                        if(disp.equals(UNAVAILABLE)){
                                            Toast.makeText(MultiPlayerCodeActivity.this, "We have a rival", Toast.LENGTH_SHORT).show();
                                            accepted(CREATOR);
                                        }else {
                                            Toast.makeText(MultiPlayerCodeActivity.this, "Waiting for a rival", Toast.LENGTH_SHORT).show();
                                            mJoinBtn.setEnabled(false);
                                            mCreateBtn.setEnabled(false);
                                            mCodeET.setEnabled(false);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        mJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mCodeET.getText().toString().equals("null")&&!mCodeET.getText().toString().equals("")){
                    FirebaseDatabase.getInstance().getReference(PATH_CODES).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean check = codeAlreadyExists(snapshot, mCodeET.getText().toString().trim());
                            if(check){
                                FirebaseDatabase.getInstance().getReference(PATH_CODES).child(mCode.getKey()).child("availability").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String disp = snapshot.getValue().toString();
                                        if(disp.equals(AVAILABLE)){
                                            mCode.setCode(mCodeET.getText().toString().trim());
                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put("/availability" , UNAVAILABLE);
                                            FirebaseDatabase.getInstance().getReference(PATH_CODES).child(mCode.getKey()).updateChildren(childUpdates);
                                            Toast.makeText(MultiPlayerCodeActivity.this, "Code added successfully", Toast.LENGTH_SHORT).show();
                                            accepted(JOINED);
                                        }else {
                                            Toast.makeText(MultiPlayerCodeActivity.this, "This code is no longer available", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }else{
                                Toast.makeText(MultiPlayerCodeActivity.this, "This code does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    public void accepted(char w) {
        CODE=mCode.getCode();
        KEY=mCode.getKey();
        if(w==MultiPlayerCodeActivity.CREATOR){
            ME=TicTacToeGame.HUMAN_PLAYER;
            HE=TicTacToeGame.COMPUTER_PLAYER;
        }else if(w==MultiPlayerCodeActivity.JOINED){
            ME=TicTacToeGame.COMPUTER_PLAYER;
            HE=TicTacToeGame.HUMAN_PLAYER;
        }
        //handler.postDelayed(new Runnable() {
            //@Override
            //public void run() {
                startActivity(new Intent(this, MultiPlayerOnlineActivity.class));
            //}
        //},2000);
    }

    public boolean codeAlreadyExists(DataSnapshot snapshot, String code) {
        Iterable<DataSnapshot> data = snapshot.getChildren();
        for (DataSnapshot value : data) {
            String val = (String) value.child("code").getValue();
            if (val.equals(code)) {
                this.mCode.setKey(value.getKey());
                return true;
            }
        }
        return false;
    }

}