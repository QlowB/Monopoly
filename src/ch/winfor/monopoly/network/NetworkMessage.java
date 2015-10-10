package ch.winfor.monopoly.network;

import java.io.Serializable;

/**
 * a message that can be sent to the other players
 * 
 * @author Nicolas Winkler
 * 
 */
public abstract class NetworkMessage implements Serializable {
    /** */
    private static final long serialVersionUID = -7984114155557108935L;

    public NetworkMessage() {
    }
}
