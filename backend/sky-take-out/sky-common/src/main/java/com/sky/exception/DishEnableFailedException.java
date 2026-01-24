package com.sky.exception;

/**
 * 菜品起售停售异常
 */
public class DishEnableFailedException extends BaseException {

    public DishEnableFailedException() {
    }

    public DishEnableFailedException(String msg) {
        super(msg);
    }

}
