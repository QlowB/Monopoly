package ch.winfor.monopoly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Dictionary class to translate the user interface to other languages
 * 
 * @author Nicolas Winkler
 * 
 */
public class Language {
    /** contains all the translations */
    private HashMap<String, String> dictionary;

    /** list of subscribers to this language */
    private List<LanguageListener> languageListeners;

    /** name of this language */
    private String name;

    /** the current language */
    private static Language instance;

    /**
     * creates an empty language dictionary
     */
    private Language() {
        dictionary = new HashMap<String, String>();
        languageListeners = new ArrayList<LanguageListener>();
    }

    /**
     * gets the only instance of this class in the program
     * 
     * @return the {@link Language} instance
     */
    public static Language getInstance() {
        if (instance == null) {
            instance = new Language();
        }
        return instance;
    }

    /**
     * @return the name of this language
     */
    public String getName() {
        return name;
    }

    /**
     * gets the translation of a word
     * 
     * 
     * @param word
     *            the word to translate
     * @return the specified word in the translated language if it's a known
     *         word, or the old word, if the translation doesn't contain the
     *         word
     */
    public String get(String word) {
        String translation = dictionary.get(word);
        if (translation == null)
            translation = word;
        return translation;
    }

    /**
     * sets this dictionary to a specific language
     * 
     * @param languageFile
     *            a stream resource to the file containing the language data;
     *            This language file should consist of different lines, each
     *            wrapping one translation (indicated by the '=' character).
     *            Comments are allowed after a double slash '//'. The file
     *            should be UTF-8 encoded.
     */
    public void loadLanguage(InputStream languageFile) {
        dictionary.clear();
        InputStreamReader isr;
        try {
            isr = new InputStreamReader(languageFile, "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        BufferedReader reader = new BufferedReader(isr);
        String line = "";
        try {
            while ((line = reader.readLine()) != null) {

                if (!line.trim().startsWith("//")) { // if it's a comment line
                    int eqIndex = line.indexOf('=');
                    if (eqIndex > 0) {
                        String leftHand = line.substring(0, eqIndex).trim()
                                .replaceAll("\\\\s", " ");
                        String rightHand = line.substring(eqIndex + 1).trim()
                                .replaceAll("\\\\s", " ");

                        if (leftHand.startsWith("!")) {
                            setProperty(leftHand.substring(1), rightHand);
                        } else if (!leftHand.isEmpty()) {
                            dictionary.put(leftHand, rightHand);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        fireLanguageChanged();
    }

    /**
     * sets a property of this language
     * 
     * @param property
     *            the name of the property to set
     * @param value
     *            the value of the property
     */
    private void setProperty(String property, String value) {
        if (property.equals("language_name")) {
            this.name = value;
        }
    }

    /**
     * adds a subscriber to this language
     * 
     * @param ll
     *            the new subscriber
     */
    public void addLanguageListener(LanguageListener ll) {
        languageListeners.add(ll);
    }

    /**
     * removes a subscriber from this language
     * 
     * @param ll
     *            the subscriber
     */
    public void removeLanguageListener(LanguageListener ll) {
        languageListeners.remove(ll);
    }

    /**
     * sends a language changed message to all subscribers (invokes
     * {@link LanguageListener#languageChanged(Language)} for all subscribers)
     * 
     * @see LanguageListener#languageChanged(Language)
     */
    protected void fireLanguageChanged() {
        for (LanguageListener ll : languageListeners) {
            ll.languageChanged(this);
        }
    }

    /**
     * listener interface to catch events when the language was changed
     * 
     * @author Nicolas Winkler
     * 
     */
    public static interface LanguageListener {
        /**
         * invoked whenever the main language of the program has changed
         * 
         * @param sender
         *            the language that was changed
         */
        public void languageChanged(Language sender);
    }
}
