package com.bdd.domain;

import javax.validation.Payload;

/**
 *
 */
public class BddPayload {

    public interface MissingParameter extends Payload {

    }

    public interface InvalidParameter extends Payload{

    }

    public interface Group1 {
    }
    public interface Group2 {
    }
}
