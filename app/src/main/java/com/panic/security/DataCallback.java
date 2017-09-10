package com.panic.security;

/**
 * Created by david on 9/9/17.
 */

public interface DataCallback <T> {
    /**
     * callback for get Firebase entities
     * @param data entity
     */
    void onDataReceive (T data);
}
