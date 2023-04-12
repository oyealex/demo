package com.oye.ibmp.client.infrastructure.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.core.env.Environment;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * SpelTestRunner
 *
 * @author oyealex
 * @since 2023-02-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpelTestRunner implements ApplicationRunner, EnvironmentAware {
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("value: {}", environment.resolvePlaceholders("${app.cache.enabled}"));
    }
}
