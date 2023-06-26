/*
 * Copyright (c) 2021. oyealex. All rights reserved.
 */

package com.oyealex.anfs.rpc.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * RpcConstants
 *
 * @author oyealex
 * @version 1.0
 * @since 2021/3/13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcConstants {
    /* msg type */

    public static final int MSG_TYPE_CALL = 0;

    public static final int MSG_TYPE_REPLY = 1;

    /* reply stat */

    public static final int REPLY_STAT_ACCEPTED = 0;

    public static final int REPLY_STAT_DENIED = 1;

    /* accept stat */

    public static final int ACCEPT_STAT_SUCCESS = 0; // RPC executed successfully

    public static final int ACCEPT_STAT_PROG_UNAVAIL = 1; // remote hasn’t exported program

    public static final int ACCEPT_STAT_PROG_MISMATCH = 2; // remote can’t support version #

    public static final int ACCEPT_STAT_PROC_UNAVAIL = 3; // program can’t support procedure

    public static final int ACCEPT_STAT_GARBAGE_ARGS = 4; // procedure can’t decode params

    public static final int ACCEPT_STAT_SYSTEM_ERR = 5; // errors like memory allocation failure

    /* reject stat */

    public static final int REJECT_STAT_RPC_MISMATCH = 0; // RPC version number != 2

    public static final int REJECT_STAT_AUTH_ERROR = 1; // remote can’t authenticate caller

    /* auth stat */

    public static final int AUTH_STAT_AUTH_OK = 0; // success

    // failed at remote end

    public static final int AUTH_STAT_AUTH_BADCRED = 1; // bad credential (seal broken)

    public static final int AUTH_STAT_AUTH_REJECTEDCRED = 2; // client must begin new session

    public static final int AUTH_STAT_AUTH_BADVERF = 3; // bad verifier (seal broken)

    public static final int AUTH_STAT_AUTH_REJECTEDVERF = 4; // verifier expired or replayed

    public static final int AUTH_STAT_AUTH_TOOWEAK = 5; // rejected for security reasons

    // failed locally

    public static final int AUTH_STAT_AUTH_INVALIDRESP = 6; // bogus response verifier

    public static final int AUTH_STAT_AUTH_FAILED = 7; // reason unknown

    /* auth flavor */

    public static final int AUTH_FLAVOR_NONE = 0;

    public static final int AUTH_FLAVOR_SYS = 1;

    public static final int AUTH_FLAVOR_SHORT = 2;

    /* program */

    public static final int PROGRAM_PORTMAP = 100000;

    public static final int PROGRAM_NFS = 100003;

    public static final int PROGRAM_MOUNT = 100005;

    /* program version */

    public static final int PROGRAM_VERS_NFS_V3 = 3;

    public static final int PROGRAM_VERS_PORTMAP_V2 = 2;

    public static final int PROGRAM_VERS_MOUNT_V3 = 3;

    /* procedure */

    public static final int PROCEDURE_PORTMAP_GETPORT = 3;

    public static final int PROCEDURE_MOUNT_MNT = 1;

    public static final int PROCEDURE_MOUNT_UMNT = 3;

    public static final int PROCEDURE_MOUNT_EXPORT = 5;

    /* proto */

    public static final int PROTO_TCP = 6;

    public static final int PROTO_UDP = 17;

    /* mountstat3 */

    public static final int MNT3_OK = 0; // no error

    public static final int MNT3ERR_PERM = 1; // Not owner

    public static final int MNT3ERR_NOENT = 2; // No such file or directory

    public static final int MNT3ERR_IO = 5; // I/O error

    public static final int MNT3ERR_ACCES = 13; // Permission denied

    public static final int MNT3ERR_NOTDIR = 20; // Not a directory

    public static final int MNT3ERR_INVAL = 22; // Invalid argument

    public static final int MNT3ERR_NAMETOOLONG = 63; // Filename too long

    public static final int MNT3ERR_NOTSUPP = 10004; // Operation not supported

    public static final int MNT3ERR_SERVERFAULT = 10006; // A failure on the server
}
