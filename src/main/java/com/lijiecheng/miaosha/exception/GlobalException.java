package com.lijiecheng.miaosha.exception;

import com.lijiecheng.miaosha.result.CodeMsg;

/**
 * @Author: myname
 * @Date: 2019/2/21 3:05
 */
public class GlobalException extends RuntimeException{
    private CodeMsg cm;
    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }
}
