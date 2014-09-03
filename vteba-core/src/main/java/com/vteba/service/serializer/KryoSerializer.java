package com.vteba.service.serializer;

import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

/**
 * kryo序列化器扩展
 * @author 尹雷 
 * @since
 * @param <T>
 */
public class KryoSerializer<T> extends Serializer<T> {

    @SuppressWarnings("unchecked")
    @Override
    public void write(Kryo kryo, Output output, T object) {
        //处理空
        if (object == null) {
            return;
        }
        
        Object serialObject = object;
        Class<?> realType = object.getClass();
        //判断是否是Hibernate的代理类
        if (object instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy) object;
            LazyInitializer initializer = proxy.getHibernateLazyInitializer();
            if (initializer.isUninitialized()) {
                //取真实类型
                realType = initializer.getPersistentClass();
                try {
                    serialObject = initializer.getPersistentClass().newInstance();
                } catch (Exception e) {
                    serialObject = null;
                }
            } else {// 已经初始化，直接取加载好的真实类型的对象
                serialObject = initializer.getImplementation();
            }
            
        } else if (object instanceof PersistentCollection) {
            PersistentCollection collection = (PersistentCollection) object;
            if (collection.wasInitialized()) {
                serialObject = collection.getValue();
            }
        }
        new FieldSerializer<T>(kryo, realType).write(kryo, output, (T)serialObject);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T read(Kryo kryo, Input input, Class<T> type) {
        Class<T> realType = type;
        //取真实类型
        if (HibernateProxy.class.isAssignableFrom(type)) {
            realType = (Class<T>) type.getSuperclass();
        }
        //按照真实类型解析对象
        return (T) new FieldSerializer<T>(kryo, realType).read(kryo, input, realType);
    }

}
