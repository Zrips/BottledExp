package me.Zrips.bottledexp.commands;

import java.lang.reflect.Method;

class BottleCMD {
    private Method m;

    public BottleCMD(Method m) {
        this.m = m;
    }

    public CAnnotation getAnnotation() {
        return m.getAnnotation(CAnnotation.class);
    }

    public Method getMethod() {
        return m;
    }
}