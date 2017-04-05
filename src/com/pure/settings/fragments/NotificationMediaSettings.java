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

import android.content.res.Resources;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NotificationMediaSettings extends SettingsPreferenceFragment
         implements OnPreferenceChangeListener {

    private static final String KEY_HEADS_UP_SETTINGS = "heads_up_settings";

    private static final String KEY_SHOW_TICKER = "status_bar_show_ticker";

    private PreferenceScreen mHeadsUp;

    private SwitchPreference mTicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notification_media_settings);
        PreferenceScreen prefScreen = getPreferenceScreen();

        mHeadsUp = (PreferenceScreen) findPreference(KEY_HEADS_UP_SETTINGS);

        mTicker = (SwitchPreference) findPreference(KEY_SHOW_TICKER);
        boolean tickerState = getTickerState();
        if (tickerState) {
            Settings.System.putIntForUser(getActivity().getContentResolver(), Settings.System.HEADS_UP_USER_ENABLED, 0, UserHandle.USER_CURRENT);
            mHeadsUp.setSummary(R.string.summary_heads_up_disabled);
            mHeadsUp.setEnabled(false);
        }else{
            mHeadsUp.setEnabled(true);
            mHeadsUp.setSummary(getUserHeadsUpState() ? R.string.summary_heads_up_enabled : R.string.summary_heads_up_disabled);
        }
        mTicker.setChecked(tickerState);
        mTicker.setOnPreferenceChangeListener(this);
    }

    private boolean getTickerState() {
         return Settings.System.getInt(getContentResolver(),Settings.System.STATUS_BAR_SHOW_TICKER,0) != 0;
    }

    private boolean getUserHeadsUpState() {
         return Settings.System.getInt(getContentResolver(),
                Settings.System.HEADS_UP_USER_ENABLED,
                Settings.System.HEADS_UP_USER_ON) != 0;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.PURE;
    }

     @Override
     public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mTicker) {
            boolean showing = ((Boolean)newValue);
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_SHOW_TICKER, showing ? 1 : 0);
            if (showing) {
                Settings.System.putIntForUser(resolver, Settings.System.HEADS_UP_USER_ENABLED, 0, UserHandle.USER_CURRENT);
                mHeadsUp.setSummary(R.string.summary_heads_up_disabled);
                mHeadsUp.setEnabled(false);
            }else{
                mHeadsUp.setEnabled(true);
                mHeadsUp.setSummary(getUserHeadsUpState() ? R.string.summary_heads_up_enabled : R.string.summary_heads_up_disabled);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean tickerState = getTickerState();
        if (tickerState) {
            Settings.System.putIntForUser(getActivity().getContentResolver(), Settings.System.HEADS_UP_USER_ENABLED, 0, UserHandle.USER_CURRENT);
            mHeadsUp.setSummary(R.string.summary_heads_up_disabled);
            mHeadsUp.setEnabled(false);
        }else{
            mHeadsUp.setEnabled(true);
            mHeadsUp.setSummary(getUserHeadsUpState() ? R.string.summary_heads_up_enabled : R.string.summary_heads_up_disabled);
        }
    }
}
