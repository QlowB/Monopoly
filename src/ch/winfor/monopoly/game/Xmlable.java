package ch.winfor.monopoly.game;

/**
 * interface for game objects that can be saved to an xml string
 * 
 * @author Nicolas Winkler
 * 
 */
public interface Xmlable {
    /**
     * creates an xml string which stores all necessary information about the
     * game object
     * 
     * This xml string should
     * 
     * @return the xml string
     */
    public String createXml();

    /**
     * static class with indentation functions
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class Indenter {
        /**
         * indents a xml string
         * 
         * @param xml
         *            the xml string
         * @param indent
         *            the indent string (what is put at the beginning of each
         *            line)
         * @return the new indented string
         */
        public static String indent(String xml, String indent) {
            return xml.replaceAll("\\n", "\\n" + indent);
        }
    }
}
