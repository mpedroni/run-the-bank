package com.mpedroni.runthebank.domain;

public class ValidationError extends IllegalArgumentException {
        public ValidationError(String message) {
            super(message);
        }
}
