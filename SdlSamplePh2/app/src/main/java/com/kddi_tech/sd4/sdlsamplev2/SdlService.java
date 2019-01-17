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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.LockScreenManager;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.RPCResponse;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.TTSChunkFactory;
import com.smartdevicelink.proxy.callbacks.OnServiceEnded;
import com.smartdevicelink.proxy.callbacks.OnServiceNACKed;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.interfaces.OnSystemCapabilityListener;
import com.smartdevicelink.proxy.rpc.AddCommand;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.AlertManeuverResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ButtonPressResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteCommandResponse;
import com.smartdevicelink.proxy.rpc.DeleteFileResponse;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteSubMenuResponse;
import com.smartdevicelink.proxy.rpc.DiagnosticMessageResponse;
import com.smartdevicelink.proxy.rpc.DialNumberResponse;
import com.smartdevicelink.proxy.rpc.DisplayCapabilities;
import com.smartdevicelink.proxy.rpc.EndAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.GenericResponse;
import com.smartdevicelink.proxy.rpc.GetDTCsResponse;
import com.smartdevicelink.proxy.rpc.GetInteriorVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.GetSystemCapabilityResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleData;
import com.smartdevicelink.proxy.rpc.GetVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.GetWayPointsResponse;
import com.smartdevicelink.proxy.rpc.HeadLampStatus;
import com.smartdevicelink.proxy.rpc.Image;
import com.smartdevicelink.proxy.rpc.ListFiles;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.MenuParams;
import com.smartdevicelink.proxy.rpc.OnAudioPassThru;
import com.smartdevicelink.proxy.rpc.OnButtonEvent;
import com.smartdevicelink.proxy.rpc.OnButtonPress;
import com.smartdevicelink.proxy.rpc.OnCommand;
import com.smartdevicelink.proxy.rpc.OnDriverDistraction;
import com.smartdevicelink.proxy.rpc.OnHMIStatus;
import com.smartdevicelink.proxy.rpc.OnHashChange;
import com.smartdevicelink.proxy.rpc.OnInteriorVehicleData;
import com.smartdevicelink.proxy.rpc.OnKeyboardInput;
import com.smartdevicelink.proxy.rpc.OnLanguageChange;
import com.smartdevicelink.proxy.rpc.OnLockScreenStatus;
import com.smartdevicelink.proxy.rpc.OnPermissionsChange;
import com.smartdevicelink.proxy.rpc.OnStreamRPC;
import com.smartdevicelink.proxy.rpc.OnSystemRequest;
import com.smartdevicelink.proxy.rpc.OnTBTClientState;
import com.smartdevicelink.proxy.rpc.OnTouchEvent;
import com.smartdevicelink.proxy.rpc.OnVehicleData;
import com.smartdevicelink.proxy.rpc.OnWayPointChange;
import com.smartdevicelink.proxy.rpc.PerformAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.PerformInteractionResponse;
import com.smartdevicelink.proxy.rpc.PutFile;
import com.smartdevicelink.proxy.rpc.PutFileResponse;
import com.smartdevicelink.proxy.rpc.ReadDIDResponse;
import com.smartdevicelink.proxy.rpc.ResetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.ScrollableMessageResponse;
import com.smartdevicelink.proxy.rpc.SendHapticDataResponse;
import com.smartdevicelink.proxy.rpc.SendLocationResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayout;
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetInteriorVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.Show;
import com.smartdevicelink.proxy.rpc.ShowConstantTbtResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SoftButton;
import com.smartdevicelink.proxy.rpc.Speak;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.StreamRPCResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleData;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.SystemRequestResponse;
import com.smartdevicelink.proxy.rpc.TextField;
import com.smartdevicelink.proxy.rpc.TireStatus;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.UpdateTurnListResponse;
import com.smartdevicelink.proxy.rpc.enums.AmbientLightStatus;
import com.smartdevicelink.proxy.rpc.enums.ComponentVolumeStatus;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.HMILevel;
import com.smartdevicelink.proxy.rpc.enums.ImageType;
import com.smartdevicelink.proxy.rpc.enums.LockScreenStatus;
import com.smartdevicelink.proxy.rpc.enums.RequestType;
import com.smartdevicelink.proxy.rpc.enums.SdlDisconnectedReason;
import com.smartdevicelink.proxy.rpc.enums.SoftButtonType;
import com.smartdevicelink.proxy.rpc.enums.SystemAction;
import com.smartdevicelink.proxy.rpc.enums.SystemCapabilityType;
import com.smartdevicelink.proxy.rpc.enums.VehicleDataEventStatus;
import com.smartdevicelink.proxy.rpc.enums.VehicleDataResultCode;
import com.smartdevicelink.proxy.rpc.listeners.OnRPCResponseListener;
import com.smartdevicelink.transport.BTTransportConfig;
import com.smartdevicelink.transport.BaseTransportConfig;
import com.smartdevicelink.transport.MultiplexTransportConfig;
import com.smartdevicelink.transport.TCPTransportConfig;
import com.smartdevicelink.transport.TransportConstants;
import com.smartdevicelink.transport.USBTransportConfig;
import com.smartdevicelink.util.CorrelationIdGenerator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class SdlService extends Service implements IProxyListenerALM, TextToSpeech.OnInitListener {

    private static final String LOG_TAG				= "[Log:[SdlService]]";
    private static final String DEBUG_TAG			= "[Log:[DEBUG]]";
    private static String APP_ID						= "0";  // set your own APP_ID
    private static Boolean USE_MANTICORE				= true;
    private static String APP_NAME					= null;
    private static int MANTICORE_TCP_PORT				= 0;
    private static String MANTICORE_IP_ADDRESS		= null;
    private static String NOTIFICATION_CHANNEL_ID		= null;
    private static final String SUPPORTED			= "supported";
    private static final String NONE_SUPPORTED		= "not supported";

    private SdlProxyALM proxy							= null;
    private LockScreenManager lockScreenManager		= new LockScreenManager();
    private PrefManager prefManager;

    // Settings(DisplayCapabilities)
    private DisplayCapabilities mDisplayCapabilities	= null;
    private ArrayList<String> mAvailableTemplates		= null;
    private ArrayList<TextField> mTextFields				= null;
    private Boolean mGraphicsSupported					= false;
    private Boolean mDisplayLayoutSupported			= false;
    private int mNumberOfTextFields						= 0;

    // アプリ初回接続時のフラグ
    private boolean firstLaunchHmiNone = true;

    // 取得可能な車両情報のMapデータ
    private Map<String, Boolean> usableVehicleData		= new HashMap<String, Boolean>();
    private static final String VD_FUEL_LEVEL			= "FUEL_LEVEL";
    private static final String VD_HEAD_LAMP_STATUS	= "HEAD_LAMP_STATUS";
    private static final String VD_TIRE_PRESSURE		= "TIRE_PRESSURE";
    private static final String VD_SPEED				= "SPEED";
    private static final String VD_BREAKING				= "DIVER_BREAKING";

    // 車両情報のsubscribe済フラグ
    private boolean isVehicleDataSubscribed			= false;
    // 画面表示切替用のQueue
    private static Queue<UISettings> uiQueue				= new LinkedList<UISettings>();
    // Templateの変更管理
    private static String currentTemplateName		= "DEFAULT";  // 現在表示しているテンプレート
    private static String reqTemplateName			= "GRAPHIC_WITH_TEXT";  // 変更要求をかける際のテンプレート
    private static int requestemplateID			= 0;  // 変更要求をかける際のID

    // Command
    private static final int COMMAND_ID_1		= 1;
    private static final int COMMAND_ID_2		= 2;
    private static final int COMMAND_ID_3		= 3;

    // SoftButton
    private static final int SOFT_BUTTON_ID_1	= 1;
    private static final int SOFT_BUTTON_ID_2	= 2;
    private static final int SOFT_BUTTON_ID_3	= 3;

    // image id
    // SdlProxyALM.setappicon()でHUにアイコンを出す際の識別キーとして使用
    private static final int APP_ICON_ID		= CorrelationIdGenerator.generateId();
    // image file name
    private List<String> remoteFiles;
    private static final String ICON_LOCK_SCREEN		= "sdl_lock_screen_img.png";
    private static final String ICON_TIRE				= "sdl_tire.png";
    private static final String ICON_HEADLIGHT			= "sdl_headlight.png";
    private static final String ICON_FUEL				= "sdl_fuel.png";
    private static final String ICON_FILENAME			= "sdl_hu_icon.png";
    private static final String PIC_CHARACTER			= "sdl_chara.png";
    private static final String PIC_SORRY				= "sdl_hu_sorry.png";

    // 主要機能のデータ
    private static AmbientLightStatus currentAmbientStatus	= AmbientLightStatus.UNKNOWN;
    private static final List<String> FUEL_SWITCH_LIST		= new ArrayList<String>() {{ add("seekSwitch1"); add("seekSwitch2"); add("seekSwitch3"); add("seekSwitch4"); add("seekSwitch5"); }};
    private static final List<String> FUEL_LEVEL_LIST		= new ArrayList<String>() {{ add("seekText1"); add("seekText2"); add("seekText3"); add("seekText4"); add("seekText5"); }};
    private static List<Integer> fuelLvThreshold		= new ArrayList<Integer>();    // FuelLevelの通知閾値
    private static int prevFuelLevel					= 0;
    private boolean isHeadlightTurnOn				= false;
    private boolean isHeadlightTurnOff				= false;

    // 画面描画コントロール用の変数
    private Timer uiChangeTimer						= null;
    private boolean isTimerWorked					= false;      // タイマーの動作状況(何らかの画面変更を行った後、TIMER_DELAY_MS時間経過するまではtrue)
    private boolean isChangeUIWorked				= false;
    private static final int TIMER_DELAY_MS		= 7000; // 画面内UIの(デフォルト時以外の)表示時間

    // RSS取得イベント(車両停止検知)用変数
    private static int latestSpeed = -1;
    private static VehicleDataEventStatus latestBreakState = VehicleDataEventStatus.NO;
    private static boolean detectVehicleStop = false;
    // TTS用変数
    private TextToSpeech tts;
    private boolean isTtsEnabled = false;
    private Map<Integer, String> ttsStandby = new HashMap<Integer,String>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        remoteFiles = new ArrayList<>();
        _connectForeground();
    }

    /**
     * ServiceをstartForegroundで起動させる
     */
    private void _connectForeground() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            APP_ID = getResources().getString(R.string.app_id);
            APP_NAME = getResources().getString(R.string.app_name);

            // BuildConfigにManticoreのフィールドがなければ動作をさせないようにする
            String manticorePort = BuildConfig.MANTICORE_PORT;
            MANTICORE_IP_ADDRESS = BuildConfig.MANTICORE_IP_ADDR;
            if(manticorePort == null || MANTICORE_IP_ADDRESS == null) {
                USE_MANTICORE = false;
            } else {
                MANTICORE_TCP_PORT = Integer.parseInt(manticorePort);
                USE_MANTICORE = true;
            }
            // パッケージ毎に一意のID値(長い文字列長の場合切り捨てられる場合があるようです)
            NOTIFICATION_CHANNEL_ID = getResources().getString(R.string.notif_channel_id);
            usableVehicleData.put(VD_FUEL_LEVEL,false);
            usableVehicleData.put(VD_HEAD_LAMP_STATUS,false);
            usableVehicleData.put(VD_TIRE_PRESSURE,false);
            usableVehicleData.put(VD_SPEED,false);
            usableVehicleData.put(VD_BREAKING,false);
            prevFuelLevel = 0;
            startForeground(1, _createNotification());
            tts = new TextToSpeech(this, this);
        }
    }

    /**
     * Android Oreo(v26)以降の端末向け対応
     * 通知チャネル(NotificationChannel)を登録し、通知用のインスタンスを返却する
     * @return Notification 作成した通知情報
     */
    private Notification _createNotification() {
        String name = getResources().getString(R.string.notif_channel_name);
        String description = getResources().getString(R.string.notif_channel_desctiption);
        int importance = NotificationManager.IMPORTANCE_HIGH; // デフォルトの重要度

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setSound(null, null);
            channel.setShowBadge(false);
            manager.createNotificationChannel(channel);
        }

        Notification builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.notif_content_title))
                .setContentText(getResources().getString(R.string.notif_content_text))
                .setSmallIcon(R.drawable.ic_sdl)
                .build();
        return builder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG,"onStartCommand called");
        // Serviceを2回目以降に起動する際は、OnCreateが呼ばれません。
        // Android Oreoから、Serviceのバックグラウンド・サービスの挙動が変わったため、
        // startForegroundをここからも呼べるようにする必要があります。
        if (! intent.getBooleanExtra(getResources().getString(R.string.is_first_connect),true)) {
            _connectForeground();
        }
        prefManager = prefManager.getInstance(getApplicationContext());
        boolean forceConnect = intent !=null && intent.getBooleanExtra(TransportConstants.FORCE_TRANSPORT_CONNECTED, false);
        if (proxy == null) {
            try {
                BaseTransportConfig transport = null;
                if(BuildConfig.TRANSPORT.equals("MBT")){
                    int securityLevel;
                    if(BuildConfig.SECURITY.equals("HIGH")){
                        securityLevel = MultiplexTransportConfig.FLAG_MULTI_SECURITY_HIGH;
                    }else if(BuildConfig.SECURITY.equals("MED")){
                        securityLevel = MultiplexTransportConfig.FLAG_MULTI_SECURITY_MED;
                    }else if(BuildConfig.SECURITY.equals("LOW")){
                        securityLevel = MultiplexTransportConfig.FLAG_MULTI_SECURITY_LOW;
                    }else{
                        securityLevel = MultiplexTransportConfig.FLAG_MULTI_SECURITY_OFF;
                    }
                    transport = new MultiplexTransportConfig(this, APP_ID, securityLevel);
                } else if(BuildConfig.TRANSPORT.equals("LBT")) {
                    transport = new BTTransportConfig();
                } else if(BuildConfig.TRANSPORT.equals("TCP")){
                    transport = new TCPTransportConfig(MANTICORE_TCP_PORT, MANTICORE_IP_ADDRESS, true);
                } else if(BuildConfig.TRANSPORT.equals("USB")) {
                    if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
                        if (intent != null && intent.hasExtra(UsbManager.EXTRA_ACCESSORY)) {
                            transport = new USBTransportConfig(getBaseContext(), (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY));
                        }
                    } else {
                        Log.e("SdlService", "Unable to start proxy. Android OS version is too low");
                    }
                }

                if(transport != null) {
                    if (USE_MANTICORE && BuildConfig.TRANSPORT.equals("TCP")) {
                        // SDL Coreに接続しにいく
                        proxy = new SdlProxyALM(this, APP_NAME, false, APP_ID, new TCPTransportConfig(MANTICORE_TCP_PORT, MANTICORE_IP_ADDRESS, false));
                    } else {
                        proxy = new SdlProxyALM(this.getBaseContext(), this, APP_NAME, true, APP_ID);
                    }
                }
            }catch(SdlException e) {
                if (proxy == null) {
                    stopForeground(Service.STOP_FOREGROUND_REMOVE);
                    stopSelf();
                }
                e.printStackTrace();
            }
        } else if(forceConnect){
            proxy.forceOnConnected();
        }
        return START_STICKY;
    }



    @Override
    public void onDestroy(){
        _disposeSyncProxy();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(notificationManager!=null){ //If this is the only notification on your channel
                notificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID);
            }
            stopForeground(Service.STOP_FOREGROUND_REMOVE);
        }
        if(tts != null) {
            tts.shutdown();
        }
        super.onDestroy();
    }

    /**
     * ※※※※※
     * Serviceを終了するための前処理
     */
    private void _disposeSyncProxy() {
        sendBroadcast(new Intent(LockScreenActivity.CLOSE_LOCK_SCREEN_ACTION));
        if (proxy != null) {
            try {
                proxy.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                proxy = null;
            }
        }
        firstLaunchHmiNone = true;
        isVehicleDataSubscribed = false;
    }

    /**
     * ※※※※※
     * DisplayCapabilitiesを取得し、画面表示が可能であれば表示する
     */
    private void _getDisplayCapabilities() {
        if (proxy == null) {
            return;
        }

        _detectDisplayCapabilities();
        if(mDisplayCapabilities != null) {
            Boolean gSupport = mDisplayCapabilities.getGraphicSupported();
            if (gSupport != null && gSupport.booleanValue()) {
                //mGraphicsSupported = true;
                _checkImageUploaded();
            }

            if (mDisplayCapabilities.getTextFields() != null) {
                mTextFields = new ArrayList<TextField>(mDisplayCapabilities.getTextFields());
            }

            mAvailableTemplates = new ArrayList<String>(mDisplayCapabilities.getTemplatesAvailable());
            if (mAvailableTemplates != null && mAvailableTemplates.contains(reqTemplateName)) {
                mDisplayLayoutSupported = true;
            }

            if (mAvailableTemplates != null) {
                /*
                // show enable template list
                for (String str : mAvailableTemplates) {
                    Log.i(DEBUG_TAG, "dispCapabilities：" + str);
                }
                */
            }

            /*
            // 用途不明につきコメントアウト
            DisplayType dt = mDisplayCapabilities.getDisplayType();
            if (dt == DisplayType.CID) {
                mNumberOfTextFields = 2;
            } else if (dt == DisplayType.GEN3_8_INCH) {
                mNumberOfTextFields = 3;
            } else if (dt == DisplayType.MFD3 ||
                        dt == DisplayType.MFD4 ||
                        dt == DisplayType.MFD5) {
                mNumberOfTextFields = 2;
            } else if (dt == DisplayType.NGN) {
                mNumberOfTextFields = 1;
            } else {
                mNumberOfTextFields = 1;
            }
            */

            if (mDisplayLayoutSupported) {
                _updateTemplate();
            }
        }
    }

    /**
     * SDL Coreに対してのリクエストを行う
     * テンプレート変更や、画像、テキスト、コマンド等の変更時に呼び出す
     * @param req RPCRequest
     */
    private void _sendRequest(RPCRequest req) {
        try{
            proxy.sendRPCRequest(req);
        }catch (SdlException e){
            e.printStackTrace();
        }
    }

    /**
     * ※※※※※
     * 画面表示中のテンプレートを別のものに切り替える
     */
    private void _updateTemplate() {
        Log.i(DEBUG_TAG, "Called updateTemplate");
        SetDisplayLayout setDisplayLayoutRequest = new SetDisplayLayout();
        setDisplayLayoutRequest.setDisplayLayout(reqTemplateName);
        // note : SetDisplayLayout.setCorrelationIDは明示的に指定しない。
        // getCorrelationIDで生成・取得した値をキーにID値判定に用いるようにする
        requestemplateID = setDisplayLayoutRequest.getCorrelationID();
        _sendRequest(setDisplayLayoutRequest);
    }

    /**
     * ※※※※※
     * SdlProxyALM.getDisplayCapabilities(deprecated)の代替機能
     * 定義済みの変数：
     * DisplayCapabilities mDisplayCapabilities
     *
     * このメソッドを呼び出すと、
     * mDisplayCapabilities は、SdlProxyALM.getDisplayCapabilities()と同等の振る舞いをします。
     *
     * @todo
     * SdlProxyALM.getXXXCapabilities()系は軒並みdeprecatedになっているので、必要に応じて同等の処理を行ってください。
     */
    private void _detectDisplayCapabilities () {
        if (mDisplayCapabilities != null) {
            return;
        }
        if (proxy.isCapabilitySupported(SystemCapabilityType.DISPLAY)) {
            proxy.getCapability(SystemCapabilityType.DISPLAY, new OnSystemCapabilityListener(){
                @Override
                public void onCapabilityRetrieved(Object capability){
                    mDisplayCapabilities = (DisplayCapabilities) capability;
                    Log.i(DEBUG_TAG, "getDisplayCapabilities Success");
                }
                @Override
                public void onError(String info){
                    Log.e(DEBUG_TAG, "Capability could not be retrieved: "+ info);
                }
            });
        }
    }


    // SDL method
    @Override
    public void onOnHMIStatus(OnHMIStatus notification) {
        Log.i(DEBUG_TAG, "OnHMIStatus : HmiLevel : "+ notification.getHmiLevel() + ", getFirstRun :"+notification.getFirstRun());

        if (notification.getHmiLevel().equals(HMILevel.HMI_FULL)) {
            // Other HMI (Show, PerformInteraction, etc.) would go here
            if(notification.getFirstRun()) {
                _registVehicleData();
                _setCommand();
                _showGreetingUI();
                firstLaunchHmiNone = false;
            }
        } else if (notification.getHmiLevel().equals(HMILevel.HMI_BACKGROUND)) {
            Log.i(DEBUG_TAG, "HMI Status : HMI_BACKGROUND");
            // Other app setup (SubMenu, CreateChoiceSet, etc.) would go here
        } else if (notification.getHmiLevel().equals(HMILevel.HMI_NONE)) {
            Log.i(DEBUG_TAG, "HMI Status : HMI_NONE");
            if(notification.getFirstRun()) {
                _getDisplayCapabilities();
            }
        }
    }

    /**
     * ※※※※※
     * GetVehicleDataで取得した車両情報を元に、車両がサポートしている情報をSharedPreferencesに保存する。
     * この際、サポートしていない項目と、SettingsAvctivityで設定した通知情報を加味して、
     * 必要のない車両情報はsubscribeしないようにする。
     */
    private void _registVehicleData() {
        GetVehicleData vdRequest = new GetVehicleData();
        vdRequest.setVin(true);
        vdRequest.setTirePressure(true);
        vdRequest.setFuelLevel(true);

        // RSSの情報が登録されていない場合、スキップする
        if(!prefManager.getPrefByStr(R.id.rssText,"").isEmpty()) {
            // 車両停止状態の判定用として
            vdRequest.setDriverBraking(true);
            vdRequest.setSpeed(true);
        }


        vdRequest.setOnRPCResponseListener(new OnRPCResponseListener() {

            Vehicle vehicle;
            TireStatus tire;

            @Override
            public void onResponse(int correlationId, RPCResponse response) {
                if(response.getSuccess()){
                    String vin = ((GetVehicleDataResponse) response).getVin();
                    LocalDateTime d = LocalDateTime.now();
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

                    vehicle = new Vehicle();
                    vehicle.setVin(vin);
                    vehicle.setCreateAt(d.format(f));
                    vehicle.setUpdateAt(d.format(f));
                    try {
                        vehicle.setMaker(proxy.getVehicleType().getMake());
                        vehicle.setModel(proxy.getVehicleType().getModel());
                        vehicle.setModelYear(proxy.getVehicleType().getModelYear());
                    } catch (SdlException e) {
                        e.printStackTrace();
                    }
                    // タイヤ空気圧の判定
                    tire = ((GetVehicleDataResponse) response).getTirePressure();
                    _checkTirePressureSupport(tire.getLeftFront().getStatus(), getResources().getString(R.string.tire_front_left));
                    _checkTirePressureSupport(tire.getRightFront().getStatus(), getResources().getString(R.string.tire_front_right));
                    _checkTirePressureSupport(tire.getLeftRear().getStatus(), getResources().getString(R.string.tire_rear_left));
                    _checkTirePressureSupport(tire.getRightRear().getStatus(), getResources().getString(R.string.tire_rear_right));
                    _checkTirePressureSupport(tire.getInnerLeftRear().getStatus(), getResources().getString(R.string.tire_inner_left));
                    _checkTirePressureSupport(tire.getInnerRightRear().getStatus(), getResources().getString(R.string.tire_inner_right));

                    // 燃料残量の判定
                    Double fuel = ((GetVehicleDataResponse) response).getFuelLevel();
                    if (fuel.intValue() < 0) {
                        vehicle.setFuelLevel(NONE_SUPPORTED);
                    } else {
                        vehicle.setFuelLevel(SUPPORTED);
                        usableVehicleData.put(VD_FUEL_LEVEL,true);
                    }

                    // RSSの情報が登録されていない場合、スキップする
                    if(!prefManager.getPrefByStr(R.id.rssText,"").isEmpty()) {
                        // 車速情報の取得判定
                        Double speed = ((GetVehicleDataResponse) response).getSpeed();
                        if (speed.intValue() < 0) {
                            vehicle.setSpeed(NONE_SUPPORTED);
                        } else {
                            vehicle.setSpeed(SUPPORTED);
                            usableVehicleData.put(VD_SPEED, true);
                        }

                        // ブレーキ情報の取得判定
                        VehicleDataEventStatus status = ((GetVehicleDataResponse) response).getDriverBraking();
                        // manticoreの場合、ブレーキの初期値がNOT_SUPPORTEDになっていて、永久に処理が走らない
                        //if (status.equals(VehicleDataEventStatus.NOT_SUPPORTED)) {
                        if (status == null) {
                            vehicle.setBreake(NONE_SUPPORTED);
                        } else {
                            vehicle.setBreake(SUPPORTED);
                            usableVehicleData.put(VD_BREAKING, true);
                        }
                    }

                    ArrayList<String> arrayList = new ArrayList<>();
                    Gson gson = new Gson();
                    String vinKey = getResources().getString(R.string.pref_key_vin);
                    String json = prefManager.read(vinKey,"");
                    boolean isNweCar = false; // 未登録の車に接続している場合True
                    if(json.isEmpty()) {
                        isNweCar = true;
                    } else {
                        arrayList = gson.fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());
                        if (!arrayList.contains(vin)) {
                            isNweCar = true;
                        }
                    }

                    // SharedPreferencesに車両情報が保持されているか確認し、
                    // 無ければ追加、あれば最終接続日時を更新する
                    if(isNweCar) {
                        // 車両識別番号のリストを保存する
                        arrayList.add(vin);
                        prefManager.write(vinKey, gson.toJson(arrayList));
                        prefManager.write(vin, gson.toJson(vehicle));
                    } else {
                        Vehicle existVehicle = gson.fromJson(prefManager.read(vin, ""), Vehicle.class);
                        existVehicle.setUpdateAt(d.format(f));
                        prefManager.write(vin, gson.toJson(existVehicle));
                    }

                    // ユーザの通知許可があるものに限り通知するようする
                    // TirePressure
                    if (!prefManager.read(getResources().getResourceEntryName(R.id.tireSwitch),true) ) {
                        usableVehicleData.put(VD_TIRE_PRESSURE,false);
                    } else {
                        _changeDisplayByTirePressure(tire);
                    }
                    // FuelLevelをSubscribeするかどうか
                    if(usableVehicleData.get(VD_FUEL_LEVEL)) {
                        for(int i = 0; i < FUEL_SWITCH_LIST.size(); i++){
                            if (prefManager.read(FUEL_SWITCH_LIST.get(i), true)) {
                                usableVehicleData.put(VD_FUEL_LEVEL,true);
                                int fuelLv = Integer.parseInt(prefManager.read(FUEL_LEVEL_LIST.get(i),"0"));
                                if(!fuelLvThreshold.contains(fuelLv)) {
                                    fuelLvThreshold.add(fuelLv);
                                    Collections.reverse(fuelLvThreshold);
                                }
                            }
                        }
                        if (fuelLvThreshold.size() == 0) {
                            usableVehicleData.put(VD_FUEL_LEVEL,false);
                        } else {
                            _changeDisplayByFuelLevel(fuel);
                        }
                    }
                    // HeadLightをSubscribeするかどうか
                    isHeadlightTurnOn = prefManager.read(getResources().getResourceEntryName(R.id.lightOnSwitch),true);
                    isHeadlightTurnOff = prefManager.read(getResources().getResourceEntryName(R.id.lightOffSwitch),true);

                    if (isHeadlightTurnOn || isHeadlightTurnOff) {
                        usableVehicleData.put(VD_HEAD_LAMP_STATUS,true);
                    }
                    _subscribeVehicleData();
                }else{
                    Log.i(LOG_TAG, "GetVehicleData was rejected.");
                }
            }
            private void _checkTirePressureSupport(ComponentVolumeStatus status, String str) {
                if(ComponentVolumeStatus.NOT_SUPPORTED.equals(status)) {
                    vehicle.setTireMap(str, NONE_SUPPORTED);
                } else {
                    vehicle.setTireMap(str, SUPPORTED);
                    usableVehicleData.put(VD_TIRE_PRESSURE,true);
                }
            }
        });
        _sendRequest(vdRequest);
    }

    /**
     * ※※※※※
     * コマンド(メニュー)をHUに表示する
     * どのコマンドが選択されたかは、onOnCommand()で判定を行う
     */
    private void _setCommand() {
        MenuParams params = new MenuParams();
        params.setParentID(0);
        params.setPosition(0);
        params.setMenuName(getResources().getString(R.string.cmd_exit));

        AddCommand command = new AddCommand();
        command.setCmdID(COMMAND_ID_1);
        command.setMenuParams(params);
        command.setVrCommands(Collections.singletonList(getResources().getString(R.string.cmd_exit)));
        _sendRequest(command);
    }

    /**
     * ※※※※※
     * Greetingメッセージを表示する
     */
    private void _showGreetingUI() {
        UISettings ui = new UISettings(UISettings.EventType.Greeting, PIC_CHARACTER,null,
                "こんにちは！あなたの運転のサポートを担当する「イラスト子」です！",
                "パワーアップするために皆様のコメントお待ちしています！",
                null,
                null);
        _addChangeUIQueue(ui);
        //_showSoftButtons();
    }

    /**
     * ※※※※※
     * 車両情報に変更があった際、通知するように要求する
     */
    private void _subscribeVehicleData() {
        Log.i(LOG_TAG, "subscribeVehicleData." + isVehicleDataSubscribed);

        if(isVehicleDataSubscribed) {
            return;
        }
        SubscribeVehicleData subscribeRequest = new SubscribeVehicleData();
        if(usableVehicleData.get(VD_HEAD_LAMP_STATUS)) {
            subscribeRequest.setHeadLampStatus(true);
        }
        if(usableVehicleData.get(VD_FUEL_LEVEL)) {
            subscribeRequest.setFuelLevel(true);
        }
        if(usableVehicleData.get(VD_TIRE_PRESSURE)) {
            subscribeRequest.setTirePressure(true);
        }
        if(usableVehicleData.get(VD_SPEED)) {
            subscribeRequest.setSpeed(true);
        }
        if(usableVehicleData.get(VD_BREAKING)) {
            subscribeRequest.setDriverBraking(true);
        }
        _sendRequest(subscribeRequest);
    }

    /**
     * ※※※※※
     * 画像の有無を確認し、必要に応じて画像をアップロードする
      */
    private void _checkImageUploaded() {
        ListFiles listFiles = new ListFiles();
        listFiles.setOnRPCResponseListener(new OnRPCResponseListener() {

            @Override
            public void onResponse(int correlationId, RPCResponse response) {
                if(response.getSuccess()){
                    remoteFiles = ((ListFilesResponse) response).getFilenames();
                }
                // Check the mutable set for the AppIcon
                // If not present, upload the image
                if(remoteFiles== null || !remoteFiles.contains(SdlService.ICON_FILENAME)){
                    _uploadImages();
                }else{
                    // If the file is already present, send the SetAppIcon request
                    /*
                    for (String str :remoteFiles) {
                    }
                    */
                    try {
                        proxy.setappicon(ICON_FILENAME, CorrelationIdGenerator.generateId());
                    } catch (SdlException e) {
                        e.printStackTrace();
                    }
                }

                // Check the mutable set for the SDL image
                // If not present, upload the image
                if(remoteFiles== null || !remoteFiles.contains(SdlService.ICON_LOCK_SCREEN)){
                    _imageUploader(R.drawable.ic_lock, ICON_LOCK_SCREEN, CorrelationIdGenerator.generateId(), true);
                }
            }
        });
        _sendRequest(listFiles);
    }

    /**
     * 複数の画像をアップロードさせる
     */
    private void _uploadImages(){
        // icon
        _imageUploader(R.drawable.ic_application_icon, ICON_FILENAME, APP_ICON_ID, true);
        // 車両データの警告用アイコン
        _imageUploader(R.drawable.tire, ICON_TIRE, CorrelationIdGenerator.generateId(), true);
        _imageUploader(R.drawable.fuel, ICON_FUEL, CorrelationIdGenerator.generateId(), true);
        _imageUploader(R.drawable.headlight, ICON_HEADLIGHT, CorrelationIdGenerator.generateId(), true);
        // ナビキャラクター
        _imageUploader(R.drawable.pic_welcome, PIC_CHARACTER, CorrelationIdGenerator.generateId(), true);
    }

    /**
     * 画像ファイルを削除する
     */
    private void _removeImage(){
        /*
        DeleteFile deleteFileRequest = new DeleteFile();
        deleteFileRequest.setSdlFileName(SdlService.ICON_FILENAME);
        _sendRequest(deleteFileRequest);
        */
    }

    /**
     * 画像をアップロードする
     * @param resource リソースID
     * @param imageName 画像名(HUで保持されるファイル名)
     * @param correlationId リクエストID
     * @param isPersistent 永続化フラグ
     */
    private void _imageUploader(int resource, String imageName, int correlationId, boolean isPersistent){
        PutFile putFile = new PutFile();
        putFile.setFileType(FileType.GRAPHIC_PNG);
        putFile.setSdlFileName(imageName);
        putFile.setCorrelationID(correlationId);
        putFile.setPersistentFile(isPersistent);
        putFile.setSystemFile(false);
        putFile.setBulkData(_contentsOfResource(resource));
        _sendRequest(putFile);
    }

    private byte[] _contentsOfResource(int resource) {
        InputStream is = null;
        try {
            is = getResources().openRawResource(resource);
            ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
            final int bufferSize = 4096;
            final byte[] buffer = new byte[bufferSize];
            int available;
            while ((available = is.read(buffer)) >= 0) {
                os.write(buffer, 0, available);
            }
            return os.toByteArray();
        } catch (IOException e) {
            Log.w(LOG_TAG, "Can't read icon file", e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * SDL標準機能。
     * 画像アップロードリクエストの後に、その結果が通知される
     * @param response
     */
    @Override
    public void onPutFileResponse(PutFileResponse response) {
        Log.i(DEBUG_TAG, "onPutFileResponse from SDL");

        if(response.getCorrelationID() == APP_ICON_ID){ //If we have successfully uploaded our icon, we want to set it
            try {
                Log.i(LOG_TAG,"iconファイルを設置しました");
                proxy.setappicon(ICON_FILENAME, CorrelationIdGenerator.generateId());
            } catch (SdlException e) {
                e.printStackTrace();
            }
        } else {
            // Manticoreの場合、5-6件程度まで登録することができる。
            Log.i(LOG_TAG, "file upload : result -> " +
                    "ID：" + response.getCorrelationID() +
                    ", result-code : "+response.getResultCode()+
                    ", getSuccess: " + response.getSuccess()+
                    ", getSpaceAvailable: " + response.getSpaceAvailable());
        }
    }


    /**
     * SDL標準機能。
     * 車両データの登録結果が返却されます。
     * @param response 登録結果
     */
    @Override
    public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse response) {
        // note : 複数の車両データを登録した場合、responseにてそれぞれの登録結果が判定可能です。
        Log.i(LOG_TAG, "SubscribeVehicleDataResponse response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

        if (response.getSuccess()) {
            if (response.getFuelLevel() != null) {
                usableVehicleData.put(VD_FUEL_LEVEL, response.getFuelLevel().getResultCode().equals(VehicleDataResultCode.SUCCESS));
            }
            if (response.getHeadLampStatus() != null) {
                usableVehicleData.put(VD_HEAD_LAMP_STATUS, response.getHeadLampStatus().getResultCode().equals(VehicleDataResultCode.SUCCESS));
            }
            if (response.getTirePressure() != null) {
                usableVehicleData.put(VD_TIRE_PRESSURE, response.getTirePressure().getResultCode().equals(VehicleDataResultCode.SUCCESS));
            }
            if (response.getSpeed() != null) {
                usableVehicleData.put(VD_SPEED, response.getSpeed().getResultCode().equals(VehicleDataResultCode.SUCCESS));
            }
            if (response.getDriverBraking() != null) {
                usableVehicleData.put(VD_BREAKING, response.getDriverBraking().getResultCode().equals(VehicleDataResultCode.SUCCESS));
            }

            if (usableVehicleData.get(VD_SPEED) || usableVehicleData.get(VD_BREAKING)) {
                detectVehicleStop = true;
            }
            if (usableVehicleData.get(VD_FUEL_LEVEL) ||
                    usableVehicleData.get(VD_HEAD_LAMP_STATUS) ||
                    usableVehicleData.get(VD_TIRE_PRESSURE) ||
                    detectVehicleStop) {
                isVehicleDataSubscribed = true;
            }
        }

        if (! isVehicleDataSubscribed) {
            UISettings ui = new UISettings(UISettings.EventType.Other, PIC_SORRY,null,"ご乗車中のお車ではお手伝い出来ることがなさそうです。", "別の車にてお試しください。", null, null);
            _addChangeUIQueue(ui);
        }
    }

    /**
     * SDL標準機能
     * 車両データに変更があった場合、このメソッドに通知されます。
     * ※notificationには変更のあったデータのみが格納されます。
     * 例：`beltStatus`と`speed`の2つのデータをsubscribeしている場合に`speed`が変更された場合、
     * notificationには`beltStatus`のデータは含まれません。
     * @param notification
     */
    @Override
    public void onOnVehicleData(OnVehicleData notification) {
        Log.i(LOG_TAG, "OnVehicleData from SDL: " + notification);
        if (usableVehicleData.get(VD_HEAD_LAMP_STATUS) && notification.getHeadLampStatus() != null) {
            _changeDisplayByHeadLampStatus(notification.getHeadLampStatus());
        }
        if (usableVehicleData.get(VD_FUEL_LEVEL) && notification.getFuelLevel() != null) {
            _changeDisplayByFuelLevel(notification.getFuelLevel());
        }
        if (usableVehicleData.get(VD_TIRE_PRESSURE) && notification.getTirePressure() != null) {
            _changeDisplayByTirePressure(notification.getTirePressure());
        }
        if(notification.getSpeed() != null){
            latestSpeed = notification.getSpeed().intValue();
            if(latestSpeed == 0) {
                _checkVehicleDriveState();
            }
        }
        if(notification.getDriverBraking() != null){
            latestBreakState = notification.getDriverBraking();
            if(latestBreakState.equals(VehicleDataEventStatus.YES)) {
                _checkVehicleDriveState();
            }
        }
    }

    /**
     * ※※※※※
     * 車両の走行状態(停止中かどうか)
     * 登録されているRSS情報からデータを取得する
     */
    private void _checkVehicleDriveState() {
        // (瞬間的な情報なので実用向きではないけれども)車両が停止して3秒程度動かない場合に
        // RSS情報を取得しにいく
        try {
            Thread.sleep(3 * 1000);
            if(latestSpeed == 0 && latestBreakState.equals(VehicleDataEventStatus.YES)) {
                String url = prefManager.getPrefByStr(R.id.rssText,"");
                if(!url.isEmpty()) {
                    String lastMod = prefManager.read(RssActivity.RSS_LAST_MODIFIED_KEY, "");
                    new RssAsyncReader(this).execute(url, lastMod);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * ※※※※※
     * 取得したRSSを表示する
     * @param url
     * @param isSuccess
     * @param rssData
     */
    public void _checkRssCallback(String url, boolean isSuccess, Map<Integer, RssAsyncReader.RssContent> rssData) {
        if(!rssData.isEmpty()) {
            for(int i: rssData.keySet()){
                // 表示切替までの時間が長いため、デモとしては表示件数を制限する
                if(i>=3) {
                    break;
                }
                rssData.get(i).getTitle();
                UISettings ui = new UISettings(UISettings.EventType.Other, PIC_CHARACTER , null,
                        rssData.get(i).getTitle(), null,null, null);
                _addChangeUIQueue(ui);
            }
        }
    }

    /**
     * ※※※※※
     * タイヤ空気圧に変更があった場合の処理を定義しています
     * @param tire TireStatus
     */
    private void _changeDisplayByTirePressure(TireStatus tire) {
        ComponentVolumeStatus inLeft = tire.getInnerLeftRear().getStatus();
        ComponentVolumeStatus inRight = tire.getInnerRightRear().getStatus();
        ComponentVolumeStatus frontLeft = tire.getLeftFront().getStatus();
        ComponentVolumeStatus frontRight = tire.getRightFront().getStatus();
        ComponentVolumeStatus rearLeft = tire.getLeftRear().getStatus();
        ComponentVolumeStatus rearRight = tire.getRightRear().getStatus();

        String textfield1 = _checkTirePressure(ComponentVolumeStatus.LOW, frontLeft, frontRight, rearLeft, rearRight, inLeft, inRight);
        String textfield2 = _checkTirePressure(ComponentVolumeStatus.ALERT, frontLeft, frontRight, rearLeft, rearRight, inLeft, inRight);
        String textfield3 = _checkTirePressure(ComponentVolumeStatus.FAULT, frontLeft, frontRight, rearLeft, rearRight, inLeft, inRight);

        if (textfield1 != null) {
            textfield1 = textfield1.concat("の空気圧が低くなっています。");
        }

        if (textfield2 != null) {
            if (textfield3 != null) {
                textfield2 = String.join("、", textfield2,textfield3);
            }
            textfield2 = textfield2.concat("に異常を検知しました。");
        } else if (textfield3 != null) {
            textfield2 = textfield3.concat("に異常を検知しました。");
            textfield3 = null;
        }
        if (textfield1 == null && textfield2 != null) {
            textfield1 = textfield2;
            textfield2 = null;
        }

        if (textfield1 != null) {
            UISettings ui = new UISettings(UISettings.EventType.Tire, ICON_TIRE,null,textfield1, textfield2, null, null);
            _addChangeUIQueue(ui);
        }
    }

    /**
     * ※※※※※
     * タイヤ空気圧のチェックを行い、指定したステータスと一致したものを文字列ベースで連結して返却する
     * @param checkStatus ComponentVolumeStatusのインスタンス
     * @param frontLeft 前輪(左)の状態
     * @param frontRight 前輪(右)の状態
     * @param rearLeft 後輪(左)の状態
     * @param rearRight 後輪(右)の状態
     * @param inLeft 中輪(左)の状態
     * @param inRight 中輪(右)の状態
     * @return checkStatusで指定された状態と一致したタイヤ情報
     */
    private String _checkTirePressure(ComponentVolumeStatus checkStatus, ComponentVolumeStatus frontLeft, ComponentVolumeStatus frontRight, ComponentVolumeStatus rearLeft, ComponentVolumeStatus rearRight, ComponentVolumeStatus inLeft, ComponentVolumeStatus inRight) {
        List<String> list = new ArrayList<String>();
        if (checkStatus.equals(frontLeft)) {
            list.add(getResources().getString(R.string.tire_front_left));
        }
        if (checkStatus.equals(frontRight)) {
            list.add(getResources().getString(R.string.tire_front_right));
        }
        if (checkStatus.equals(rearLeft)) {
            list.add(getResources().getString(R.string.tire_rear_left));
        }
        if (checkStatus.equals(rearRight)) {
            list.add(getResources().getString(R.string.tire_rear_right));
        }
        if (checkStatus.equals(inLeft)) {
            list.add(getResources().getString(R.string.tire_inner_left));
        }
        if (checkStatus.equals(inRight)) {
            list.add(getResources().getString(R.string.tire_inner_right));
        }
        if (list != null && list.size() != 0) {
            return String.join("、", list);
        }
        return null;
    }

    /**
     * ※※※※※
     * 残燃料状態に応じてメッセージを表示する
     * @param fuelLevel 燃料残量
     */
    private void _changeDisplayByFuelLevel(Double fuelLevel) {
        int fuel = fuelLevel.intValue();
        // doubleをintに変換したことで、同じ整数値が最大10回呼ばれるため、前回の値と比較をする
        // ex.30.9%～30.0%までのdouble値がすべて30%(int)となる
        if(fuel == prevFuelLevel) {
            return;
        }
        prevFuelLevel = fuel;
        if(fuelLvThreshold.contains(fuel)){
            // 30%を切ったらGSを探すように通知する
            String str1 = "燃料の残量が" + fuel + "%になりました。";
            String str2 = (fuel <= 30) ? "そろそろガソリンスタンドを探しましょう。" : "";
            UISettings ui = new UISettings(UISettings.EventType.Fuel, ICON_FUEL,null,str1,str2,null,null);
            _addChangeUIQueue(ui);
        }
    }

    /**
     * ※※※※※
     * ヘッドランプステータスの状態変更通知があった際の処理
     * @param lampStatus onOnVehicleData()で取得したnotification.getHeadLampStatus()
     */
    private void _changeDisplayByHeadLampStatus(HeadLampStatus lampStatus) {
        AmbientLightStatus lightStatus = lampStatus.getAmbientLightStatus();
        if (_checkAmbientStatusIsNight(lightStatus) && isHeadlightTurnOn) {
            if (! _checkAnyHeadLightIsOn(lampStatus)){
                UISettings ui = new UISettings(UISettings.EventType.Headlight, ICON_HEADLIGHT, null,
                        "ヘッドライトが点灯してませんが大丈夫ですか？","安全運転を心がけてください。",null, null);
                _addChangeUIQueue(ui);
            }
        } else if (lightStatus.equals(AmbientLightStatus.DAY) && isHeadlightTurnOff) {
            if(_checkAnyHeadLightIsOn(lampStatus)) {
                UISettings ui = new UISettings(UISettings.EventType.Headlight, ICON_HEADLIGHT, null,
                        "ヘッドライトが点灯していませんか？","まだ明るいようなので、消灯してはいかがでしょうか？",null, null);
                _addChangeUIQueue(ui);
            }
        }
        // @todo
        // 前回通知時の値と比較して、周辺光の推移を判定し、表示メッセージを変える
        //currentAmbientStatus = lightStatus;
    }

    /**
     * ※※※※※
     * 周辺光センサーの値が夜(Twilight_1～4、Night)かどうか判定する
     * @param lightStatus AmbientLightStatus
     * @return 周辺光が夜に該当する場合Trueを返却する
     */
    private boolean _checkAmbientStatusIsNight(AmbientLightStatus lightStatus) {
        if (lightStatus.equals(AmbientLightStatus.TWILIGHT_1) ||
                lightStatus.equals(AmbientLightStatus.TWILIGHT_2) ||
                lightStatus.equals(AmbientLightStatus.TWILIGHT_3) ||
                lightStatus.equals(AmbientLightStatus.TWILIGHT_4) ||
                lightStatus.equals(AmbientLightStatus.NIGHT)) {
            return true;
        }
        return false;
    }

    /**
     * ※※※※※
     * ハイビームかロービームのいずれかが点灯状態にあるか確認する
     * @param lampStatus HeadLampStatus
     * @return いずれかが点灯状態の場合Trueを返却する
     */
    private boolean _checkAnyHeadLightIsOn(HeadLampStatus lampStatus){
        if(lampStatus.getHighBeamsOn() || lampStatus.getLowBeamsOn()){
            return true;
        }
        return false;
    }

    // 画面表示のキューイング
    /**
     * SDL 標準機能。
     * Showリクエストのレスポンスを受信する部分になります。
     * @param response Showリクエストの結果
     */
    @Override
    public void onShowResponse(ShowResponse response) {
        Log.i(LOG_TAG, "Show response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
        if(response.getSuccess()){
            if(ttsStandby.containsKey(response.getCorrelationID())) {
                _ttsSpeech(ttsStandby.get(response.getCorrelationID()), String.valueOf(response.getCorrelationID()));
            }
        }
        if(uiQueue.isEmpty()) {
            return;
        }
        if(response.getCorrelationID() ==  uiQueue.peek().getId()){
            _waitTimer();
        } else {
            Log.w(LOG_TAG, "who used uncontrolled 'Show' request ?");
        }
    }

    /**
     * ※※※※※
     * HU上にデフォルト画面用のアイコン、文字を表示するように設定する
     */
    private void _showDefaultUI() {
        UISettings ui = new UISettings(UISettings.EventType.Default, PIC_CHARACTER,null,"デフォルト画面になります。",null,null,null);
        _addChangeUIQueue(ui);
    }

    /**
     * ※※※※※
     * HUに表示したい画像、テキスト情報をキューに格納する
     * @param ui UISettings 表示したい情報が格納されたUISettings
     */
    private void _addChangeUIQueue(UISettings ui){
        uiQueue.offer(ui);
        _checkNextQueue();
    }

    /**
     * ※※※※※
     * 画面の表示コントロールをするためのタイマー機能。
     * 何らかの画面を表示した後、一定時間経過すると、デフォルト画面になるようにする。
     */
    private void _waitTimer() {
        isChangeUIWorked = false;
        if(isTimerWorked) {
            return;
        }
        isTimerWorked = true;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                isTimerWorked = false;
                UISettings ui = uiQueue.poll();
                if(uiQueue.isEmpty()){
                    if (! ui.getEventType().equals(UISettings.EventType.Default)) {
                        _showDefaultUI();
                    }
                } else {
                    _checkNextQueue();
                }
            }
        };
        int delayTime = TIMER_DELAY_MS;
        UISettings ui = uiQueue.peek();
        if(ui.getEventType().equals(UISettings.EventType.Default)) {
            delayTime = 0;
        }
        uiChangeTimer = new Timer(false);
        uiChangeTimer.schedule(task, delayTime);
    }

    /**
     * ※※※※※
     * キューに格納されている画面情報を元に(表示可能なタイミングになったら)HUに表示リクエストを出す
     */
    private synchronized void _checkNextQueue() {
        // 次の画面変更があれば、一定時間経過後に表示するようにする
        if (!uiQueue.isEmpty() && ! isTimerWorked && ! isChangeUIWorked) {

            isChangeUIWorked = true;
            int id = CorrelationIdGenerator.generateId();
            uiQueue.peek().setId(id);
            UISettings que = uiQueue.peek();

            Show show = new Show();
            Image image;

            if(que.getText1() != null) {
                show.setMainField1(que.getText1());
            }
            if(que.getText2() != null) {
                show.setMainField2(que.getText2());
            }
            if(que.getText3() != null) {
                show.setMainField3(que.getText3());
            }
            if(que.getText4() != null) {
                show.setMainField4(que.getText4());
            }
            if(que.getImage1() != null) {
                image = new Image();
                image.setValue(que.getImage1());
                image.setImageType(ImageType.DYNAMIC);
                show.setGraphic(image);
            }
            if(que.getImage2() != null) {
                image = new Image();
                image.setValue(que.getImage1());
                image.setImageType(ImageType.DYNAMIC);
                show.setSecondaryGraphic(image);
            }
            // TTS
            if(que.getEventType() != UISettings.EventType.Default &&
                    que.getEventType() != UISettings.EventType.Greeting) {
                ttsStandby.put(que.getId(), que.getText1());
            }

            show.setCorrelationID(que.getId());
            _sendRequest(show);
            // response onShowResponse() called
        }
    }

    /**
     * SDL標準機能。
     * テンプレート変更リクエスト後に、成否が返却される
     * ※responseにgetXXXXCapabilities()系のメソッドが用意されているものの、少なくともManticore環境ではNullが返却される
     * @param response
     */
    @Override
    public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse response) {
        Log.i(LOG_TAG, "SetDisplayLayout response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
        /*
        if (response.getSuccess()) {
            if (response.getCorrelationID() != null && response.getCorrelationID().equals(requestemplateID)) {
                // テンプレートが指定したものに変更されていた場合に何かなの処理を行う場合はここで行う
            }
        }
        */
    }

    /**
     * SDL標準機能。
     * HU接続後ロックスクリーン用の画像が、HUから降ってくる場合の処理
     * @param notification
     */
    @Override
    public void onOnLockScreenNotification(OnLockScreenStatus notification) {
        Log.i(LOG_TAG, "OnLockScreenNotification: " + notification);
        if(notification.getHMILevel() == HMILevel.HMI_FULL && notification.getShowLockScreen() == LockScreenStatus.REQUIRED) {
            Intent showLockScreenIntent = new Intent(this, LockScreenActivity.class);
            showLockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(lockScreenManager.getLockScreenIcon() != null){
                // HUからロックスクリーン用のアイコンが取得できた場合、デフォルトで設定していた画像は上書きする
                showLockScreenIntent.putExtra(LockScreenActivity.LOCKSCREEN_BITMAP_EXTRA, lockScreenManager.getLockScreenIcon());
            }
            startActivity(showLockScreenIntent);
        }else if(notification.getShowLockScreen() == LockScreenStatus.OFF){
            sendBroadcast(new Intent(LockScreenActivity.CLOSE_LOCK_SCREEN_ACTION));
        }
    }

    /**
     * ソフトボタンを表示するサンプル
     * (GRAPHIC_WITH_TEXTのテンプレートでは、ソフトボタンが表示されませんので、別のテンプレートを指定する必要があります)
     * またソフトボタンを押した際は、onOnButtonPressやonOnButtonEventで確認できます。
     */
    private void _showSoftButtons() {

        SoftButton sb1 = new SoftButton();
        SoftButton sb2 = new SoftButton();
        SoftButton sb3 = new SoftButton();
        SoftButton sb4 = new SoftButton();
        SoftButton sb5 = new SoftButton();
        SoftButton sb6 = new SoftButton();

        sb1.setSoftButtonID(SOFT_BUTTON_ID_1);
        sb1.setText("ボタン１");
        sb1.setType(SoftButtonType.SBT_TEXT);
        sb1.setIsHighlighted(false);
        sb1.setSystemAction(SystemAction.DEFAULT_ACTION);

        sb2.setSoftButtonID(SOFT_BUTTON_ID_2);
        sb2.setText("ボタン２");
        sb2.setType(SoftButtonType.SBT_TEXT);
        sb2.setIsHighlighted(false);
        sb2.setSystemAction(SystemAction.DEFAULT_ACTION);

        sb3.setSoftButtonID(SOFT_BUTTON_ID_3);
        sb3.setText("ボタン３");
        sb3.setType(SoftButtonType.SBT_TEXT);
        sb3.setSystemAction(SystemAction.DEFAULT_ACTION);

        Show show = new Show();
        Vector<SoftButton> softButtons = new Vector<SoftButton>();
        softButtons.add(sb1);
        softButtons.add(sb2);
        softButtons.add(sb3);
        show.setSoftButtons(softButtons);
        _sendRequest(show);
    }

    /**
     * SDL標準機能。
     * コマンド(サブメニュー)を選択した際のレスポンスが取得できる
     * @param notification
     */
    @Override
    public void onOnCommand(OnCommand notification) {
        Log.i(LOG_TAG, "onOnCommand");
        Integer id = notification.getCmdID();
        if(id != null) {
            Log.i(DEBUG_TAG,"Button Pess : " + id);
            switch (id) {
                case COMMAND_ID_1:
                    // set data to MainActivity
                    Intent broadcast = new Intent();
                    broadcast.putExtra(getResources().getString(R.string.is_first_connect), false);
                    broadcast.setAction(getResources().getString(R.string.action_service_close));
                    getBaseContext().sendBroadcast(broadcast);
                    this.onDestroy();
                    break;
                default:
                    Log.i(DEBUG_TAG,"Button Pess : default " + id);
                    break;
            }
        }
    }

    /**
     * TTS：initialize
     * @param status init status
     */
    @Override
    public void onInit(int status) {
        if (TextToSpeech.SUCCESS == status) {
            Locale locale = Locale.JAPAN;
            if(tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                isTtsEnabled = true;
                tts.setLanguage(locale);
            } else {
                Log.w(LOG_TAG,"言語設定に日本語を選択できませんでした");
                isTtsEnabled = true;
            }
        } else {
            isTtsEnabled = false;
        }
    }

    /**
     * ※※※※※
     * TTS：指定した文字列を読み上げさせる
     * @param str TTSで読み上げさせたい文字列
     * @param utteranceId リクエスト用の一意のID値(null可)
     */
    private void _ttsSpeech(String str, @Nullable String utteranceId) {
        if(isTtsEnabled && tts != null) {
            if (tts.isSpeaking()) {
                tts.stop();
            }
            tts.setSpeechRate(1.2f);
            tts.setPitch(1.0f);
            if(utteranceId == null) {
                utteranceId = String.valueOf(CorrelationIdGenerator.generateId());
            }
            tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
            // ヘッドユニット側にもTTSで読み上げさせる
            _sendRequest(new Speak(TTSChunkFactory.createSimpleTTSChunks(str)));
        }
    }

    /*
     * Button
     */
    // SDL method
    @Override
    public void onOnButtonEvent(OnButtonEvent notification) {
        // ボタンを押下した際に呼び出されます。
        // note : OnButtonEventは、ボタンを1度押下した際に、BUTTONDOWNとBUTTONUPの2度呼び出されます。
        // 特に問題がなければOnButtonPressを使うようにしましょう。
        Log.i(LOG_TAG, "OnButtonEvent notification from SDL: " + notification);
        /*
        if (notification.getButtonEventMode().equals(ButtonEventMode.BUTTONDOWN)) {
        } else {    // ButtonEventMode.BUTTONUP
        }
        */
    }
    @Override
    public void onOnButtonPress(OnButtonPress notification) {
        Log.i(LOG_TAG, "OnButtonPress notification from SDL: " + notification);
        // ここにボタンが押下された際の処理を記述します。
    }


    // SDL method
    @Override
    public void onProxyClosed(String info, Exception e, SdlDisconnectedReason reason) {
        stopSelf();
        if(reason.equals(SdlDisconnectedReason.LANGUAGE_CHANGE) && BuildConfig.TRANSPORT.equals("MBT")){
            Intent intent = new Intent(TransportConstants.START_ROUTER_SERVICE_ACTION);
            //intent.putExtra(SdlReceiver.RECONNECT_LANG_CHANGE, true);
            sendBroadcast(intent);
        }
    }

    /*
     * SystemRequest
     */
    // SDL method
    @Override
    public void onOnSystemRequest(OnSystemRequest notification) {
        Log.i(LOG_TAG, "OnSystemRequest notification from SDL: " + notification);
        if(notification.getRequestType().equals(RequestType.LOCK_SCREEN_ICON_URL)){
            if(notification.getUrl() != null && lockScreenManager.getLockScreenIcon() == null){
                lockScreenManager.downloadLockScreenIcon(notification.getUrl(), new LockScreenDownloadedListener());
            }
        }
    }
    @Override
    public void onSystemRequestResponse(SystemRequestResponse response) {
        Log.i(LOG_TAG, "SystemRequest response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    private class LockScreenDownloadedListener implements LockScreenManager.OnLockScreenIconDownloadedListener{
        @Override
        public void onLockScreenIconDownloaded(Bitmap icon) {
            Log.i(LOG_TAG, "Lock screen icon downloaded successfully");
        }
        @Override
        public void onLockScreenIconDownloadError(Exception e) {
            Log.e(LOG_TAG, "Couldn't download lock screen icon, resorting to default.");
        }
    }

    // SDL method
    @Override
    public void onGetSystemCapabilityResponse(GetSystemCapabilityResponse response) {
        Log.i(LOG_TAG, "GetSystemCapabilityResponse from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onServiceEnded(OnServiceEnded serviceEnded) {
        Log.i(LOG_TAG, "onServiceEnded response from SDL: " + serviceEnded.getSessionType().getName() + " Info: ");
    }

    // SDL method
    @Override
    public void onError(String info, Exception e) {}

    /*
     * StreamRPC
     */
    // SDL method
    @Override
    public void onOnStreamRPC(OnStreamRPC notification) {
        Log.i(LOG_TAG, "OnStreamRPC notification from SDL: " + notification);
    }
    @Override
    public void onStreamRPCResponse(StreamRPCResponse response) {
        Log.i(LOG_TAG, "StreamRPC response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    /*
     * InteractionChoiceSet
     */
    // SDL method
    @Override
    public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse response) {
        Log.i(LOG_TAG, "CreateInteractionChoiceSet response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }
    @Override
    public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse response) {
        Log.i(LOG_TAG, "DeleteInteractionChoiceSet response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onAddCommandResponse(AddCommandResponse response) {
        Log.i(LOG_TAG, "AddCommand response from SDL: " + response.getResultCode().name());
    }
    @Override
    public void onDeleteCommandResponse(DeleteCommandResponse response) {
        Log.i(LOG_TAG, "DeleteCommand response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    /*
     * SubMenu
     */
    // SDL method
    @Override
    public void onAddSubMenuResponse(AddSubMenuResponse response) {
        Log.i(LOG_TAG, "AddSubMenu response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }
    @Override
    public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {
        Log.i(LOG_TAG, "DeleteSubMenu response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    /*
     * GlobalProperties
     */
    // SDL method
    @Override
    public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse response) {
        Log.i(LOG_TAG, "ResetGlobalProperties response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }
    @Override
    public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {
        Log.i(LOG_TAG, "SetGlobalProperties response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    @Override
    public void onButtonPressResponse(ButtonPressResponse response) {
        Log.i(LOG_TAG, "ButtonPress response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }
    @Override
    public void onSubscribeButtonResponse(SubscribeButtonResponse response) {
        Log.i(LOG_TAG, "SubscribeButton response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }
    @Override
    public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {
        Log.i(LOG_TAG, "UnsubscribeButton response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    /*
     * VehicleData
     */
    // SDL method
    @Override
    public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse response) {
        Log.i(LOG_TAG, "UnsubscribeVehicleData response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }
    @Override
    public void onGetVehicleDataResponse(GetVehicleDataResponse response) {
        Log.i(LOG_TAG, "GetVehicleData response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    /*
     * InteriorVehicleData
     */
    // SDL method
    @Override
    public void onSetInteriorVehicleDataResponse(SetInteriorVehicleDataResponse response) {
        Log.i(LOG_TAG, "SetInteriorVehicleData response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }
    @Override
    public void onGetInteriorVehicleDataResponse(GetInteriorVehicleDataResponse response) {
        Log.i(LOG_TAG, "GetInteriorVehicleDataResponse response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }
    @Override
    public void onOnInteriorVehicleData(OnInteriorVehicleData notification) {
        Log.i(LOG_TAG, "OnInteriorVehicleData from SDL: " + notification);
    }

    @Override
    public void onDeleteFileResponse(DeleteFileResponse response) {
        Log.i(LOG_TAG, "DeleteFile response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }
    @Override
    public void onListFilesResponse(ListFilesResponse response) {
        Log.i(LOG_TAG, "onListFilesResponse from SDL");
    }

    /*
     * AudioPassThru
     */
    // SDL method
    @Override
    public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response) {
        Log.i(LOG_TAG, "PerformAudioPassThru response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }
    @Override
    public void onEndAudioPassThruResponse(EndAudioPassThruResponse response) {
        Log.i(LOG_TAG, "EndAudioPassThru response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }
    @Override
    public void onOnAudioPassThru(OnAudioPassThru notification) {
        Log.i(LOG_TAG, "OnAudioPassThru notification from SDL: " + notification );
    }

    /*
     * WayPoints
     */
    // SDL method
    @Override
    public void onGetWayPointsResponse(GetWayPointsResponse response) {
        Log.i(LOG_TAG, "GetWayPoints response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }
    @Override
    public void onSubscribeWayPointsResponse(SubscribeWayPointsResponse response) {
        Log.i(LOG_TAG, "SubscribeWayPoints response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }
    @Override
    public void onUnsubscribeWayPointsResponse(UnsubscribeWayPointsResponse response) {
        Log.i(LOG_TAG, "UnsubscribeWayPoints response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }
    @Override
    public void onOnWayPointChange(OnWayPointChange notification) {
        Log.i(LOG_TAG, "OnWayPointChange notification from SDL: " + notification);
    }

    @Override
    public void onOnPermissionsChange(OnPermissionsChange notification) {
        Log.i(DEBUG_TAG, "Permision changed: " + notification);
        /*
        List<PermissionItem> permissions = notification.getPermissionItem();
        for(PermissionItem permission:permissions){
            if(permission.getRpcName().equalsIgnoreCase(FunctionID.SUBSCRIBE_VEHICLE_DATA.toString())){
                if(permission.getHMIPermissions().getAllowed()!=null && permission.getHMIPermissions().getAllowed().size()>0){
                    if(!isVehicleDataSubscribed){ //If we haven't already subscribed we will subscribe now
                        try {
                            proxy.subscribevehicledata(false,false,false,true, true, false,false, false, true, false, false, false, false, false, autoIncCorrId++);
                        } catch (SdlException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
        }
        */
    }

    // SDL method
    @Override
    public void onSetAppIconResponse(SetAppIconResponse response) {
        Log.i(LOG_TAG, "SetAppIcon response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onOnTouchEvent(OnTouchEvent notification) {
        Log.i(LOG_TAG, "OnTouchEvent notification from SDL: " + notification);
    }

    // SDL method
    @Override
    public void onOnLanguageChange(OnLanguageChange notification) {
        Log.i(LOG_TAG, "OnLanguageChange notification from SDL: " + notification);
    }

    // SDL method
    @Override
    public void onAlertManeuverResponse(AlertManeuverResponse response) {
        Log.i(LOG_TAG, "AlertManeuver response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onAlertResponse(AlertResponse response) {
        Log.i(LOG_TAG, "Alert response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onChangeRegistrationResponse(ChangeRegistrationResponse response) {
        Log.i(LOG_TAG, "ChangeRegistration response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onDiagnosticMessageResponse(DiagnosticMessageResponse response) {
        Log.i(LOG_TAG, "DiagnosticMessage response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onDialNumberResponse(DialNumberResponse response) {
        Log.i(LOG_TAG, "DialNumber response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onGenericResponse(GenericResponse response) {
        Log.i(LOG_TAG, "Generic response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onGetDTCsResponse(GetDTCsResponse response) {
        Log.i(LOG_TAG, "GetDTCs response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onOnDriverDistraction(OnDriverDistraction notification) {
        // Some RPCs (depending on region) cannot be sent when driver distraction is active.
        Log.i(LOG_TAG, "OnDriverDistraction from SDL");
    }

    // SDL method
    @Override
    public void onOnHashChange(OnHashChange notification) {
        Log.i(LOG_TAG, "OnHashChange notification from SDL: " + notification);
    }

    // SDL method
    @Override
    public void onOnKeyboardInput(OnKeyboardInput notification) {
        Log.i(LOG_TAG, "OnKeyboardInput notification from SDL: " + notification);
    }

    // SDL method
    @Override
    public void onPerformInteractionResponse(PerformInteractionResponse response) {
        Log.i(LOG_TAG, "PerformInteraction response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onReadDIDResponse(ReadDIDResponse response) {
        Log.i(LOG_TAG, "ReadDID response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onScrollableMessageResponse(ScrollableMessageResponse response) {
        Log.i(LOG_TAG, "ScrollableMessage response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onSendLocationResponse(SendLocationResponse response) {
        Log.i(LOG_TAG, "SendLocation response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onSendHapticDataResponse(SendHapticDataResponse response) {
        Log.i(LOG_TAG, "SendHapticDataResponse from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {
        Log.i(LOG_TAG, "SetMediaClockTimer response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onShowConstantTbtResponse(ShowConstantTbtResponse response) {
        Log.i(LOG_TAG, "ShowConstantTbt response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onSliderResponse(SliderResponse response) {
        Log.i(LOG_TAG, "Slider response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onSpeakResponse(SpeakResponse response) {
        Log.i(LOG_TAG, "SpeakCommand response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onOnTBTClientState(OnTBTClientState notification) {
        Log.i(LOG_TAG, "OnTBTClientState notification from SDL: " + notification);
    }

    // SDL method
    @Override
    public void onUpdateTurnListResponse(UpdateTurnListResponse response) {
        Log.i(LOG_TAG, "UpdateTurnList response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
    }

    // SDL method
    @Override
    public void onServiceNACKed(OnServiceNACKed serviceNACKed) {
        // Negative ACKnowledge
    }

    // SDL method
    @Override
    public void onServiceDataACK(int dataSize) {}
}
