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

import android.support.annotation.Nullable;

/**
 * ※※※※※
 * HU(HMI)に対して画面描画を行う際の設定クラス
 */
public class UISettings {
    public enum EventType {
        Default,
        Greeting,
        Fuel,
        Tire,
        Headlight,
        Other
    }
    private EventType eventType;
    private String image1;
    private String image2;
    private String text1;
    private String text2;
    private String text3;
    private String text4;
    private int id = 0;

    public UISettings(EventType event, @Nullable String mainImage, @Nullable String subImage,
                      @Nullable String textField1, @Nullable String textField2,
                      @Nullable String textField3, @Nullable String textField4) {
        eventType = event;
        image1 = mainImage;
        image2 = subImage;
        text1 = (textField1 == null) ? "" : textField1;
        text2 = (textField2 == null) ? "" : textField2;
        text3 = (textField3 == null) ? "" : textField3;
        text4 = (textField4 == null) ? "" : textField4;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(@Nullable String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(@Nullable String image2) {
        this.image2 = image2;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(@Nullable String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(@Nullable String text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(@Nullable String text3) {
        this.text3 = text3;
    }

    public String getText4() {
        return text4;
    }

    public void setText4(@Nullable String text4) {
        this.text4 = text4;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
