package ru.akkuzin.vkr.backendVKR.util;

public class PersonNotCreatedException  extends RuntimeException{
    public PersonNotCreatedException(String msg) {
        super(msg);
    }
}