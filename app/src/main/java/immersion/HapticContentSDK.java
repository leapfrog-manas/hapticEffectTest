/*
 * =============================================================================
 * Copyright (c) 2013-2014  Immersion Corporation.  All rights reserved.
 *                          Immersion Corporation Confidential and Proprietary
 *
 * MAY NOT BE USED OR DISTRIBUTED UNLESS EXPRESSLY LICENSED UNDER, AND
 * SUBJECT TO, A SEPARATE WRITTEN LICENSE AGREEMENT EXECUTED BETWEEN THE
 * APPLICABLE OEM/MANUFACTURER AND IMMERSION CORPORATION.
 *
 * File:
 *      HapticContentSDK.java
 *
 * Description:
 *     Entry point class into the Haptic Media SDK
 *
 * =============================================================================
 */

package immersion;

import android.content.Context;

public abstract class HapticContentSDK {

    private static final String TAG = "HapticContentSDK";

    public static final int SDKMODE_MEDIAPLAYBACK = 0x0;


    public static final int SUCCESS = 0;


    public static final int INVALID = -1;


    public static final int INACCESSIBLE_URL = -2;


    public static final int PERMISSION_DENIED = -3;


    public static final int MALFORMED_URL = -4;


    public static final int UNSUPPORTED_PROTOCOL = -5;

    HapticContentSDK(int mode, Context context) {
    }

    public abstract int openHaptics(String uri);


    public final int update(long timestamp) {

        return SUCCESS;
    }

    public final int play() {
        return SUCCESS;
    }

    public final int resume() {
        return SUCCESS;
    }

    public final int pause() {
        return SUCCESS;
    }

    public final int stop() {
        return SUCCESS;
    }

    public final int seek(int pos) {
        return SUCCESS;
    }

    public final String getVersion() {
        return "";
    }

    public final int mute() {
        return SUCCESS;
    }

    public final int unmute() {
        return SUCCESS;
    }
}
