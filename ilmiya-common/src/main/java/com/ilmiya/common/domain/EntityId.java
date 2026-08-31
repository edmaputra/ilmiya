package com.ilmiya.common.domain;

import java.io.Serializable;

public interface EntityId<T> extends Serializable {
    T value();
}
