import java.util.Iterator;
import java.util.NoSuchElementException;


public class HashedDictionary<K, V> implements DictionaryInterface<K, V>
{
    //The dictionary
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 5;
    private static final int MAX_CAPACITY = 10000;

    //the hash table:
    private Entry <K,V>[] hashTable;
    private int tableSize;
    private static final int MAX_SIZE = 2 * MAX_CAPACITY;
    private boolean integrityOK = false;
    private int collisionCount;
        private static final double MAX_LOAD_FACTOR = 0.5;
        //Fraction of hash table that can be filled
        protected final Entry<K,V> AVAILABLE = new Entry<>(null, null);

        public HashedDictionary()
        {
            this(DEFAULT_CAPACITY);
        }

        public HashedDictionary(int initialCapacity)
        {
            initialCapacity = checkCapacity(initialCapacity);
            numberOfEntries = 0;
            collisionCount = 0;

            int tableSize = getNextPrime(initialCapacity);
            checkSize(tableSize);

            @SuppressWarnings("unchecked")
            Entry<K,V>[] temp = (Entry<K, V>[])new Entry[tableSize];
            hashTable = temp;
            integrityOK = true;
        }

    protected final class Entry<S, T> {
        private S key;
        private T value;
        private Entry<S, T> next;

        private Entry(S searchKey, T dataValue){
            key = searchKey;
            value = dataValue;
        }

        private S getKey(){
            return key;
        }

        private T getValue(){
            return value;
        }

        private Entry<S, T> getNextEntry(){
            return next;
        }

        @SuppressWarnings("unused")
        private void setValue(T newValue){
            value = newValue;
        }
    }

    private void enlargeHashTable(){
     Entry<K, V>[] oldTable = hashTable;
     int oldSize = hashTable.length;
     int newSize = getNextPrime(oldSize + oldSize);
     checkSize(newSize);

     @SuppressWarnings("unchecked")
     Entry<K, V>[]temp = (Entry<K, V>[])new Entry[newSize];
     hashTable = temp;
     numberOfEntries = 0;

     for(int index = 0; index < oldSize; index++){
        if((oldTable[index] != null) && oldTable[index] != AVAILABLE)
            add(oldTable[index].getKey(), oldTable[index].getValue());
     }
        
    }
    
    @Override
    public V add(K key, V value) {
        if((key == null)||(value == null))
            throw new IllegalArgumentException("Key or Value is null");
        int index = getHashIndex(key);
        V oldValue = null;
        Entry<K, V> currentEntry = hashTable[index];
        while(currentEntry != null && !key.equals(currentEntry.getKey())){
            currentEntry = currentEntry.getNextEntry();
            collisionCount++;
        }
        if(currentEntry == null)
        {
            hashTable[index] = new Entry<>(key, value);
            numberOfEntries++;
            oldValue = null;
        }else{
            oldValue = hashTable[index].getValue();
            hashTable[index].setValue(value);
        }
        if(numberOfEntries > MAX_LOAD_FACTOR * tableSize){
            enlargeHashTable();
        }
        return oldValue;
      
    }

    @Override
    public V getValue(K key) {
      V result = null;

      int index = getHashIndex(key);
      if((hashTable[index] != null) && (hashTable[index] != AVAILABLE))
        result = hashTable[index].getValue();
        return result;

    }

    @Override
    public Iterator<K> getKeyIterator() {
        return new KeyIterator();
    }

    public int getCollisionCount(){
        return collisionCount;
    }


    private class KeyIterator implements Iterator<K>{
        private int currentIndex;
        private int numberLeft;

        private KeyIterator(){
            currentIndex = 0;
            numberLeft = numberOfEntries;
        }

        public boolean hasNext(){
            return numberLeft > 0;
        }
        
        public K next(){
          K result = null;

          if(hasNext()){
            while((hashTable[currentIndex] == null)||(hashTable[currentIndex] == AVAILABLE)){
                currentIndex++;
            }
            result = hashTable[currentIndex].getKey();
            numberLeft--;
            currentIndex++;
          }else{
            throw new NoSuchElementException();
          }
          return result;
        }

        public void remove(){
            throw new UnsupportedOperationException("Remove operation is not supported.");
        }
    }

    /**
     * @param key
     * @return
     */
    private int getHashIndex(K key){
        int hashIndex = key.hashCode() % tableSize;
        if(hashIndex < 0){
            hashIndex = hashIndex + tableSize;
        }
        return hashIndex;
    }

    private void checkSize(int size){
        if(size < DEFAULT_CAPACITY )
            throw new IllegalArgumentException("Hash table Too small");
        if(size > MAX_SIZE)
            throw new IllegalArgumentException("Hash table Too large");
    }

    private int checkCapacity(int capacity){
        if(capacity < numberOfEntries){
            throw new IllegalArgumentException("capacity too small");
        }
        if(capacity < DEFAULT_CAPACITY){
            return DEFAULT_CAPACITY;
        }
        if(! isPrime(capacity)){
            return getNextPrime(capacity);
        }
        return capacity;
    }

    private int getNextPrime(int value){
        while(! isPrime(value)){
            value++;
        }
        return value;
    }

    /**
     * @param value
     * @return
     */
    private boolean isPrime(int value) {
        if(value < 2){
            return false;
        }
        for(int i = 2; i <= Math.sqrt(value); i++){
            if(value % i == 0){
                return false;
            }
        }
        return true;
    }
    
}
