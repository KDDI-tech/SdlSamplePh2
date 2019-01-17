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

import android.app.Activity;
import android.app.Service;
import android.os.AsyncTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * RSSとして公開されている情報を取得する
 */
public class RssAsyncReader extends AsyncTask<String, Void, String> {

    public class RssContent{
        private String title;
        private String url;
        private String id;
        private String pub;
        public RssContent(String id, String publishDate, String title, String url){
            this.title = title;
            this.url = url;
            this.id = id;
            this.pub = publishDate;
        }
        public String getTitle(){
            return title;
        }
        public String getUrl(){
            return url;
        }
        public String getId(){
            return id;
        }
        public String getPub(){
            return pub;
        }
    }

    private Service service;
    private Activity mActivity;
    private int activityTypeId;
    private PrefManager prefManager;
    private static final int RSS_ACTIVITY = 1;
    private static final int SDL_SERVICE = 2;
    private Map<Integer, RssContent> rssData = new HashMap<Integer, RssContent>();
    private boolean isSuccess = false;
    private String url = null;
    private String lastMod = null;
    private String newLastMod = null;

    public RssAsyncReader(RssActivity activity){
        this.mActivity = activity;
        activityTypeId = RSS_ACTIVITY;
    }
    public RssAsyncReader(SdlService service){
        this.service = service;
        activityTypeId = SDL_SERVICE;
    }

    @Override
    protected String doInBackground(String... params) {
        url = params[0];
        lastMod = params[1];
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(url);
            Element root = document.getDocumentElement();
            // rss type
            NodeList rss2 = root.getElementsByTagName("channel");
            NodeList atom = root.getElementsByTagName("entry");

            if(rss2.getLength() > 0) {
                //NodeList siteTitle = ((Element) rss2.item(0)).getElementsByTagName("title");
                NodeList item_list = root.getElementsByTagName("item");

                for (int i = 0; i < item_list.getLength(); i++) {
                    if(i > 10) {
                        break;
                    }
                    Element element = (Element) item_list.item(i);
                    NodeList item_title = element.getElementsByTagName("title");
                    NodeList item_link = element.getElementsByTagName("link");
                    NodeList item_pub = element.getElementsByTagName("pubDate");
                    String title = item_title.item(0).getFirstChild().getNodeValue();
                    String url = item_link.item(0).getFirstChild().getNodeValue();
                    String pub = item_pub.item(0).getFirstChild().getNodeValue();
                    if(lastMod.equals(pub)){
                        break;
                    } else {
                        if(i==0){
                            newLastMod = pub;
                        }
                        RssContent rssContent = new RssContent(pub, pub, title, url);
                        rssData.put(i, rssContent);
                    }
                }
                isSuccess = true;
            } else if(atom.getLength() > 0){
                // Atom
                NodeList item_list = root.getElementsByTagName("entry");
                for(int i = 0 ; i < atom.getLength(); i++){
                    if(i > 10) {
                        break;
                    }
                    Element element = (Element) item_list.item(i);
                    NodeList item_title = element.getElementsByTagName("title");
                    NodeList item_link = element.getElementsByTagName("link");
                    NodeList item_id = element.getElementsByTagName("id");
                    NodeList item_pub = element.getElementsByTagName("updated");
                    String title = item_title.item(0).getFirstChild().getNodeValue();
                    String url = item_link.item(0).getAttributes().getNamedItem("href").getNodeValue();
                    String id = item_id.item(0).getFirstChild().getNodeValue();
                    String pub = item_pub.item(0).getFirstChild().getNodeValue();
                    if(lastMod.equals(pub)) {
                        break;
                    } else {
                        if(i==0){
                            newLastMod = pub;
                        }
                        RssContent rssContent = new RssContent(id, pub, title, url);
                        rssData.put(i, rssContent);
                    }
                }
                isSuccess = true;
            } else {
                // not supported
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        // このメソッドは非同期処理の終わった後に呼び出されます
        super.onPostExecute(result);
        switch(activityTypeId) {
            case RSS_ACTIVITY:
                ((RssActivity) mActivity)._checkRssCallback(url, newLastMod, isSuccess, rssData);
                break;
            case SDL_SERVICE:
                ((SdlService) service)._checkRssCallback(url, isSuccess, rssData);
                break;
            default:
                break;
        }
    }
}
