package com.agile.common.exception;

import org.springframework.security.authentication.AccountStatusException;

/**
 * Created by 佟盟 on 2018/7/6
 */
public class RepeatAccount extends AccountStatusException {
    public RepeatAccount(String msg) {
        super(msg);
    }

    public RepeatAccount(String msg, Throwable t) {
        super(msg, t);
    }
}
