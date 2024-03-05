package org.example.back.exception;

public class ExceededServerRecordThresholdException extends  RuntimeException {
    public ExceededServerRecordThresholdException() {
        super("No more than 10 server records per service are allowed!");
    }
}
