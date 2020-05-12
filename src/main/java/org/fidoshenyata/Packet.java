package org.fidoshenyata;

public class Packet {

    private byte source;
    private long packetID;
    private int commandType;
    private int userID;
    private String message;

    public Packet(byte source, long packetID, int commandType, int userID, String message){
        this.source = source;
        this.packetID = packetID;
        this.commandType = commandType;
        this.userID = userID;
        this.message = message;
    }

    public byte getSource() {
        return this.source;
    }

    public long getPacketID() {
        return this.packetID;
    }

    public int getCommandType() {
        return this.commandType;
    }

    public int getUserID() {
        return this.userID;
    }

    public String getMessage() {
        return this.message;
    }
}
