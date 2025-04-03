package com.example.abstractinterface;

public abstract class AImplementingA implements A{

    public abstract void aDoingWork();

    @Override
    public void doSomeWork() {
        System.out.println("Class Doing Work");
    }

    @Override
    public void doAnotherWork() {
        System.out.println("Doing Work");
    }
}
