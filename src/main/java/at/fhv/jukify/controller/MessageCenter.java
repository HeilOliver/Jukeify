package at.fhv.jukify.controller;

public class MessageCenter {

    private MessageCenter(){}

    private static MessageCenter instance;

    public static MessageCenter getInstance(){
        if(instance == null){
            instance = new MessageCenter();
        }
        return instance;
    }

    private EMessageType messageType;
    private String messageText;

    public EMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(EMessageType messageType) {
        this.messageType = messageType;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String toString(){
        return messageType.getType() + " - " + messageText;
    }
}
