package fans.time.com.android_test.area;


/**
 * @author
 */
public class AreaModel {

    private String mCode;

    private String mText;

    private String mChinese;

    private String mEnglish;

    private String mChineseTw;

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        this.mCode = code;
    }

    public String getChinese() {
        return mChinese;
    }

    public void setChinese(String chinese) {
        this.mChinese = chinese;
    }

    public String getEnglish() {
        return mEnglish;
    }

    public void setmEnglish(String english) {
        this.mEnglish = english;
    }

    public String getChineseTw() {
        return mChineseTw;
    }

    public void setChineseTw(String chineseTw) {
        this.mChineseTw = chineseTw;
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("mCode: ");
        sb.append(mCode);
        sb.append(" mText: ");
        sb.append(mText);
        sb.append(" mChinese: ");
        sb.append(mChinese);
        sb.append(" mEnglish: ");
        sb.append(mEnglish);
        sb.append(" mChineseTw: ");
        sb.append(mChineseTw);
        return sb.toString();
    }
}
