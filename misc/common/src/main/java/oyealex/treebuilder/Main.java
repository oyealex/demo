/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package oyealex.treebuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author oye
 * @since 2020-07-03 00:33:15
 */
@Slf4j
public class Main implements Constants {
    public static void main(String[] args) {
        parseFromArgs(args).startBuild();
    }

    private static SingleFolderBuildContext parseFromArgs(String[] args) {
        if (args.length < 8) {
            log.info(
                "java -jar x.jar basePath filePrefix fileSize amount startIndex parallel subFolderPrefix " +
                    "subFolderStartIndex");
            System.exit(0);
        }
        final String basePath = args[0];
        final String filePrefix = args[1];
        final long fileSize = Long.parseLong(args[2]);
        final int amount = Integer.parseInt(args[3]);
        final int startIndex = Integer.parseInt(args[4]);
        final int parallel = Integer.parseInt(args[5]);
        final String subFolderPrefix = args[6];
        final int subFolderStartIndex = Integer.parseInt(args[7]);
        return new SingleFolderBuildContext(parallel,
            new SingleFolderBuildParam(
                basePath,
                filePrefix,
                subFolderPrefix,
                subFolderStartIndex,
                fileSize,
                amount,
                startIndex));
    }
}
