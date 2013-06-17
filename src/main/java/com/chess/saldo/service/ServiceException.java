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

    private final Type type;

    public ServiceException(String message, Type type) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }


}
