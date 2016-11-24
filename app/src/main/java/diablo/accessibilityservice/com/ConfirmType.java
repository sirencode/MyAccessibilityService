package diablo.accessibilityservice.com;

/**
 * Created by Diablo on 2016/11/23.
 */

public enum ConfirmType {
    INBLACKLIST(1), NOTSURE(2), NOTINBLACKLIST(3);
    private int code;
    private ConfirmType(int code){
        this.code = code;
    }

    public int getValue(){
        return code;
    }
}
