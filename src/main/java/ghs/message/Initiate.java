package ghs.message;

import asyncGHS.NeighborObject;
import com.sun.org.apache.xml.internal.security.Init;
import floodmax.MessageType;

/**
 * Represents an initiate message, broadcast by the leader of the component to all processes in its component to start
 * searching for the MWOE.
 */
public class Initiate extends Message {
    /**
     * Level of the sender's component
     */
    private Integer level;

    /**
     * Core edge weight of the sender's component
     */
    private Integer coreEdgeWeight;

    private NeighborObject neighborObject;

    /**
     * ID of leader of sender's component
     */
    private Integer leader;

    public Initiate(Integer sender,  Integer receiver, Integer level, Integer coreEdgeWeight, Integer leader) {
        super(sender, receiver);
        this.level = level;
        this.coreEdgeWeight = coreEdgeWeight;
        this.leader = leader;
        this.setType(MessageType.INITIATE);
    }

    public Initiate(Integer sender, Integer receiver, NeighborObject neighborObject, Integer level) {
        super(sender, receiver);
        this.neighborObject = neighborObject;
        this.level = level;
    }
}
