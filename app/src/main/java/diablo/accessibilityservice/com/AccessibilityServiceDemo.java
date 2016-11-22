package diablo.accessibilityservice.com;

import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Diablo on 2016/11/21.
 */

public class AccessibilityServiceDemo extends AccessibilityService {

    private static final String TGA = "AccessibilityServiceDemo";
    private String curentDistrict = "";
    private String name = "";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "onServiceConnected");
//        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
//        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
//        serviceInfo.packageNames = new String[]{"com.tencent.mm"};
//        serviceInfo.notificationTimeout=100;
//        setServiceInfo(serviceInfo);
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
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:// 通知栏事件
                String content = "";
                content = event.getText().get(0) + "";
                name = content.substring(0, content.indexOf("请求添加你为朋友"));
                Log.i(TAG, "event type:通知栏事件:" + name);
                if (content.endsWith("请求添加你为朋友")) {
                    setScreenOn();
                    openAppByNotification(event);
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://窗体状态改变
                Log.i(TAG, "event type:窗体状态改变");
                if (atNewFrendUI(event)) {
                    findAndPerformActionButton(name);
                } else if (atNewFrendInfoUI(event)) {
                    getNewFrendInfo(getRootInActiveWindow());
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED://View获取到焦点
                Log.i(TAG, "event type:View获取到焦点");
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                Log.i(TAG, "event type:TYPE_VIEW_ACCESSIBILITY_FOCUSED");
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                Log.i(TAG, "event type:TYPE_GESTURE_DETECTION_END");
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.i(TAG, "event type:TYPE_WINDOW_CONTENT_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Log.i(TAG, "event type:TYPE_VIEW_CLICKED");
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                Log.i(TAG, "event type:TYPE_VIEW_TEXT_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                Log.i(TAG, "event type:TYPE_VIEW_SCROLLED");
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                Log.i(TAG, "event type:TYPE_VIEW_TEXT_SELECTION_CHANGED");
                break;
            default:
                Log.i(TAG, "no listen event");
        }
    }

    /**
     * 获取地区信息
     *
     * @param root
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void getNewFrendInfo(AccessibilityNodeInfo root) {
        boolean isDistrictPosition = false;
        if (root == null)//取得当前激活窗体的根节点
            return;
        AccessibilityNodeInfo current = null;
        for (int i = 0; i < root.getChildCount(); i++) {
            current = root.getChild(i);

            if (current == null) {
                continue;
            }

            if (current.getChildCount() > 0) {
                getNewFrendInfo(current);
            }

            if (current.getClassName().equals("android.widget.TextView")) {
                if (current.getText() == null) {
                    if (isDistrictPosition) {
                        curentDistrict = "没有地区信息";
                        break;
                    }
                    continue;
                }
                String district = current.getText().toString();
                if (isDistrictPosition) {
                    district = TextUtils.isEmpty(district) ? "没有地区信息" : district;
                    curentDistrict = district;
                    //// TODO: 获取到昵称和地区信息,发送到服务器
                    Log.i(TAG, "昵称:" + name + "\n地区:" + curentDistrict);
                    name = "";
                    curentDistrict = "";
                    break;
                }
                if (district.equals("地区")) {
                    isDistrictPosition = true;
                }
            }
        }
    }

    /**
     * 判断当前是否是在新朋友界面
     *
     * @param event
     * @return
     */
    private boolean atNewFrendUI(AccessibilityEvent event) {
        return event.getClassName().equals("com.tencent.mm.plugin.subapp.ui.friend.FMessageConversationUI");
    }

    /**
     * 判断当前是否是在新好友详细信息界面
     *
     * @param event
     * @return
     */
    private boolean atNewFrendInfoUI(AccessibilityEvent event) {
        return event.getClassName().equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI");
    }

    @Override
    public void onInterrupt() {

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

    /**
     * 点击新好友信息
     *
     * @param text
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void findAndPerformActionButton(String text) {
        if (getRootInActiveWindow() == null)//取得当前激活窗体的根节点
            return;
        //通过文字找到当前的节点
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
        for (int i = 0; i < nodes.size(); i++) {
            AccessibilityNodeInfo node = nodes.get(i);
            // 执行点击行为
            if (node.getClassName().equals("android.widget.TextView")) {
                node = getClickedNodes(node);
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    /**
     * 点击好友查看详细信息
     *
     * @param nodeInfo
     * @return
     */
    private AccessibilityNodeInfo getClickedNodes(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo.isClickable()) {
            return nodeInfo;
        } else {
            return getClickedNodes(nodeInfo.getParent());
        }
    }

    /**
     * 回到系统桌面
     */
    private void back2Home() {
        Intent home = new Intent(Intent.ACTION_MAIN);

        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);

        startActivity(home);
    }

    /**
     * 打开微信
     *
     * @param event 事件
     */
    private void openAppByNotification(AccessibilityEvent event) {
        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            try {
                PendingIntent pendingIntent = notification.contentIntent;
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }
}
