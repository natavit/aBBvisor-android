package com.abbvisor.abbvisor.template;

public class SingletonTemplate {

    private static SingletonTemplate instance;

    public static  SingletonTemplate getInstance() {
        if (instance == null)
            instance = new SingletonTemplate();
        return instance;
    }

    private SingletonTemplate() {

    }

}
