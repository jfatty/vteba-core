package com.vteba.tx.jdbc.uuid;

import java.io.Serializable;
import java.util.UUID;

/**
 * Descriptor for {@link UUID} handling.
 *
 * @author Steve Ebersole
 */
public class UUIDTypeDescriptor {
    public static final UUIDTypeDescriptor INSTANCE = new UUIDTypeDescriptor();

    public UUIDTypeDescriptor() {
        
    }

    public String toString(UUID value) {
        return ToStringTransformer.INSTANCE.transform( value );
    }

    public UUID fromString(String string) {
        return ToStringTransformer.INSTANCE.parse( string );
    }

    @SuppressWarnings({ "unchecked" })
    public <X> X unwrap(UUID value, Class<X> type) {
        if ( value == null ) {
            return null;
        }
        if ( UUID.class.isAssignableFrom( type ) ) {
            return (X) PassThroughTransformer.INSTANCE.transform( value );
        }
        if ( String.class.isAssignableFrom( type ) ) {
            return (X) ToStringTransformer.INSTANCE.transform( value );
        }
        if ( byte[].class.isAssignableFrom( type ) ) {
            return (X) ToBytesTransformer.INSTANCE.transform( value );
        }
        throw new RuntimeException("can not wrap calss : " + type.getName() );
    }

    public <X> UUID wrap(X value) {
        if ( value == null ) {
            return null;
        }
        if ( UUID.class.isInstance( value ) ) {
            return PassThroughTransformer.INSTANCE.parse( value );
        }
        if ( String.class.isInstance( value ) ) {
            return ToStringTransformer.INSTANCE.parse( value );
        }
        if ( byte[].class.isInstance( value ) ) {
            return ToBytesTransformer.INSTANCE.parse( value );
        }
        throw new RuntimeException("can not wrap value : " + value.getClass().getName() );
    }

    public static interface ValueTransformer {
        public Serializable transform(UUID uuid);
        public UUID parse(Object value);
    }

    public static class PassThroughTransformer implements ValueTransformer {
        public static final PassThroughTransformer INSTANCE = new PassThroughTransformer();

        public UUID transform(UUID uuid) {
            return uuid;
        }

        public UUID parse(Object value) {
            return (UUID)value;
        }
    }

    public static class ToStringTransformer implements ValueTransformer {
        public static final ToStringTransformer INSTANCE = new ToStringTransformer();

        public String transform(UUID uuid) {
            return uuid.toString();
        }

        public UUID parse(Object value) {
            return UUID.fromString( (String) value );
        }
    }

    public static class ToBytesTransformer implements ValueTransformer {
        public static final ToBytesTransformer INSTANCE = new ToBytesTransformer();

        public byte[] transform(UUID uuid) {
            byte[] bytes = new byte[16];
            System.arraycopy( BytesHelper.fromLong( uuid.getMostSignificantBits() ), 0, bytes, 0, 8 );
            System.arraycopy( BytesHelper.fromLong( uuid.getLeastSignificantBits() ), 0, bytes, 8, 8 );
            return bytes;
        }

        public UUID parse(Object value) {
            byte[] msb = new byte[8];
            byte[] lsb = new byte[8];
            System.arraycopy( value, 0, msb, 0, 8 );
            System.arraycopy( value, 8, lsb, 0, 8 );
            return new UUID( BytesHelper.asLong( msb ), BytesHelper.asLong( lsb ) );
        }
    }
}

