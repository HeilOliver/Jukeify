package at.fhv.jukify.controller;

public enum EMessageType {
    OK("OK"), ERROR("Error"), WARNING("Warning");

    private String message;

    EMessageType(String msg){
        message = msg;
    }
    public String getType(){
        return message;
    }
}
