package fans.time.com.android_test.area;


import android.content.Context;
import android.util.Xml;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fans.time.com.android_test.LogUtils;

/**
 * Manager area content by area code
 *
 * @author
 */
public class AreaManager {

    private static final String TAG = "AreaManager";

    public enum LANGUAGE {
        ZH, EN, TW
    }

    private static final int P_SIZE = 2;

    private static final String P_FILL = "0000";

    private static Map<String, String> QUERY_AREA_MAP;

    private static List<AreaModel> PROVINCE_LIST;

    private static Map<String, List<AreaModel>> AREA_MAP;

    private static List<AreaModel> PROVINCE_LIST_RANK;

    private static List<AreaModel> HOTCITIES_LIST;

    private static Map<String, AreaModel> CODE_MAP;

    private static LANGUAGE curLanguage = null;

    private static LANGUAGE curCityLanguage = null;

    private static LANGUAGE curLanguageOfQueryMap = null;

    /**
     * Get province list from memory
     *
     * @return
     */
    public static List<AreaModel> getProvinceList(Context context, LANGUAGE language) {
        if (language == null) {
            language = LANGUAGE.ZH;
        }
        if (curCityLanguage != language) {
            readAllArea(context, language);
            curCityLanguage = language;
        }
        return PROVINCE_LIST;
    }

    public static List<AreaModel> getProvinceListForRank(Context context, LANGUAGE language) {
        if (language == null) {
            language = LANGUAGE.ZH;
        }
        if (curLanguage != language) {
            readAllAreaForRank(context, language);
            curLanguage = language;
        }
        return PROVINCE_LIST_RANK;
    }

    public static List<AreaModel> getHotCitiesListForRank(Context context, LANGUAGE language) {
        if (language == null) {
            language = LANGUAGE.ZH;
        }
        if (curLanguage != language) {
            readAllAreaForRank(context, language);
            curLanguage = language;
        }
        return HOTCITIES_LIST;
    }

    /**
     * Get city list by province code from memory
     *
     * @param provinceCode
     * @return
     */
    public static List<AreaModel> getCityList(Context context, String provinceCode, LANGUAGE language) {
        if (language == null) {
            language = LANGUAGE.ZH;
        }
        if (curCityLanguage != language) {
            readAllArea(context, language);
            curCityLanguage = language;
        }
        return AREA_MAP == null ? null : AREA_MAP.get(provinceCode);
    }

