/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oye.common.treebuilder;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author oye
 * @since 2020-07-02 23:43:45
 */
@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class SingleFolderBuildParam {
    private final String basePath;

    private final String filePrefix;

    private final String subFolderPrefix;

    private final int subFolderStartIndex;

    private final long fileSize;

    private final int fileAmount;

    private final int startIndex;
}
