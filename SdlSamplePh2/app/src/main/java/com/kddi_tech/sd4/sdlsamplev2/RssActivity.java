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
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

/**
 * RSSの購読を設定するための画面
 */
public class RssActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String RSS_LAST_MODIFIED_KEY = "rssLastModified";
    private static final String LOG_TAG = "LOG:[RssActivity]";
    private EditText rssTxt;
    private ImageButton clearBtn;
    private ImageButton saveBtn;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss);

        prefManager = PrefManager.getInstance(this.getApplicationContext());
        rssTxt = findViewById(R.id.rssText);
        clearBtn = findViewById(R.id.rssClear);
        saveBtn = findViewById(R.id.rssSave);
        // set event listener
        clearBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        //rssTxt.setOnFocusChangeListener(new _onFocusChange(rssTxt));
        // init val
        rssTxt.setText(prefManager.getPrefByStr(R.id.rssText, ""));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rssClear:
                prefManager.remove(getResources().getResourceName(R.id.rssText));
                rssTxt.setText("");
                break;
            case R.id.rssSave:
                _checkTextInput(v);
            default:
                break;
        }
    }

    /**
     * ※※※※※
     * URLをチェックする
     */
    private void _checkTextInput(View v) {
        EditText et = _getSiblingsEditText(v);
        if(et == null) {
            return;
        }
        String url = et.getText().toString();
        if(url.isEmpty()) {
           return;
        } else if(url.length() < 10) {
            // URLが短すぎる
            Toast.makeText(getApplicationContext(), "指定されたURLは本アプリでは対応できません", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "指定されたURLが正しいか確認中です", Toast.LENGTH_SHORT).show();
            String lastMod = prefManager.read(RSS_LAST_MODIFIED_KEY,"");
            new RssAsyncReader(this).execute(url,lastMod);
        }
    }

    /**
     * ※※※※※
     * 横並びになっている(レイアウトxml上、同じ階層にある)EditTextオブジェクトを取得する
     * @param v チェックアイコンのview
     * @return EditTextオブジェクト(見つからなかった場合はnull)
     */
    private EditText _getSiblingsEditText(View v) {
        ViewGroup row = (ViewGroup) v.getParent();
        EditText et = null;
        for (int itemPos = 0; itemPos < row.getChildCount(); itemPos++) {
            View view = row.getChildAt(itemPos);
            if(view instanceof EditText) {
                et = (EditText) view;
                break;
            }
        }
        return et;
    }


    /**
     * ※※※※※
     * Rssとして認識するかどうか確認を行った後の結果を表示する
     * @param url
     * @param lastMod
     * @param isSuccess
     * @param rssData
     */
    public synchronized void _checkRssCallback(String url, String lastMod, boolean isSuccess, Map<Integer, RssAsyncReader.RssContent> rssData) {
        String msg = null;
        if(isSuccess){
            prefManager.setPrefByStr(rssTxt.getId(),url);
            // 更新データのみを表示したい場合はアンコメントしてください
            //prefManager.write(RSS_LAST_MODIFIED_KEY,lastMod);
            msg = "URLを確認できました。走行中に変更があった場合は通知します";
            if(!rssData.isEmpty()) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.RssResult);
                layout.removeAllViews();
                for(int i: rssData.keySet()){
                    // set clickable textlink
                    final RssAsyncReader.RssContent content = rssData.get(i);
                    SpannableString ss = new SpannableString(content.getTitle());
                    ss.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            String url = content.getUrl();
                            Uri uri = Uri.parse(url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    },0,ss.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                    TextView tv = new TextView(this);
                    tv.setText(ss);
                    tv.setPadding(40,30,40,30);

                    tv.setAutoLinkMask(Linkify.WEB_URLS);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    layout.addView(tv);
                }
            }
        } else {
            msg = "指定されたURLは本アプリでは対応できません";
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
