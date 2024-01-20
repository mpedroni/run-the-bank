package com.mpedroni.runthebank.domain;

public class ValidationError extends ApplicationException {
        public ValidationError(String message) {
            super(message);
        }
}
