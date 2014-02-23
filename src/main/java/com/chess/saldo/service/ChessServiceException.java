package com.chess.saldo.service;

public class ChessServiceException extends Exception {

    public ChessServiceException(String detailMessage) {
        super(detailMessage);
    }

    public ChessServiceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
