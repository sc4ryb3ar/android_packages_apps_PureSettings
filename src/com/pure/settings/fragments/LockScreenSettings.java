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

package com.pure.settings.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.pure.settings.utils.Utils;
import com.pure.settings.preferences.SystemSettingSwitchPreference;

import android.provider.Settings;

public class LockScreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final int MY_USER_ID = UserHandle.myUserId();

    private static final String LS_OPTIONS_CAT = "lockscreen_options";
    private static final String LS_SECURE_CAT = "lockscreen_secure_options";

    private static final String KEYGUARD_TORCH = "keyguard_toggle_torch";
    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String FP_UNLOCK_KEYSTORE = "fp_unlock_keystore";
    private static final String LOCKSCREEN_BATTERY_INFO = "lockscreen_battery_info";

    private FingerprintManager mFingerprintManager;

    private SystemSettingSwitchPreference mLsTorch;
    private SystemSettingSwitchPreference mFingerprintVib;
    private SwitchPreference mFpKeystore;
    private SwitchPreference mLockscreenCharging;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lockscreen_settings);
        PreferenceScreen prefScreen = getPreferenceScreen();
        Resources resources = getResources();

        PreferenceCategory optionsCategory = (PreferenceCategory) findPreference(LS_OPTIONS_CAT);
        PreferenceCategory secureCategory = (PreferenceCategory) findPreference(LS_SECURE_CAT);

        mLsTorch = (SystemSettingSwitchPreference) findPreference(KEYGUARD_TORCH);
        if (!Utils.deviceSupportsFlashLight(getActivity())) {
            optionsCategory.removePreference(mLsTorch);
        }

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintVib = (SystemSettingSwitchPreference) findPreference(FINGERPRINT_VIB);
        mFpKeystore = (SwitchPreference) findPreference(FP_UNLOCK_KEYSTORE);
        if (!mFingerprintManager.isHardwareDetected()){
            secureCategory.removePreference(mFingerprintVib);
            secureCategory.removePreference(mFpKeystore);
        }else{
            mFpKeystore.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.FP_UNLOCK_KEYSTORE, 0) == 1));
            mFpKeystore.setOnPreferenceChangeListener(this);
        }

        mLockscreenCharging = (SwitchPreference) findPreference(LOCKSCREEN_BATTERY_INFO);
        if (!resources.getBoolean(R.bool.showCharging)) {
            prefScreen.removePreference(mLockscreenCharging);
        } else {
        mLockscreenCharging.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_BATTERY_INFO, 0) == 1));
        mLockscreenCharging.setOnPreferenceChangeListener(this);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mLockscreenCharging) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.LOCKSCREEN_BATTERY_INFO, value ? 1 : 0);
            return true;
        } else if (preference == mFpKeystore) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FP_UNLOCK_KEYSTORE, value ? 1 : 0);
            return true;
        }
        return false;
    }


    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.PURE;
    }
}

