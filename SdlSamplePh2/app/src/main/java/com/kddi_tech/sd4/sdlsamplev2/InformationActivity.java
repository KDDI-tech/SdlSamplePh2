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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

/**
 * 接続したことのある車両情報を表示するためのクラス
 */
public class InformationActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PADDING_TOP = 100;
    private static final int PADDING_OTHER = 0;
    private static final String EMPTY_STRING = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_wrapper);

        findViewById(R.id.btn_show_licnse).setOnClickListener(this);
        _setDisplay();
    }

    /**
     * ※※※※※
     * 接続した車両情報を表示する
     */
    private void _setDisplay() {
        PrefManager prefManager = PrefManager.getInstance(this);
        String json = prefManager.read(getResources().getString(R.string.pref_key_vin),EMPTY_STRING);
        LinearLayout layout = (LinearLayout) findViewById(R.id.info_wrapper);

        if(json.isEmpty()) {
            // 車両との接続データが無ければ、未接続用のメッセージを表示する
            TextView tv = new TextView(this);
            tv.setText(getResources().getString(R.string.never_connected_to_vehicle));
            tv.setPadding(PADDING_OTHER, PADDING_TOP,PADDING_OTHER,PADDING_OTHER);
            tv.setGravity(Gravity.CENTER);
            layout.addView(tv);
        } else {
            Gson gson = new Gson();
            ArrayList<String> arrayList = gson.fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());
            for (String s: arrayList) {
                // Vehicle形式で保存したjsonデータを復元する
                Vehicle vehicle = gson.fromJson(prefManager.read(s, EMPTY_STRING), Vehicle.class);
                // 表示中のviewに表示用のテンプレートを追加
                View view = getLayoutInflater().inflate(R.layout.activity_information_body, null);
                layout.addView(view);
                // テンプレートに値を反映
                ((TextView) view.findViewById(R.id.txt_label_vin)).setText(vehicle.getVin());
                ((TextView) view.findViewById(R.id.txt_label_maker)).setText(vehicle.getMaker());
                ((TextView) view.findViewById(R.id.txt_label_model)).setText(vehicle.getModel());
                ((TextView) view.findViewById(R.id.txt_label_year)).setText(vehicle.getModelYear());
                ((TextView) view.findViewById(R.id.txt_label_create_at)).setText(vehicle.getCreateAt());
                ((TextView) view.findViewById(R.id.txt_label_update_at)).setText(vehicle.getUpdateAt());
                ((TextView) view.findViewById(R.id.txt_label_fuel)).setText(vehicle.getFuelLevel());
                ((TextView) view.findViewById(R.id.txt_label_tire_front_left)).setText(vehicle.getTireMap().get(getResources().getString(R.string.tire_front_left)));
                ((TextView) view.findViewById(R.id.txt_label_tire_front_right)).setText(vehicle.getTireMap().get(getResources().getString(R.string.tire_front_right)));
                ((TextView) view.findViewById(R.id.txt_label_tire_rear_left)).setText(vehicle.getTireMap().get(getResources().getString(R.string.tire_rear_left)));
                ((TextView) view.findViewById(R.id.txt_label_tire_rear_right)).setText(vehicle.getTireMap().get(getResources().getString(R.string.tire_rear_right)));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_licnse:
                Intent i = new Intent(this, OssLicensesMenuActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}
