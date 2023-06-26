package com.oye.common.influxdb;

import com.google.common.base.Stopwatch;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.influxdb.client.InfluxDBClientFactory.create;

/**
 * InfluxdbMain
 *
 * @author oyealex
 * @version 1.0
 * @since 2022-11-15
 */
public class InfluxdbMain {
    public static final long NANOS = 1_000_000_000L;

    private static final String URL = "http://localhost:18086";

    private static final String TOKEN
            = "3jY0q7y1Mz4Jo_iba-QAUDHBLS0d1ktkqpp0BHxODHYGY84swoSpAgJs9dixwRqlMLTfwqnscng-vNi-zU70xA==";

    private static final String ORG = "com.oyealex";

    private static final String BUCKET = "primary";

    public static void main(String[] args) {
        try (InfluxDBClient client = create(URL, TOKEN.toCharArray(), ORG, BUCKET)) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            // query(client);
            writeHugeData10m1x1t10f(client, Instant.now().getEpochSecond() * NANOS);
            System.out.println(stopwatch.stop());
        }
    }

    private static void query(InfluxDBClient client) {
        QueryApi queryApi = client.getQueryApi();
        queryApi.query(
                "from(bucket:\"primary\") |> range(start: 0) |> filter(fn: (r) => r._measurement == \"airSensors\") |> limit(n: 10)",
                ORG,
                (cancellable, fluxRecord) -> System.out.println(fluxRecord),
                Throwable::printStackTrace, () -> System.out.println("complete"));
    }

    private static void writeHugeData10m1x1t10f(InfluxDBClient client, long time) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        List<Point> points = new ArrayList<>(10_000);
        for (int i = 0; i < 100_000_000; i++) {
            points.add(buildRandomPoint(i, time));
            if (points.size() >= 10_000) {
                writeApi.writePoints(points);
                points.clear();
                System.out.println(i);
            }
        }
        if (!points.isEmpty()) {
            writeApi.writePoints(points);
        }
    }

    private static Point buildRandomPoint(int index, long time) {
        return Point.measurement("10m1x1t10f")
                .addTag("sn", "10m1x1t10f_4")
                .addField("f0", index)
                .addField("f1", index)
                .addField("f2", index)
                .addField("f3", index)
                .addField("f4", index)
                .addField("f5", index)
                .addField("f6", index)
                .addField("f7", index)
                .addField("f8", index)
                .addField("f9", index)
                .time(time + index * NANOS, WritePrecision.NS);
    }
}
