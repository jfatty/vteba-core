package com.vteba.tx.jdbc.id;

import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;

import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;

public class UUIDGenerator implements IdentifierGenerator {
    public static final String UUID_GEN_STRATEGY = "uuid_gen_strategy";
    public static final String UUID_GEN_STRATEGY_CLASS = "uuid_gen_strategy_class";

    private UUIDGenerationStrategy strategy;
    private UUIDTypeDescriptor.ValueTransformer valueTransformer;

    public static UUIDGenerator buildUniqueIdentifierGenerator() {
        final UUIDGenerator generator = new UUIDGenerator();
        generator.strategy = StandardRandomStrategy.INSTANCE;
        generator.valueTransformer = UUIDTypeDescriptor.ToStringTransformer.INSTANCE;
        return generator;
    }

    public void configure(Class<?> type, Properties params) {
        // check first for the strategy instance
        strategy = (UUIDGenerationStrategy) params.get( UUID_GEN_STRATEGY );
        if ( strategy == null ) {
            // next check for the strategy class
            final String strategyClassName = params.getProperty( UUID_GEN_STRATEGY_CLASS );
            if ( strategyClassName != null ) {
                try {
                    final Class<?> strategyClass = Class.forName(strategyClassName );
                    try {
                        strategy = (UUIDGenerationStrategy) strategyClass.newInstance();
                    }
                    catch ( Exception ignore ) {
                        //LOG.unableToInstantiateUuidGenerationStrategy(ignore);
                    }
                }
                catch ( ClassNotFoundException ignore ) {
                    //.unableToLocateUuidGenerationStrategy(strategyClassName);
                }
            }
        }
        if ( strategy == null ) {
            // lastly use the standard random generator
            strategy = StandardRandomStrategy.INSTANCE;
        }

        if ( UUID.class.isAssignableFrom( type ) ) {
            valueTransformer = UUIDTypeDescriptor.PassThroughTransformer.INSTANCE;
        }
        else if ( String.class.isAssignableFrom( type ) ) {
            valueTransformer = UUIDTypeDescriptor.ToStringTransformer.INSTANCE;
        }
        else if ( byte[].class.isAssignableFrom( type ) ) {
            valueTransformer = UUIDTypeDescriptor.ToBytesTransformer.INSTANCE;
        }
        else {
            throw new UUIDException( "Unanticipated return type [" + type.getName() + "] for UUID conversion" );
        }
    }

    public Serializable generate(Object session, Object object) throws UUIDException {
        return valueTransformer.transform( strategy.generateUUID( session ) );
    }
}

