package ando.player.setting;

public class MediaConstants {

    public static final int PLAYSPEED_075 = 0x00030004;
    public static final int PLAYSPEED_10 = 0x00010001;
    public static final int PLAYSPEED_12 = 0x00060005;
    public static final int PLAYSPEED_15 = 0x00030002;
    public static final int PLAYSPEED_20 = 0x00020001;

    public static final int QUALITY_NORMAL = 10;
    public static final int QUALITY_HIGH = 20;

    public static final String TIMING_TYPE = "key_timing_type";
    public static final String KEY_LAST_TIMING = "key_last_timing"; // 默认定时
    public static final String KEY_LAST_PLAYSPEED = "key_last_playspeed";
    //
    public static final long mTimingM30 = 18000;// 真值  30 * 60 * 1000
    public static final long mTimingM60 = 50000;// 真值  60 * 60 * 1000

    public static final String ERROR_TEXT_NET = "当前网络不可用，请检查网络设置";


}