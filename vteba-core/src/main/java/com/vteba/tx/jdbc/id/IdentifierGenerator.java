package com.vteba.tx.jdbc.id;

import java.io.Serializable;

import org.hibernate.HibernateException;

public interface IdentifierGenerator {
    /**
     * The configuration parameter holding the entity name
     */
    public static final String ENTITY_NAME = "entity_name";

    /**
     * The configuration parameter holding the JPA entity name
     */
    public static final String JPA_ENTITY_NAME = "jpa_entity_name";

    /**
     * Generate a new identifier.
     *
     * @param session The session from which the request originates
     * @param object the entity or collection (idbag) for which the id is being generated
     *
     * @return a new identifier
     *
     * @throws HibernateException Indicates trouble generating the identifier
     */
    public Serializable generate(Object session, Object object) throws UUIDException;
}
