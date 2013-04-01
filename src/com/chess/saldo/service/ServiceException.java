/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chess.saldo.service;

/**
 *
 * @author Bjorncs
 */
public class ServiceException extends Exception {

    public enum Type {LoginProblem, ParseProblem}

    private String htmlText;
    private final Type type;

    public ServiceException(String message, Type type) {
        super(message);
        this.type = type;
    }

    public ServiceException(String message, Type type, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public String getHtmlText() {
        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    public Type getType() {
        return type;
    }


}
