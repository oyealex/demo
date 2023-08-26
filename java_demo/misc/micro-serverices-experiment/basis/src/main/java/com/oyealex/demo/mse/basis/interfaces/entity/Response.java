package com.oyealex.demo.mse.basis.interfaces.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Response
 *
 * @author oyealex
 * @since 2023-08-27
 */
@Getter
public class Response<T> {
    @NotNull
    private final String code;

    @Nullable
    private final String desc;

    @Nullable
    private final T data;

    @JsonSerialize
    private Response(@NotNull String code, @Nullable String desc, @Nullable T data) {
        this.code = code;
        this.desc = desc;
        this.data = data;
    }
}
