This repo has been moved to the KDDI Technology organization. It will no longer be updated here. For the latest information, please go to the following URL:    
[https://github.com/KDDI-Technology/SdlSamplePh2](https://github.com/KDDI-Technology/SdlSamplePh2)  

# SdlSamplePh2

### 本アプリケーションの位置づけ
[SdlSamplePh1](https://github.com/KDDI-tech?tab=repositories)の機能を拡張したアプリケーションになります。

### 実行環境、制限事項
以下の環境下での動作を確認しています。
+ Android Studio (version 3.1.4)
+ JDK (version 1.8.0_181)
+ Android API Level：26
  - targetSdkVersion：26
  - compileSdkVersion：26
+ 対向システム：[Manticore](https://smartdevicelink.com/resources/manticore/)※
  - Manticoreは、下記で構成されています。
  - [SDL Core v4.5.1](https://github.com/smartdevicelink/sdl_core/releases/tag/4.5.1)
  - [Generic HMI v0.4.1](https://github.com/smartdevicelink/generic_hmi/releases/tag/0.4.1)

`※`ここでの対向システムとは、「SDL対応Androidアプリケーションと通信を行い、画面表示を行うヘッドユニット相当の機能を提供するもの」とします。   

### アプリケーションで利用しているsdl_androidのバージョン
+ [sdl_android(4.6.3)](https://github.com/smartdevicelink/sdl_android/tree/4.6.3)

### ドキュメント
+ [開発ガイド(4.5.0)](https://github.com/smartdevicelink/sdl_android_guides/tree/4.5.0/)   
※開発ガイドの4.6系は存在しません。

### 注意事項
+ 本アプリケーション内で利用している[sdl_android]のバージョンは4.6.3ですが、開発ガイドは4.5.0から更新されていないため、
書かれている内容通りに作成しても(4.6.3では期待通りに)動作しないことがありますので、注意してください。   


### Manticoreの使い方
1. [Manticore](https://smartdevicelink.com/resources/manticore/)にアクセスします。
2. 「SIGN IN」からログイン、または「REGISTER」から利用者登録(無料)を行ってください。
3. ログイン後1のページにアクセスし、「LAUNCH MANTICORE」ボタンを押すことで、Manticoreが起動します。
4. Manticore起動後に表示される`URL`と`Port Number`は、本アプリケーション内に設定する必要`※`がありますので、控えておいてください。   
`※`Manticoreは起動する度にPortNumberが切り替わりますので、都度指定するようにしてください。


### アプリケーションのデプロイ先
以下のいずれかを用意してください。
+ Android Oreo以降の実機
+ AVD(エミュレータ)(Android Oreo以降のイメージに限る)

### アプリケーションの起動方法
1. 本リポジトリをclone(まだはDL)してください。
2. ブラウザ上で[Manticore](https://smartdevicelink.com/resources/manticore/)を起動します。
3. 起動後に表示されるURLとPort Numberを、local.propertiesに記入してください。
以下は記入例になります。
```
> 入力形式
manticore_port={PORT番号}
manticore_ipaddr={IPアドレス}
> 入力例
manticore_port=10726
manticore_ipaddr=m.sdl.tools
```

4. Build Variant：tcpDebugにしてビルドを行ってください。
5. 実機またはエミュレータのいずれかに対してデプロイを行うと動作します。

※TCP以外の接続方法は本アプリケーションではサポートしていません。


### 本アプリケーションでサポートしている動作
+ Fuel Level (燃料残量の通知)
+ Tire pressure (タイヤ空気圧の通知)
+ Head lamp status (周辺光とヘッドライトの状態を加味した通知)
+ RSSの通知   
	-> 車速(Speed)が0かつ、ブレーキ中(DriverBraking)に、登録しているRSSの情報を読み上げます)

### ソースコード(Javaファイル)について

|ファイル名|概要|
----|---- 
|MainActivity.java|アプリケーション起動時に表示される画面|
|SdlService.java|SDL Coreと通信するコントローラー。車両情報の取得や設定を管理しているサービスクラス|
|LockScreenActivity.java|車両がアプリケーションを起動した際に、Android側をロックするための画面|
|InformationActivity.java|接続したことのある車両情報を表示する画面|
|SettingsActivity.java|車両情報のイベントの設定画面|
|SettingsActivity.java|車両情報のイベントの設定画面|
|RssActivity.java|RSSの購読設定画面|
|RssAsyncReader.java|RSSを取得するAsyncTaskクラス|
|PrefManager.java|SharedPreferencesのマネージャクラス|
|UISettings.java|SdlServiceが車両に対して画面表示を行う際に、UIパーツ(画像・テキスト)を設定するためのモデルクラス|
|Vehicle.java|InformationActivityで車両情報を表示するためのモデルクラス|


### ライセンス情報について

##### 本ソフトウェアのライセンスについて
SdlSamplePh2 is released under the [Apache 2.0 license](LICENSE).

```
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
```

##### 本ソフトウェアで使用しているライセンスについて
```
本ソフトウェアには、様々なオープンソースソフトウェアが含まれています。
各ソフトウェア及びそのライセンス内容に関しては、本ソフトウェア内からご確認いただけますので、内容をご一読くださいますよう、よろしくお願い申し上げます。
```

### Disclaimer

This is not an officially supported KDDI Technology product.
