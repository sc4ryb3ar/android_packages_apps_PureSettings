/*
 * Copyright (C) 2017 The Pure Nexus Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pure.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceScreen;
import android.content.res.Resources;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.SettingsPreferenceFragment;

public class PureSettings extends SettingsPreferenceFragment {

    private static final String CATEGORY_HWKEY = "pure_hardware_keys_settings";

    // Masks for checking presence of hardware keys.
    // Must match values in frameworks/base/core/res/res/values/config.xml
    public static final int KEY_MASK_HOME = 0x01;
    public static final int KEY_MASK_BACK = 0x02;
    public static final int KEY_MASK_MENU = 0x04;
    public static final int KEY_MASK_ASSIST = 0x08;
    public static final int KEY_MASK_APP_SWITCH = 0x10;
    public static final int KEY_MASK_CAMERA = 0x20;
    public static final int KEY_MASK_VOLUME = 0x40;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.pure_settings_main);
        PreferenceScreen prefScreen = getPreferenceScreen();
        final PreferenceScreen hwkeyCat = (PreferenceScreen) prefScreen.findPreference(CATEGORY_HWKEY);
        
        // bits for hardware keys present on device
        final int deviceKeys = getResources().getInteger(
                com.android.internal.R.integer.config_deviceHardwareKeys);

        // read bits for present hardware keys
        final boolean hasHomeKey = (deviceKeys & KEY_MASK_HOME) != 0;
        final boolean hasBackKey = (deviceKeys & KEY_MASK_BACK) != 0;
        final boolean hasMenuKey = (deviceKeys & KEY_MASK_MENU) != 0;
        final boolean hasAssistKey = (deviceKeys & KEY_MASK_ASSIST) != 0;
        final boolean hasAppSwitchKey = (deviceKeys & KEY_MASK_APP_SWITCH) != 0;

        if (!hasHomeKey && !hasBackKey && !hasMenuKey && !hasAssistKey && !hasAppSwitchKey) { // no hwkeys, remove pref
            prefScreen.removePreference(hwkeyCat);
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.PURE;
    }
}

