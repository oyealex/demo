package com.oye.common.general.executor.result.state;

/** 结果状态 */
public enum ResultState {
    /** 成功。 */
    SUCCESSFUL,
    /** 部分成功。 */
    PARTIALLY_SUCCESSFUL,
    /** 失败。 */
    FAILED,
    /** 跳过。 */
    SKIPPED,
    /** 暂无结果。 */
    UNDEFINED,
}
