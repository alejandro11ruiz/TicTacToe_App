package com.example.tictactoe_app;

public class Code {

    private String code;
    private String key;
    private String availability;

    public Code (String code, String availability, String key){
        this.code=code;
        this.availability = availability;
        this.key=key;
    }

    public Code (){
        this.code="null";
        this.availability = "null";
        this.key="null";
    }

    public void restartCode (){
        this.code="null";
        this.availability = "null";
        this.key="null";
    }

    public void setCode(String code){
        this.code=code;
    }

    public void setKey(String key){
        this.key=key;
    }

    public void setAvailability(String availability){
        this.availability=availability;
    }

    public String getCode(){
        return this.code;
    }

    public String getKey(){
        return this.key;
    }

    public String getAvailability(){
        return this.availability;
    }

}
