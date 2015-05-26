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
 *     HapticContentSDKFactory.java
 *
 * Description:
 *     Factory class used to instantiate a proper object of the HapticContentSDKFactory class.
 *
 * =============================================================================
 */

package immersion;

import android.content.Context;


public class HapticContentSDKFactory {

    private static final String TAG = "HapticContentSDKFactory";

    public static HapticContentSDK GetNewSDKInstance(int mode, Context context) {

        HapticContentSDK hapticContentSDK = new HapticContentSDK(mode, context) {
            @Override
            public int openHaptics(String uri) {
                return 0;
            }
        };
        return hapticContentSDK;
    }
}
