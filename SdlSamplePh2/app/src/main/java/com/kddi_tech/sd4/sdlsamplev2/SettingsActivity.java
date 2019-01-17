/**
 Copyright 2018 KDDI Technology Corp.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.kddi_tech.sd4.sdlsamplev2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

/**
 * ※※※※※
 * SDL Coreで利用する通知項目の設定を行うためのクラス
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "LOG:[SettingsActivity]";
    // seekbarの最大/小値
    private static final int SEEK_MAX = 99;
    private static final int SEEK_MIN = 1;
    // seekbarの初期値
    public static final String SEEK_1_DEF = "50";
    public static final String SEEK_2_DEF = "40";
    public static final String SEEK_3_DEF = "30";
    public static final String SEEK_4_DEF = "20";
    public static final String SEEK_5_DEF = "10";

    private EditText et1;
    private EditText et2;
    private EditText et3;
    private EditText et4;
    private EditText et5;
    private SeekBar seekBar1;
    private SeekBar seekBar2;
    private SeekBar seekBar3;
    private SeekBar seekBar4;
    private SeekBar seekBar5;
    private Switch aSwitch1;
    private Switch aSwitch2;
    private Switch aSwitch3;
    private Switch aSwitch4;
    private Switch aSwitch5;
    private Switch tSwitch;
    private Switch hSwitchOn;
    private Switch hSwitchOff;

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefManager = PrefManager.getInstance(this.getApplicationContext());

        // binding
        aSwitch1 = findViewById(R.id.seekSwitch1);
        aSwitch2 = findViewById(R.id.seekSwitch2);
        aSwitch3 = findViewById(R.id.seekSwitch3);
        aSwitch4 = findViewById(R.id.seekSwitch4);
        aSwitch5 = findViewById(R.id.seekSwitch5);
        aSwitch1.setOnClickListener(this);
        aSwitch2.setOnClickListener(this);
        aSwitch3.setOnClickListener(this);
        aSwitch4.setOnClickListener(this);
        aSwitch5.setOnClickListener(this);

        seekBar1 = findViewById(R.id.seekBar1);
        seekBar2 = findViewById(R.id.seekBar2);
        seekBar3 = findViewById(R.id.seekBar3);
        seekBar4 = findViewById(R.id.seekBar4);
        seekBar5 = findViewById(R.id.seekBar5);
        seekBar1.setOnSeekBarChangeListener(new _changeSeekBar());
        seekBar2.setOnSeekBarChangeListener(new _changeSeekBar());
        seekBar3.setOnSeekBarChangeListener(new _changeSeekBar());
        seekBar4.setOnSeekBarChangeListener(new _changeSeekBar());
        seekBar5.setOnSeekBarChangeListener(new _changeSeekBar());

        et1 = findViewById(R.id.seekText1);
        et2 = findViewById(R.id.seekText2);
        et3 = findViewById(R.id.seekText3);
        et4 = findViewById(R.id.seekText4);
        et5 = findViewById(R.id.seekText5);
        et1.addTextChangedListener(new _changeEditText(et1));
        et2.addTextChangedListener(new _changeEditText(et2));
        et3.addTextChangedListener(new _changeEditText(et3));
        et4.addTextChangedListener(new _changeEditText(et4));
        et5.addTextChangedListener(new _changeEditText(et5));

        tSwitch = findViewById(R.id.tireSwitch);
        hSwitchOn = findViewById(R.id.lightOnSwitch);
        hSwitchOff = findViewById(R.id.lightOffSwitch);
        tSwitch.setOnClickListener(this);
        hSwitchOn.setOnClickListener(this);
        hSwitchOff.setOnClickListener(this);

        // 初期設定
        et1.setText(prefManager.getPrefByStr(R.id.seekText1, SEEK_1_DEF));
        et2.setText(prefManager.getPrefByStr(R.id.seekText2, SEEK_2_DEF));
        et3.setText(prefManager.getPrefByStr(R.id.seekText3, SEEK_3_DEF));
        et4.setText(prefManager.getPrefByStr(R.id.seekText4, SEEK_4_DEF));
        et5.setText(prefManager.getPrefByStr(R.id.seekText5, SEEK_5_DEF));

        aSwitch1.setChecked(prefManager.getPrefByBool(R.id.seekSwitch1, true));
        aSwitch2.setChecked(prefManager.getPrefByBool(R.id.seekSwitch2, true));
        aSwitch3.setChecked(prefManager.getPrefByBool(R.id.seekSwitch3, true));
        aSwitch4.setChecked(prefManager.getPrefByBool(R.id.seekSwitch4, true));
        aSwitch5.setChecked(prefManager.getPrefByBool(R.id.seekSwitch5, true));
        tSwitch.setChecked(prefManager.getPrefByBool(R.id.tireSwitch, true));
        hSwitchOn.setChecked(prefManager.getPrefByBool(R.id.lightOnSwitch, true));
        hSwitchOff.setChecked(prefManager.getPrefByBool(R.id.lightOffSwitch, true));

        _changeEnable(aSwitch1.isChecked(),R.id.seekSwitch1, seekBar1, et1);
        _changeEnable(aSwitch2.isChecked(),R.id.seekSwitch2, seekBar2, et2);
        _changeEnable(aSwitch3.isChecked(),R.id.seekSwitch3, seekBar3, et3);
        _changeEnable(aSwitch4.isChecked(),R.id.seekSwitch4, seekBar4, et4);
        _changeEnable(aSwitch5.isChecked(),R.id.seekSwitch5, seekBar5, et5);
    }

    /**
     * ※※※※※
     * EditTextの値の変更をWatchし、紐付くseekbarを更新する
     */
    private class _changeEditText implements TextWatcher {
        private EditText view;
        private _changeEditText(EditText view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            try {
                int inputVal = Integer.valueOf(s.toString());
                if(inputVal < SEEK_MIN) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.setting_error), Toast.LENGTH_SHORT).show();
                } else {
                    prefManager.setPrefByStr(view.getId(), s.toString());
                    _updateSeekBar(view.getId(), inputVal);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.setting_error), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    /**
     * ※※※※※
     * EditTextで入力のあった値で、紐付くSeekBarを更新する
     * @param id EditTextの一意のID(R.id.xxx)値
     * @param progress SeekBarに設定する、入力値
     */
    private void _updateSeekBar(int id, int progress) {
        progress = _validateSeekBar(progress);
        switch(id) {
            case R.id.seekText1:
                seekBar1.refreshDrawableState();
                seekBar1.setProgress(progress);
                break;
            case R.id.seekText2:
                seekBar2.refreshDrawableState();
                seekBar2.setProgress(progress);
                break;
            case R.id.seekText3:
                seekBar3.refreshDrawableState();
                seekBar3.setProgress(progress);
                break;
            case R.id.seekText4:
                seekBar4.refreshDrawableState();
                seekBar4.setProgress(progress);
                break;
            case R.id.seekText5:
                seekBar5.refreshDrawableState();
                seekBar5.setProgress(progress);
                break;
        }
    }

    /**
     * ※※※※※
     * SeelBarに設定するための許容範囲値内の値を返却する
     * @param progress 入力のあった値
     * @return 有効な値
     */
    private int _validateSeekBar(int progress) {
        if(progress > SEEK_MAX) {
            progress = SEEK_MAX;
        } else if (progress < SEEK_MIN) {
            progress = SEEK_MIN;
        }
        return progress;
    }

    /**
     * ※※※※※
     * SeekBarの変更を検知する
     */
    private class _changeSeekBar implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                // ユーザ自身による変更時のみ許容する
                // (EditText変更時にSeekbarをプログラム上から変更するので、そこからの変更は無視する)
                _updateTextValue(seekBar.getId(), progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    /**
     * ※※※※※
     * SeekBarの変更から、紐付くEditTextを更新する
     * @param id SeekBarの一意のID(R.id.xxx)値
     * @param progress EditTextに設定する、入力値
     */
    private void _updateTextValue(int id, int progress) {
        progress = _validateSeekBar(progress);
        switch(id) {
            case R.id.seekBar1:
                et1.setText(String.valueOf(progress));
                break;
            case R.id.seekBar2:
                et2.setText(String.valueOf(progress));
                break;
            case R.id.seekBar3:
                et3.setText(String.valueOf(progress));
                break;
            case R.id.seekBar4:
                et4.setText(String.valueOf(progress));
                break;
            case R.id.seekBar5:
                et5.setText(String.valueOf(progress));
                break;
        }
    }

    /**
     * switchのOn/Offを切り替えた際の処理
     * @param v switchのview
     */
    @Override
    public void onClick(View v) {
        SeekBar sb = null;
        EditText et = null;
        boolean isChecked = ((Switch)v).isChecked();

        switch (v.getId()) {
            case R.id.seekSwitch1:
            case R.id.seekSwitch2:
            case R.id.seekSwitch3:
            case R.id.seekSwitch4:
            case R.id.seekSwitch5:
                ViewGroup row = (ViewGroup) v.getParent();
                for (int itemPos = 0; itemPos < row.getChildCount(); itemPos++) {
                    View view = row.getChildAt(itemPos);
                    if (view instanceof SeekBar) {
                        sb = (SeekBar) view;
                    } else if(view instanceof EditText) {
                        et = (EditText) view;
                    }
                }
                break;
            case R.id.lightOffSwitch:
            case R.id.lightOnSwitch:
            case R.id.tireSwitch:
            default:
                break;
        }
        _changeEnable(isChecked, v.getId(), sb, et);
    }

    /**
     * ※※※※※
     * resourceIdで指定されたswitchのチェック状態(isChecked)に応じて、seekbarとedittextの活性化/非活性化を切り替える
     * @param isChecked チェックのOn/Off状態
     * @param resourceId switchのリソースID
     * @param sb resourceIdに紐づくseekbar
     * @param et resourceIdに紐づくedittext
     */
    private void _changeEnable(boolean isChecked, int resourceId, @Nullable SeekBar sb, @Nullable EditText et) {
        prefManager.setPrefByBool(resourceId, isChecked);
        if (sb != null) {
            sb.setEnabled(isChecked);
        }
        if (et != null) {
            et.setEnabled(isChecked);
        }
    }
}
