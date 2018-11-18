package ghs.message;

import java.util.Objects;
import floodmax.MessageType;

/**
 * Represents the format of a message to be sent in the network.
 */
public abstract class Message implements Comparable  {
    /**
     * ID of the sender process
     */
    private Integer sender;
    private Integer receiver;
    private Integer round;
    private MessageType type;

    public Message(Integer sender, Integer receiver, Integer round) {
        this.sender = sender;
        this.receiver = receiver;
        this.round = round;
    }

    public Message(Integer sender, Integer receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Integer getSender() {
        return sender;
    }

    public void setSender(Integer sender) {
        this.sender = sender;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public Integer getReceiver() {
        return receiver;
    }

    public void setReceiver(Integer receiver) {
        this.receiver = receiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(sender, message.sender) &&
                Objects.equals(receiver, message.receiver) &&
                Objects.equals(round, message.round);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, round);
    }

    @Override
    public int compareTo(Object o) {
        Message m = ((Message) o);
        return this.getRound().compareTo(m.getRound());
    }

    @Override
    public String toString() {
        return "sender=" + sender +
                ", receiver=" + receiver +
                ", round=" + round ;
    }
}
