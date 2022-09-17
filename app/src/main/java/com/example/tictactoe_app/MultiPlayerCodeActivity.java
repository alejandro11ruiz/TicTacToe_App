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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MultiPlayerCodeActivity extends AppCompatActivity {

    private EditText mCodeET;
    private Button mJoinBtn;
    private Button mCreateBtn;
    private ProgressBar mLoadingPB;

    private boolean isCodeMaker = true;
    private String code = "null";
    private boolean codeFound = false;
    private boolean checkTemp = false;
    private String keyValue = "null";

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player_code);

        mCodeET=findViewById(R.id.etCode);
        mJoinBtn=findViewById(R.id.btnJoin);
        mCreateBtn=findViewById(R.id.btnCreate);
        mLoadingPB=findViewById(R.id.pbLoading);

        mJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code="null";
                codeFound=false;
                checkTemp=true;
                keyValue="null";
                code=mCodeET.getText().toString();
                mCreateBtn.setVisibility(View.GONE);
                mJoinBtn.setVisibility(View.GONE);
                mCodeET.setVisibility(View.GONE);
                mLoadingPB.setVisibility(View.VISIBLE);
                if(code!="null" && code!=""){
                    isCodeMaker=true;
                    FirebaseDatabase.getInstance().getReference("codes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean check= isValueAvailable(snapshot,code);
                            handler.postDelayed(() -> {
                                if(check==true){
                                    mCreateBtn.setVisibility(View.VISIBLE);
                                    mJoinBtn.setVisibility(View.VISIBLE);
                                    mCodeET.setVisibility(View.VISIBLE);
                                    mLoadingPB.setVisibility(View.GONE);
                                }else{
                                    FirebaseDatabase.getInstance().getReference("codes").push().setValue(code);
                                    isValueAvailable(snapshot, code);
                                    checkTemp=false;
                                    handler.postDelayed(() -> {
                                        accepted();
                                        Toast.makeText(MultiPlayerCodeActivity.this, "Please don't go back", Toast.LENGTH_SHORT).show();
                                    }, 300);
                                }
                            }, 2000);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    mCreateBtn.setVisibility(View.VISIBLE);
                    mJoinBtn.setVisibility(View.VISIBLE);
                    mCodeET.setVisibility(View.VISIBLE);
                    mLoadingPB.setVisibility(View.GONE);
                    Toast.makeText(MultiPlayerCodeActivity.this, "Please enter a valid code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = "null";
                codeFound = false;
                checkTemp = true;
                keyValue = "null";
                code = mCodeET.getText().toString();
                if(code!="null" && code!=""){
                    mCreateBtn.setVisibility(View.GONE);
                    mJoinBtn.setVisibility(View.GONE);
                    mCodeET.setVisibility(View.GONE);
                    mLoadingPB.setVisibility(View.VISIBLE);
                    isCodeMaker = false;
                    FirebaseDatabase.getInstance().getReference().child("codes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean data = isValueAvailable(snapshot, code);
                            handler.postDelayed(() -> {
                                if(data==true){
                                    codeFound = true;
                                    accepted();
                                    mCreateBtn.setVisibility(View.VISIBLE);
                                    mJoinBtn.setVisibility(View.VISIBLE);
                                    mCodeET.setVisibility(View.VISIBLE);
                                    mLoadingPB.setVisibility(View.GONE);
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
                }else{
                    Toast.makeText(MultiPlayerCodeActivity.this, "Please enter a valid code", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    public void accepted(){
        startActivity(new Intent(this, MultiPlayerOnlineActivity.class));
        mCreateBtn.setVisibility(View.VISIBLE);
        mJoinBtn.setVisibility(View.VISIBLE);
        mCodeET.setVisibility(View.VISIBLE);
        mLoadingPB.setVisibility(View.GONE);
    }

    public boolean isValueAvailable(DataSnapshot snapshot, String code){
        Iterable<DataSnapshot> data = snapshot.getChildren();
        for(DataSnapshot value : data){
            String val = value.getValue().toString();
            if(val==code){
                keyValue=value.getKey().toString();
                return true;
            }
        }
        return false;
    }
}