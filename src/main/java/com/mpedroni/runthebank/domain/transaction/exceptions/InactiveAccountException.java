package com.mpedroni.runthebank.domain.transaction.exceptions;

import com.mpedroni.runthebank.domain.ApplicationException;

public class InactiveAccountException extends ApplicationException {
    public InactiveAccountException(String message) {
        super(message);
    }
}
