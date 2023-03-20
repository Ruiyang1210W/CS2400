import java.util.Iterator;

public interface DictionaryInterface <K, V> {
    /**
     * @param key
     * @param value
     * @return
     */
    public V add(K key, V value);

    /**
     * @param key
     * @return
     */
    public V getValue(K key);

    /**
     * @return
     */
    public Iterator<K> getKeyIterator();
}
