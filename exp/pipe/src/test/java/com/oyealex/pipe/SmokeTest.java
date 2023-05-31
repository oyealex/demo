package com.oyealex.pipe;

import com.oyealex.pipe.basis.Pipes;
import org.junit.jupiter.api.Test;

import java.util.Objects;

/**
 * Smoke
 *
 * @author oyealex
 * @since 2023-05-12
 */
class SmokeTest extends PipeTestFixture {
    @Test
    void smoke() {
        System.out.println(infiniteOddIntegerStrWithNullsPipe().limit(20).nullsFirst().toList());
        System.out.println(infiniteOddIntegerStrWithNullsPipe().limit(20).nullsLast().mapNull("_").toList());
        System.out.println(infiniteOddIntegerStrWithNullsPipe().limit(20).selectedFirst(Objects::isNull).toList());
        System.out.println(infiniteOddIntegerStrWithNullsPipe().limit(20).selectedLast(Objects::isNull).toList());
    }
}
