package com.septemberhx.common.base;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2019/12/13
 */
@Setter
@Getter
public class MUniqueObjectManager<T extends MUniqueObject> {

    protected Map<String, T> objectMap;
    protected static Logger logger = LogManager.getLogger("MObjectManager");

    public MUniqueObjectManager() {
        this.objectMap = new HashMap<>();
    }

    public void update(T obj) {
        if (this.objectMap.containsKey(obj.getId())) {
            logger.warn("Object " + obj.getId() + " has already been added in Manager");
        }

        this.objectMap.put(obj.getId(), obj);
    }

    public void remove(String objectId) {
        if (!this.objectMap.containsKey(objectId)) {
            logger.warn("Object " + objectId + " is not in the object map");
        } else {
            this.objectMap.remove(objectId);
        }
    }

    public List<T> getAllValues() {
        return new ArrayList<>(objectMap.values());
    }

    public Optional<T> getById(String id) {
        return Optional.ofNullable(this.objectMap.get(id));
    }

    public boolean containsById(String id) {
        return this.objectMap.containsKey(id);
    }
}
