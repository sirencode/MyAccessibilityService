package diablo.accessibilityservice.com;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;

/**
 * Created by Diablo on 2016/11/24.
 */

public class AccessibilityNodeInfoUtil {

    private AccessibilityServiceDemo service;
    private static final int DELETE = 1;
    private static final int ALERT = 2;

    /**
     * 同意加好友按钮文字
     */
    public static final String NOTIFICATION_FILTRATE_TEXT = "请求添加你为朋友";

    /**
     * 同意加好友按钮文字
     */
    public static final String ACCEPT_TEXT = "通过验证";

    /**
     * 发送消息按钮文字
     */
    public static final String SEND_MESSAGE_TEXT = "发消息";

    /**
     * 删除按钮文字
     */
    public static final String DELETE_TEXT = "删除";

    /**
     * 修改备注按钮文字
     */
    public static final String CHANGE_NAME_TEXT = "设置备注和标签";

    /**
     * 修改备注按钮后完成按钮文字
     */
    public static final String DONE_CHANGE_NAME_TEXT = "完成";

    /**
     * 不确定文字提示
     */
    public static final String ALERT_NAME_TEXT = "-未确定";

    /**
     * 地区按钮文字提示
     */
    public static final String DISTRICT_TEXT = "地区";


    public AccessibilityNodeInfoUtil(AccessibilityServiceDemo service) {
        this.service = service;
    }

