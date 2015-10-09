package ch.winfor.monopoly.game;

/**
 * interface that provides a hash function returning a 64 bit hash value
 * 
 * This hash function can be used to synchronize different objects (e.g. in
 * networking).
 * 
 * @author Nicolas Winkler
 * 
 */
public interface Hashable {
	/**
	 * creates a value which should represent the objects status
	 * 
	 * There should be no collisions i.e. if two objects have the same hash
	 * value, they should represent the same object and
	 * {@link Object#equals(Object)} should return <code>true</code>
	 * 
	 * @return a 64 bit hash value
	 */
	long createHash();
}
