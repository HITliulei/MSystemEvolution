package com.septemberhx.mclient.base;

import com.septemberhx.mclient.annotation.MApiFunction;
import com.septemberhx.mclient.annotation.MFunctionType;
import com.septemberhx.mclient.core.MClientSkeleton;
import com.septemberhx.mclient.core.MObjectProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @Author: septemberhx
 * @Date: 2018-12-09
 * @Version 0.2
 */
public abstract class MObject {
    private Logger logger = LogManager.getLogger(this.getClass());
    protected String id = null;
    private MObject objectProxy;

    protected MObject() {
        this.id = this.getClass().getCanonicalName() + "_" + UUID.randomUUID().toString();

        // exclude the cglib object
        if (this.getId() != null) {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.getAnnotation(MFunctionType.class) != null) {
                    if (!MObject.class.isAssignableFrom(field.getType())) {
                        throw new IllegalArgumentException("Wrong type with MFunctionType com.septemberhx.mclient.annotation!");
                    } else {

                        try {
                            MObjectProxy tmpProxy = new MObjectProxy();

                            // create an instance of field.getType with default constructor
                            // this means the class needs to have an default constructor
                            Class<?> clazz = field.getType();
                            Constructor<?> ctor = clazz.getConstructor();
                            MObject obj = (MObject) ctor.newInstance(new Object[]{});
                            MClientSkeleton.getInstance().registerParent(obj, this.getId());

                            // set the proxy object
                            field.setAccessible(true);
                            field.set(this, tmpProxy.getInstance((obj)));
                        } catch (IllegalAccessException e1) {
                            logger.error(e1);
                        } catch (InstantiationException e2) {
                            logger.error(e2);
                        } catch (NoSuchMethodException e3) {
                            logger.error(e3);
                        } catch (InvocationTargetException e4) {
                            logger.error(e4);
                        }
                    }
                }
            }

            for (Method method : this.getClass().getDeclaredMethods()) {
                if (method.getAnnotation(MApiFunction.class) != null) {
                    MClientSkeleton.getInstance().registerObjectAndApi(this.getId(), method.getName());
                }
            }
            MClientSkeleton.getInstance().registerMObject(this);
            logger.debug(this.getId() + " created");
            MClientSkeleton.getInstance().printParentIdMap();
        }
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }
}
