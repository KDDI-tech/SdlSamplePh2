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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.smartdevicelink.transport.TransportConstants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static boolean isFirstConnect = true;
    private ImageView vehicleLight;
    private UpdateReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // this demo app allowd only Android Oreo+ Device
            return;
        }
        vehicleLight = findViewById(R.id.vehicle_light);
        vehicleLight.setImageAlpha(0);

        _initPreference();

        findViewById(R.id.btn_connect).setOnClickListener(this);
        findViewById(R.id.btn_settings).setOnClickListener(this);
        findViewById(R.id.btn_info).setOnClickListener(this);
        findViewById(R.id.btn_rss).setOnClickListener(this);

        // SdlServiceからのレスポンスを取得
        receiver = new UpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.action_service_close));
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    /**
     * SdlServiceからのレスポンスを取得するためのレシーバー
     */
    protected class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            vehicleLight.setImageAlpha(0);
            Bundle extras = intent.getExtras();
            isFirstConnect = extras.getBoolean(getResources().getString(R.string.is_first_connect));
        }
    }

    /**
     * ※※※※※
     * SharedPreferencesに初期値を格納する
     */
    private void _initPreference() {
        PrefManager prefManager = PrefManager.getInstance(this);
        Resources res = getResources();
        if (! prefManager.read(res.getString(R.string.pref_key_init_flg),false)) {
            prefManager.write(res.getString(R.string.pref_key_init_flg), true);
            prefManager.write(res.getResourceEntryName(R.id.lightOffSwitch), true);
            prefManager.write(res.getResourceEntryName(R.id.lightOnSwitch), true);
            prefManager.write(res.getResourceEntryName(R.id.tireSwitch), true);
            prefManager.write(res.getResourceEntryName(R.id.seekSwitch1), true);
            prefManager.write(res.getResourceEntryName(R.id.seekSwitch2), true);
            prefManager.write(res.getResourceEntryName(R.id.seekSwitch3), true);
            prefManager.write(res.getResourceEntryName(R.id.seekSwitch4), true);
            prefManager.write(res.getResourceEntryName(R.id.seekSwitch5), true);
            prefManager.write(res.getResourceEntryName(R.id.seekText1), SettingsActivity.SEEK_1_DEF);
            prefManager.write(res.getResourceEntryName(R.id.seekText2), SettingsActivity.SEEK_2_DEF);
            prefManager.write(res.getResourceEntryName(R.id.seekText3), SettingsActivity.SEEK_3_DEF);
            prefManager.write(res.getResourceEntryName(R.id.seekText4), SettingsActivity.SEEK_4_DEF);
            prefManager.write(res.getResourceEntryName(R.id.seekText5), SettingsActivity.SEEK_5_DEF);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                _connectToSdl();
                break;
            case R.id.btn_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.btn_info:
                startActivity( new Intent(this, InformationActivity.class));
                break;
            case R.id.btn_rss:
                startActivity( new Intent(this, RssActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * ※※※※※
     * SDL Coreに対して接続を試みる
     */
    private void _connectToSdl() {
        // *** about BuildCounfig ***
        // MBT - Multiplexing Bluetooth
        // LBT - Legacy Bluetooth
        //   ->https://www.bluetooth.com/ja-jp/specifications/bluetooth-core-specification/legacy-specifications
        // TCP - Transmission Control Protocol
        // USB - Universal Serial Bus
        // BuildConfigについてはbuild.gradleを参照してください。
        if(BuildConfig.TRANSPORT.equals("MBT")) {
            // 接続確認を行い(問題なければ)SdlReceiver.onSdlEnabled()が呼ばれます。
            // queryForConnectedServiceは内部処理でBTを利用するので、
            // Android Studioのエミュレータでは基本的にテストができません。
            //SdlReceiver.queryForConnectedService(this);
        } else if(BuildConfig.TRANSPORT.equals("TCP") || BuildConfig.TRANSPORT.equals("LBT")) {
            Log.d("[Log:[MainActivity]]", "onCreate");
            Intent proxyIntent = new Intent(this, SdlService.class);
            proxyIntent.putExtra("isFirstConnect",isFirstConnect);
            proxyIntent.putExtra(TransportConstants.FORCE_TRANSPORT_CONNECTED, false);

            // Android Oreo(API 26)からはサービスの挙動に変更・制限が発生しているため、
            // OSバージョンに合わせてサービスの起動方法を変更します。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // minSdkVersion >= 26
                // startForegroundService()を呼び出し、起動されたサービスは、
                // 5秒以内にService.startForeground()を呼び出さないとRemoteServiceExceptionが発生します。
                startForegroundService(proxyIntent);
                vehicleLight.setImageAlpha(150);
            } else {
                // minSdkVersion < 26
                Toast.makeText(this,"ご利用中の端末は対象外となっております。",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
