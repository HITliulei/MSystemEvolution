package com.septemberhx.common.service;

import lombok.Getter;

import java.util.Objects;

/**
 * @author SeptemberHX
 * @version 0.1
 * @date 2020/2/29
 *
 * Service Leverage Agreement for users and services
 *
 * This class is created for possible future extension
 */
@Getter
public class MSla {

    private int level;

    public MSla(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "MSla{" +
                "level=" + level +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MSla mSla = (MSla) o;
        return level == mSla.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(level);
    }
}
