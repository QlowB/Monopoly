package ch.winfor.monopoly;

/**
 * interface for classes that need to be notified if they aren't used anymore,
 * so they can delete allocated resources
 * 
 * @author Nicolas Winkler
 * 
 */
public interface Freeable {
    /**
     * invoked when the object can delete it's ressources
     */
    public void free();
}
