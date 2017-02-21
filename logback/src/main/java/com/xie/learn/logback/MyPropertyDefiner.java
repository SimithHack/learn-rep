package com.xie.learn.logback;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.PropertyDefiner;
import ch.qos.logback.core.status.Status;

/**
 * 需要实现PropertyDefiner接口
 */
public class MyPropertyDefiner implements PropertyDefiner {
    /**
     * 可以自定义变量
     */
    private int age;
    /**
     * 这个方法被自动调用，用来设置<define name='varname'></define>中varname的值
     * @return
     */
    @Override
    public String getPropertyValue() {
        //这里边可以写逻辑了
        if(age>20){
            return "ERROR";
        }
        return "INFO";
    }

    @Override
    public void setContext(Context context) {

    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void addStatus(Status status) {

    }

    @Override
    public void addInfo(String s) {

    }

    @Override
    public void addInfo(String s, Throwable throwable) {

    }

    @Override
    public void addWarn(String s) {

    }

    @Override
    public void addWarn(String s, Throwable throwable) {

    }

    @Override
    public void addError(String s) {

    }

    @Override
    public void addError(String s, Throwable throwable) {

    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}