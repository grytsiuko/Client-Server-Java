package org.fidoshenyata.model;

public class PacketBuilder {

    private Byte source;
    private Long packetID;
    private Integer commandType;
    private Integer userID;
    private String message;

    public PacketBuilder() {
    }

    public PacketBuilder setSource(Byte source) {
        this.source = source;
        return this;
    }

    public PacketBuilder setPacketID(Long packetID) {
        this.packetID = packetID;
        return this;
    }

    public PacketBuilder setCommandType(Integer commandType) {
        this.commandType = commandType;
        return this;
    }

    public PacketBuilder setUserID(Integer userID) {
        this.userID = userID;
        return this;
    }

    public PacketBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public Packet build() {
        if (this.source == null) {
            throw new IllegalStateException("Source is not defined");
        }
        if (this.packetID == null) {
            throw new IllegalStateException("Packet ID is not defined");
        }
        if (this.commandType == null) {
            throw new IllegalStateException("Command Type is not defined");
        }
        if (this.userID == null) {
            throw new IllegalStateException("User ID is not defined");
        }
        if (this.message == null) {
            throw new IllegalStateException("Message is not defined");
        }
        return new Packet(this.source, this.packetID, this.commandType, this.userID, this.message);
    }
}
