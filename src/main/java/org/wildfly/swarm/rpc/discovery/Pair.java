package org.wildfly.swarm.rpc.discovery;

import java.io.Serializable;


/**
 * A simple class that holds a pair of values.
 * This may be useful for methods that care to
 * return two values (instead of just one).
 */
public class Pair<E1,E2> implements Serializable {


    private static final long serialVersionUID = 2L;

    private E1 mFirst;
    private E2 mSecond;

    /**
     * Construct a new pair
     *
     * @param first the object to store as the first value
     * @param second the object to store as the second value
     */
    public Pair(E1 first, E2 second) {
        mFirst = first;
        mSecond = second;
    }

    /**
     * Get the first value from the pair.
     *
     * @return the first value
     */
    public E1 first() {
        return mFirst;
    }

    /**
     * Get the second value from the pair.
     *
     * @return the second value
     */
    public E2 second() {
        return mSecond;
    }

    /**
     * Set the first value of the pair.
     *
     * @param first the new first value
     */
    public void setFirst(E1 first) {
        mFirst = first;
    }

    /**
     * Set the second value of the pair.
     *
     * @param second the new second value
     */
    public void setSecond(E2 second) {
        mSecond = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (!mFirst.equals(pair.mFirst)) return false;
        return mSecond.equals(pair.mSecond);

    }

    @Override
    public int hashCode() {
        int result = mFirst.hashCode();
        result = 31 * result + mSecond.hashCode();
        return result;
    }
}