    /**
     * If have not data in the memory, read all data from XML, and save to
     * memory.
     */
    private static void readAllArea(Context context, LANGUAGE language) {
        InputStream is = null;
        switch (language) {
            case ZH:
                //is = AreaManager.class.getResourceAsStream("area.xml");
                try {
                    is = context.getResources().getAssets().open("area.xml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case EN:
                //is = AreaManager.class.getResourceAsStream("area_en.xml");
                try {
                    is = context.getResources().getAssets().open("area_en.xml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case TW:
                //is = AreaManager.class.getResourceAsStream("area_zh_rTW.xml");
                try {
                    is = context.getResources().getAssets().open("area_zh_rTW.xml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        XmlPullParser parser = Xml.newPullParser();
        AreaModel pro = null;
        AreaModel city = null;
        List<AreaModel> CityArea = null;
        List<AreaModel> provinceList = null;
        Map<String, List<AreaModel>> areaMap = null;
        try {
            parser.setInput(is, "UTF-8");
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        provinceList = new ArrayList<AreaModel>();
                        areaMap = new HashMap<String, List<AreaModel>>();
                        break;
                    case XmlPullParser.START_TAG:
                        if ("province".equals(parser.getName())) {
                            CityArea = new ArrayList<AreaModel>();
                            String id = parser.getAttributeValue(0);
                            String content = parser.getAttributeValue(1);
                            pro = new AreaModel();
                            pro.setCode(id);
                            pro.setText(content);
                            provinceList.add(pro);
                        } else if ("city".equals(parser.getName())) {
                            String id = parser.getAttributeValue(0);
                            city = new AreaModel();
                            city.setCode(id);
                            city.setText(parser.getAttributeValue(1));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("city".equals(parser.getName())) {
                            if (city != null) {
                                CityArea.add(city);
                                city = null;
                            }
                        } else if ("province".equals(parser.getName())) {
                            if (pro != null) {
                                areaMap.put(pro.getCode(), CityArea);
                                pro = null;
                            }
                        }
                        break;
                    default:
                        break;
                }
                event = parser.next();
            }
        } catch (IOException e) {
            LogUtils.e(TAG, "Read areaCode fail!", e);
        } catch (XmlPullParserException e) {
            LogUtils.e(TAG, "Parser areaCode fail!", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LogUtils.e(TAG, "Close IO fail!", e);
                }
                is = null;
            }
        }
        PROVINCE_LIST = provinceList;
        AREA_MAP = areaMap;
    }

    /**
     * If have not data in the memory, read all data from XML, and save to
     * memory.
     */
    private static void readAllAreaForRank(Context context, LANGUAGE language) {
        InputStream is = null;
        switch (language) {
            case ZH:
                //is = AreaManager.class.getResourceAsStream("arearank.xml");
                try {
                    is = context.getResources().getAssets().open("arearank.xml");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
        XmlPullParser parser = Xml.newPullParser();
        AreaModel pro = null;
        AreaModel city = null;
        List<AreaModel> cityList = null;
        List<AreaModel> provinceList = null;
        Map<String, AreaModel> areaMap = null;
        try {
            parser.setInput(is, "UTF-8");
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        provinceList = new ArrayList<AreaModel>();
                        cityList = new ArrayList<AreaModel>();
                        areaMap = new HashMap<String, AreaModel>();
                        break;
                    case XmlPullParser.START_TAG:
                        if ("province".equals(parser.getName())) {
                            String id = parser.getAttributeValue(0);
                            String content = parser.getAttributeValue(1);
                            pro = new AreaModel();
                            pro.setCode(id);
                            pro.setText(content);
                        } else if ("city".equals(parser.getName())) {
                            String id = parser.getAttributeValue(0);
                            city = new AreaModel();
                            city.setCode(id);
                            city.setText(parser.getAttributeValue(1));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("city".equals(parser.getName())) {
                            if (city != null) {
                                cityList.add(city);
                                areaMap.put(city.getCode(), city);
                                city = null;
                            }
                        } else if ("province".equals(parser.getName())) {
                            if (pro != null) {
                                provinceList.add(pro);
                                areaMap.put(pro.getCode(), pro);
                                pro = null;
                            }
                        }
                        break;
                    default:
                        break;
                }
                event = parser.next();
            }
        } catch (IOException e) {
            LogUtils.e(TAG, "Read areaCode fail!", e);
        } catch (XmlPullParserException e) {
            LogUtils.e(TAG, "Parser areaCode fail!", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LogUtils.e(TAG, "Close IO fail!", e);
                }
                is = null;
            }
        }
        PROVINCE_LIST_RANK = provinceList;
        HOTCITIES_LIST = cityList;
        CODE_MAP = areaMap;
    }

    public static String getAreaName(Context context, LANGUAGE language, String code) {
        if (language == null) {
            language = LANGUAGE.ZH;
        }
        if (curLanguage != language) {
            readAllAreaForRank(context, language);
            curLanguage = language;
        }
        if (CODE_MAP != null && CODE_MAP.get(code) != null) {
            return CODE_MAP.get(code).getText();
        }
        return null;
    }

    /**
     * Read area content by area code.
     *
     * @param areaCode
     * @param language
     * @return
     */
    public static String readArea(Context context, String areaCode, LANGUAGE language) {
        if (areaCode == null || areaCode.length() <= P_SIZE) {
            return null;
        }
        /**
         * First, read data from memory
         */
        String areaContent = null;
        if (language == null) {
            language = LANGUAGE.ZH;
        }
        if (QUERY_AREA_MAP != null && !QUERY_AREA_MAP.isEmpty()
                && language == curLanguageOfQueryMap) {
            areaContent = QUERY_AREA_MAP.get(areaCode);
            if (areaContent != null) {
                return areaContent;
            }
        } else {
            curLanguageOfQueryMap = language;
            QUERY_AREA_MAP = new HashMap<String, String>();
        }
        /**
         * If have not this data in the memory. Read and Parser XML, save to
         * memory
         */
        String provinceCode = areaCode.substring(0, P_SIZE) + P_FILL;
        InputStream is = null;
        switch (language) {
            case ZH:
                //is = AreaManager.class.getResourceAsStream("area.xml");
                try {
                    is = context.getResources().getAssets().open("area.xml");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case EN:
                //is = AreaManager.class.getResourceAsStream("area_en.xml");

                try {
                    is = context.getResources().getAssets().open("area_en.xml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case TW:
                //is = AreaManager.class.getResourceAsStream("area_zh_rTW.xml");
                try {
                    is = context.getResources().getAssets().open("area_zh_rTW.xml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

        AreaModel province = null;
        AreaModel city = null;
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(is, "UTF-8");
            int event = parser.getEventType();
            Boolean isQueryFinish = false;
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("province".equals(parser.getName())) {
                            String id = parser.getAttributeValue(0);
                            if (provinceCode.equals(id)) {
                                province = new AreaModel();
                                province.setCode(id);
                                province.setText(parser.getAttributeValue(1));
                            }
                        } else if ("city".equals(parser.getName())) {
                            String id = parser.getAttributeValue(0);
                            if (areaCode.equals(id)) {
                                city = new AreaModel();
                                city.setCode(areaCode);
                                city.setText(parser.getAttributeValue(1));
                                isQueryFinish = true;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                if (isQueryFinish)
                    break;
                else
                    event = parser.next();
            }
        } catch (IOException e) {
            LogUtils.e(TAG, "Parser areaCode fail!", e);
        } catch (XmlPullParserException e) {
            LogUtils.e(TAG, "Read areaCode fail!", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LogUtils.e(TAG, "Close IO fail!", e);
                }
                is = null;
            }
        }
        areaContent = getCurrentArea(province, city);
        QUERY_AREA_MAP.put(areaCode, areaContent);
        return areaContent;
    }

    /**
     * Read area data from memory
     *
     * @return
     */
    private static String getCurrentArea(AreaModel province, AreaModel city) {
        if (province == null || city == null) {
            return null;
        }
        return new StringBuilder().append(province.getText()).append(" ").append(city.getText())
                .toString();
    }
}
