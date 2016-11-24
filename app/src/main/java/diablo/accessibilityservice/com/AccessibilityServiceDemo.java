package diablo.accessibilityservice.com;

import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import static android.R.attr.name;
import static android.content.ContentValues.TAG;

/**
 * Created by Diablo on 2016/11/21.
 */

public class AccessibilityServiceDemo extends AccessibilityService {

    private static final String TGA = "AccessibilityServiceDemo";
    private int currentType = 0;
    private static final int NORMAL = 0;
    private static final int DELETE = 1;
    private static final int ALERT = 2;
    private AccessibilityNodeInfoUtil util;


    public FriendBean getFriendBean() {
        return friendBean;
    }

    public void setFriendBean(FriendBean friendBean) {
        this.friendBean = friendBean;
    }

    public boolean isCanGetInfo() {
        return canGetInfo;
    }

    public void setCanGetInfo(boolean canGetInfo) {
        this.canGetInfo = canGetInfo;
    }

    private FriendBean friendBean;
    private boolean canGetInfo = false;

    public int getCurrentType() {
        return currentType;
    }

    public void setCurrentType(int currentType) {
        this.currentType = currentType;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        util = new AccessibilityNodeInfoUtil(this);
        Log.i(TAG, "onServiceConnected");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(TAG, "-------------------------------------------------------------");
        int eventType = event.getEventType(); //事件类型
        Log.i(TAG, "PackageName:" + event.getPackageName() + ""); // 响应事件的包名
        Log.i(TAG, "Source Class:" + event.getClassName() + ""); // 事件源的类名
        Log.i(TAG, "Description:" + event.getContentDescription() + ""); // 事件源描述
        Log.i(TAG, "Event Type(int):" + eventType + "");

        if (util == null){
            return;
        }

        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:// 通知栏事件
                String content = "";
                if (!event.getText().isEmpty() && !TextUtils.isEmpty(event.getText().get(0))) {
                    content = event.getText().get(0) + "";
                    String name = content.substring(0, content.indexOf(AccessibilityNodeInfoUtil.NOTIFICATION_FILTRATE_TEXT));
                    friendBean = new FriendBean(name);
                }
                Log.i(TAG, "event type:通知栏事件:" + name);
                if (content.endsWith(AccessibilityNodeInfoUtil.NOTIFICATION_FILTRATE_TEXT)) {
                    setScreenOn();
                    util.openAppByNotification(event);
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://窗体状态改变
                Log.i(TAG, "event type:窗体状态改变");
                if (getFriendBean() == null) {
                    return;
                }
                if (TextUtils.isEmpty(getFriendBean().getFriendName())) {
                    return;
                }
                if (util.atNewFrendUI(event)) {
                    util.clickItemByText(getRootInActiveWindow(),getFriendBean().getFriendName());
                } else if (util.atNewFrendInfoUI(event)) {
                    if (!isCanGetInfo()) {
                        util.clickButtonByText(getRootInActiveWindow(),AccessibilityNodeInfoUtil.ACCEPT_TEXT);
                        util.clickButtonByText(getRootInActiveWindow(),AccessibilityNodeInfoUtil.SEND_MESSAGE_TEXT);
                    }else if (getCurrentType() == ALERT){
                        util.back2Home();
                    } else {
                        util.getNewFrendInfo(getRootInActiveWindow());
                    }
                } else if (util.atChattingUI(event)) {
                    util.cliclkNewFrendIcon(getRootInActiveWindow());
                } else if (util.atMoreUI(event)) {
                    util.clickItemByText(getRootInActiveWindow(),AccessibilityNodeInfoUtil.DELETE_TEXT);
                } else if (util.atDeletePopWindowUI(event)) {
                    util.clickButtonByText(getRootInActiveWindow(),AccessibilityNodeInfoUtil.DELETE_TEXT);
                    util.back2Home();
                } else if (util.atBZUI(event)) {
                    util.setBZInfo(getRootInActiveWindow());
                }
                break;
            default:
                Log.i(TAG, "no listen event");
        }
    }

    /**
     * 如果锁屏，唤醒屏幕并解锁
     */
    private void setScreenOn() {
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
        if (!isScreenOn) {
            wakeUpAndUnlock(this);
        }
    }

    private void wakeUpAndUnlock(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }

    public void release() {
        friendBean = null;
        setCanGetInfo(false);
        setCurrentType(NORMAL);
    }

    @Override
    public void onInterrupt() {

    }

}
