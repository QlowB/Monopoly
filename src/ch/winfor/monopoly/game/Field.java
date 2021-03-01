package ch.winfor.monopoly.game;

import java.io.Serializable;

/**
 * base class for every field on the board
 * 
 * @author Nicolas Winkler
 * 
 */
public class Field implements Hashable, Serializable {
    /** */
    private static final long serialVersionUID = 1978420592817071142L;

    /** the caption of this field */
    protected String name;

    public Field(String name) {
        this.name = name;
    }

    /**
     * gets the name of this field
     * 
     * @return the name of this field
     */
    public String getName() {
        return name;
    }

    /**
     * a field in the corner (start, jail, free parking, go to jail)
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class CornerField extends Field {
        /** */
        private static final long serialVersionUID = 8480105357897766414L;

        public CornerField(String name) {
            super(name);
        }
    }

    /**
     * the start field
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class StartField extends CornerField {
        /** */
        private static final long serialVersionUID = 7485978013116830597L;

        /** money received by the player when he passes this field */
        private long passMoney;

        /** money received by the player when he lands on this field */
        private long visitMoney;

        public StartField(String name) {
            super(name);
        }

        /**
         * @return the amount of money received when passing this field
         */
        public long getPassMoney() {
            return passMoney;
        }

        /**
         * @param passMoney
         *            the amount of money received when passing this field
         */
        public void setPassMoney(long passMoney) {
            this.passMoney = passMoney;
        }

        /**
         * @return the amount of money received when passing this field
         */
        public long getVisitMoney() {
            return visitMoney;
        }

        /**
         * @param visitMoney
         *            the amount of money received when landing on this field
         */
        public void setVisitMoney(long visitMoney) {
            this.visitMoney = visitMoney;
        }

        /*
         * (non-Javadoc)
         * 
         * @see ch.winfor.monopoly.game.Field#createHash()
         */
        public long createHash() {
            return super.createHash() ^ visitMoney * 31 ^ passMoney * 59;
        }
    }

    /**
     * the jail field
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class JailField extends CornerField {
        /** */
        private static final long serialVersionUID = 7045795860494893929L;

        /** duration of the standard stay in jail */
        public static final int STANDARD_STAY = 3;

        public JailField(String name) {
            super(name);
        }
    }

    /**
     * the free parking field
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class FreeParkingField extends CornerField {
        /** */
        private static final long serialVersionUID = -539085813439304258L;

        public FreeParkingField(String name) {
            super(name);
        }
    }

    /**
     * the go to jail field
     * 
     * @author Nicolas Winkler
     * 
     */
    public static class GoToJailField extends CornerField {
        /** */
        private static final long serialVersionUID = -2905189057610780610L;

        public GoToJailField(String name) {
            super(name);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.winfor.monopoly.game.Hashable#createHash()
     */
    public long createHash() {
        return name.hashCode();
    }
}
