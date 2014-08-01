package com.vteba.tx.jdbc.uuid;

import java.util.UUID;

/**
 * Implements a "random" UUID generation strategy as defined by the {@link UUID#randomUUID()} method.
 *
 * @author Steve Ebersole
 */
public class StandardRandomStrategy implements UUIDGenerationStrategy {

    private static final long serialVersionUID = -7996885289753929772L;
    public static final StandardRandomStrategy INSTANCE = new StandardRandomStrategy();

    /**
     * A variant 4 (random) strategy
     */
    @Override
    public int getGeneratedVersion() {
        // a "random" strategy
        return 4;
    }

    /**
     * Delegates to {@link UUID#randomUUID()}
     */
    @Override
    public UUID generateUUID(Object session) {
        return UUID.randomUUID();
    }
}

