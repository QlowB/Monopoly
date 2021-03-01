package ch.winfor.monopoly.network;

/**
 * base message for any request message
 * 
 * @author Nicolas Winkler
 * 
 */
public abstract class RequestMessage extends NetworkMessage {
    /** */
    private static final long serialVersionUID = 8063835642005334548L;

    /**
     * requests the whole game
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class RequestFullGame extends RequestMessage {
        /** */
        private static final long serialVersionUID = -5513755029742133581L;
    }
}
