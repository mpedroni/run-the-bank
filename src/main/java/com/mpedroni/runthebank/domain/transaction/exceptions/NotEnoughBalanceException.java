package com.mpedroni.runthebank.domain.transaction.exceptions;

import com.mpedroni.runthebank.domain.ApplicationException;

public class NotEnoughBalanceException extends ApplicationException {
        public NotEnoughBalanceException(String message) {
            super(message);
        }
}
