package com.septemberhx.common.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/3/15
 */
public class CommonUtils {

    public static <T> Set<T> getSetIntersection(Set<T> s1, Set<T> s2) {
        Set<T> tmpSet = new HashSet<>(s1);
        tmpSet.retainAll(s2);
        return tmpSet;
    }
}