    /**
     * 打开微信
     */
    public void openAppByNotification(AccessibilityEvent event) {
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

    /**
     * 判断当前是否是在新朋友界面
     */
    public boolean atNewFrendUI(AccessibilityEvent event) {
        return event.getClassName().equals("com.tencent.mm.plugin.subapp.ui.friend.FMessageConversationUI");
    }

    /**
     * 判断当前是否是在新好友详细信息界面
     */
    public boolean atNewFrendInfoUI(AccessibilityEvent event) {
        return event.getClassName().equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI");
    }

    /**
     * 判断当前是否是在新好友详细信息界面
     */
    public boolean atChattingUI(AccessibilityEvent event) {
        if (!TextUtils.isEmpty(event.getContentDescription()) && event.getContentDescription().toString()
                .contains(service.getFriendBean().getFriendName())) {
            return event.getClassName().equals("com.tencent.mm.ui.chatting.ChattingUI");
        }
        return false;
    }

    /**
     * 判断当前是否是在新好友更多信息界面
     */
    public boolean atMoreUI(AccessibilityEvent event) {
        return event.getClassName().equals("android.support.design.widget.c");
    }

    /**
     * 判断当前是否是在新好友更多信息界面
     */
    public boolean atBZUI(AccessibilityEvent event) {
        return event.getClassName().equals("com.tencent.mm.ui.contact.ContactRemarkInfoModUI");
    }

    /**
     * 判断当前是否是在弹出删除信息界面
     */
    public boolean atDeletePopWindowUI(AccessibilityEvent event) {
        return event.getClassName().equals("com.tencent.mm.ui.base.h");
    }

    /**
     * 找到包含文字textview并找到第一个可点击的父控件点击
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void clickItemByText(AccessibilityNodeInfo root, String text) {
        if (root == null)//取得当前激活窗体的根节点
            return;
        //通过文字找到当前的节点
        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText(text);
        for (int i = 0; i < nodes.size(); i++) {
            AccessibilityNodeInfo node = nodes.get(i);
            // 执行点击行为
            if (node.getClassName().equals("android.widget.TextView")) {
                node = getClickedNodes(node);
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
        }
    }

    /**
     * 通过按钮文字找到按钮并模拟点击
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void clickButtonByText(AccessibilityNodeInfo root, String text) {
        if (root == null)//取得当前激活窗体的根节点
            return;
        //通过文字找到当前的节点
        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText(text);
        for (int i = 0; i < nodes.size(); i++) {
            AccessibilityNodeInfo node = nodes.get(i);
            // 执行点击行为
            if (node.getClassName().equals("android.widget.Button") && node.isClickable()) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    /**
     * 模拟点击聊天页面好友头像
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void cliclkNewFrendIcon(AccessibilityNodeInfo root) {
        if (root == null)//取得当前激活窗体的根节点
            return;
        AccessibilityNodeInfo current = null;
        for (int i = 0; i < root.getChildCount(); i++) {
            current = root.getChild(i);
            if (current == null) {
                continue;
            }
            if (current.getChildCount() > 0) {
                cliclkNewFrendIcon(current);
            }
            if (current.getClassName().equals("android.widget.ImageView") && !TextUtils.isEmpty(
                    current.getContentDescription())) {
                current.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                service.setCanGetInfo(true);
                break;
            }
        }
    }

    public AccessibilityNodeInfo getClickedNodes(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo.isClickable()) {
            return nodeInfo;
        } else {
            return getClickedNodes(nodeInfo.getParent());
        }
    }

    /**
     * 模拟点击好友信息页面更多操作按钮
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void cliclkMore(AccessibilityNodeInfo root) {
        if (root == null)//取得当前激活窗体的根节点
            return;
        AccessibilityNodeInfo current = null;
        for (int i = 0; i < root.getChildCount(); i++) {
            current = root.getChild(i);
            if (current == null) {
                continue;
            }
            if (current.getChildCount() > 0) {
                cliclkMore(current);
            }
            if (current.getClassName().equals("android.widget.TextView") && current.isClickable()) {
                current.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setBZInfo(AccessibilityNodeInfo root) {
        if (root == null) {
            return;
        }
        AccessibilityNodeInfo current = null;
        for (int i = 0; i < root.getChildCount(); i++) {
            current = root.getChild(i);
            if (current == null) {
                continue;
            }
            if (current.getChildCount() > 0) {
                setBZInfo(current);
            }

            if (TextUtils.isEmpty(current.getText())) {
                continue;
            }

            if (current.getClassName().equals("android.widget.TextView") && current.getText().toString()
                    .contains(service.getFriendBean().getFriendName())) {
                current.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                setBZInfo(service.getRootInActiveWindow());
                break;
            }

            if (current.getClassName().equals("android.widget.EditText") && current.getText().toString()
                    .contains(service.getFriendBean().getFriendName())) {
                Bundle arguments = new Bundle();
                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                        AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
                arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
                        true);
                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, service
                        .getFriendBean().getFriendName().getBytes().length);
                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, service
                        .getFriendBean().getFriendName().getBytes().length);
                current.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, arguments);
                current.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
                        arguments);
                current.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                ClipData clip = ClipData.newPlainText("label", service.getFriendBean().getFriendName() + ALERT_NAME_TEXT);
                ClipboardManager clipboardManager = (ClipboardManager) service.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(clip);
                current.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                clickTextViewByText(service.getRootInActiveWindow(), DONE_CHANGE_NAME_TEXT);
                break;
            }
        }
    }

    /**
     * 根据文本信息查找到textview并模拟点击
     *
     * @param text
     */
    public void clickTextViewByText(AccessibilityNodeInfo root, String text) {
        if (root == null) {
            return;
        }
        //通过文字找到当前的节点
        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText(text);
        for (int i = 0; i < nodes.size(); i++) {
            AccessibilityNodeInfo node = nodes.get(i);
            // 执行点击行为
            if (node.getClassName().equals("android.widget.TextView") && node.isClickable()) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    /**
     * 获取新好友昵称，地区，和微信号信息
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void getNewFrendInfo(AccessibilityNodeInfo root) {
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
                if (TextUtils.isEmpty(current.getText())) {
                    if (isDistrictPosition) {
                        service.getFriendBean().setFriendDistrict("");
                        break;
                    }
                    continue;
                }
                String currentTxt = current.getText().toString();
                if (isDistrictPosition) {
                    currentTxt = TextUtils.isEmpty(currentTxt) ? "" : currentTxt;
                    service.getFriendBean().setFriendDistrict(currentTxt);
                    // TODO: 获取到昵称,地区和微信号信息,发送到服务器
                    Toast.makeText(service, service.getFriendBean().toString(), Toast.LENGTH_LONG).show();
                    int result = getResult(service.getFriendBean());
                    if (result == ConfirmType.NOTINBLACKLIST.getValue()) {
                        back2Home();
                    } else if (result == ConfirmType.INBLACKLIST.getValue()) {
                        service.setCurrentType(DELETE);
                        cliclkMore(service.getRootInActiveWindow());
                    } else if (result == ConfirmType.NOTSURE.getValue()) {
                        service.setCurrentType(ALERT);
                        clickItemByText(service.getRootInActiveWindow(), CHANGE_NAME_TEXT);
                    }
                    break;
                }
                if (currentTxt.equals("DISTRICT_TEXT")) {
                    isDistrictPosition = true;
                }
                if (currentTxt.contains("微信号")) {
                    service.getFriendBean().setFriendNO(currentTxt.replace("微信号:", ""));
                }
            }
        }
    }


    public int getResult(FriendBean friendBean) {
        return ConfirmType.NOTINBLACKLIST.getValue();
    }

    /**
     * 回到系统桌面
     */
    public void back2Home() {
        service.release();
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);
        service.startActivity(home);
    }

    /**
     * 模拟back按键
     */
    public void pressBackButton() {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
