/*
 * Copyright (c) 2016 - 2021. oyealex. All rights reserved.
 */

package com.oyealex.game.tetris.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.stream.StreamSupport;

/**
 * ShapeFactory
 *
 * @author oyealex
 * @version 1.0
 * @since 2021-09-15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShapeFactory {
    private static final Shape[] SHAPES = prepareShapes();

    private static final Random RANDOM = new Random();

    private static Shape[] prepareShapes() {
        Gson gson = new Gson();
        try (InputStream dataStream = Shape.class.getResourceAsStream("/shapes.json")) {
            if (dataStream == null) {
                throw new IllegalStateException("resource [shapes.json] missing");
            }
            return parseShapes(gson, dataStream);
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static Shape[] parseShapes(Gson gson, InputStream dataStream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(dataStream, StandardCharsets.UTF_8)) {
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            return StreamSupport.stream(data.getAsJsonArray("shapes").spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .filter(json -> json.getAsJsonArray("transformations").size() > 0)
                .map(json -> parseTransformations(gson, json.get("name").getAsString(),
                    json.getAsJsonArray("transformations")))
                .toArray(Shape[]::new);
        }
    }

    private static Shape parseTransformations(Gson gson, String name, JsonArray transformations) {
        Shape current = null;
        Shape head = null;
        for (JsonElement transformationEle : transformations) {
            JsonObject transformation = transformationEle.getAsJsonObject();
            Shape shape = new Shape(name, gson.fromJson(transformation.get("offsets"), int[][].class));
            if (head == null) {
                head = shape;
                current = shape;
            }
            current.setNext(shape);
            current = shape;
        }
        if (current != null) {
            current.setNext(head);
        }
        return head;
    }

    /**
     * 获取随机的形状
     *
     * @return 形状
     */
    public static Shape randomShape() {
        return SHAPES[RANDOM.nextInt(SHAPES.length)];
    }
}
