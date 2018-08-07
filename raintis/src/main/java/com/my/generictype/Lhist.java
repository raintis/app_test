package com.my.generictype;

public class Lhist<V> {
    private V[] array;
    private int size;

    public Lhist(int capacity) {
        array = (V[]) new Object[capacity];
    }

    public void add(V value) {
        if (size == array.length) {
			throw new IndexOutOfBoundsException(Integer.toString(size));
		} else if (value == null) {
			throw new NullPointerException();
		}
        array[size++] = value;
    }

    public void remove(V value) {
        int removalCount = 0;  
        for (int i=0; i<size; i++) {
            if (array[i].equals(value)) {
				++removalCount;
			} else if (removalCount > 0) {
                array[i-removalCount] = array[i];
                array[i] = null;
            }
        }
        size -= removalCount;
    }

    public <Y> Y iforElse(boolean b ,Y t1,Y t2){
    	return b ? t1 : t2;
    }
    
    public int size() { return size; }

    public V get(int i) {
        if (i >= size) {
			throw new IndexOutOfBoundsException(Integer.toString(i));
		}
        return array[i];
    }
    
    public static void main(String[] args) {
		Lhist<String> l = new Lhist<>(5);
		l.add("0");
		l.add("1");
		l.add("2");
		l.add("3");
		l.add("4");
		l.remove("2");
		int i = 0;
		while(i < l.size()){
			System.out.println(l.get(i));
			i++;
		}
		
		System.out.println(l.iforElse(true, "123", "456"));
		System.out.println(Runtime.getRuntime().freeMemory()/1024/1024);
	}
}
