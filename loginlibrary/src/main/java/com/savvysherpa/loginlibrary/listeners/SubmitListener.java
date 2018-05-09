package com.savvysherpa.loginlibrary.listeners;

public interface SubmitListener<T> {
    void Submit(T obj, String username, String password);
}
