package diablo.accessibilityservice.com;

/**
 * Created by Diablo on 2016/11/23.
 */

public class FriendBean {
    public String friendName;
    public String friendDistrict;
    public String friendNO;

    public FriendBean(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendDistrict() {
        return friendDistrict;
    }

    public void setFriendDistrict(String friendDistrict) {
        this.friendDistrict = friendDistrict;
    }

    public String getFriendNO() {
        return friendNO;
    }

    public void setFriendNO(String friendNO) {
        this.friendNO = friendNO;
    }

    @Override
    public String toString() {
        return "昵称:" + friendName + "\n地区:" + friendDistrict + "\n微信号:" + friendNO;
    }
}
