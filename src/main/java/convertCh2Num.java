import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * date 17/3/14 10:49
 *
 * @author: shuhan.lyn@alibaba-inc.com
 */
public class convertCh2Num {

    public static final List<String> allNumberList = new ArrayList<String>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "零", "一", "二", "三", "四", "五", "六", "七", "八", "九"));
    //英文转中文
    public static final ArrayList<String> nums = new ArrayList<String>(Arrays.asList("零", "一", "二", "三", "四", "五", "六", "七", "八", "九"));
    public static final ArrayList<String> b = new ArrayList<String>(Arrays.asList("", "十", "百", "千"));
    public static final  ArrayList<String> b4 = new ArrayList<String>(Arrays.asList("", "万", "亿"));


    public static final String[] arrNum = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "30", "40", "50", "60", "70", "80", "90"};
    public static final String[] arrCh = new String[] {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九",
            "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九",
            "二十", "三十", "四十", "五十", "六十", "七十", "八十", "九十"};


    public static HashMap<String, String> ch2num = new HashMap<String, String>();
    public static HashMap<String, String> num2ch = new HashMap<String, String>();



    public static String to_chinese_4(Integer num) {
        String cnnum = "";
        int len = num.toString().length();
        for (int i = 0; i < len ; i++) {
            int cb = num % 10;
            num = num / 10;
            if (cb == 0 && cnnum.length() > 0 && !String.valueOf(cnnum.charAt(0)).equals("零") ){
                cnnum = "零"  + cnnum;
            } else if (cb != 0) {
                cnnum = nums.get(cb) + b.get(i) + cnnum;
            }
        }

        return cnnum;
    }

    public static String to_chinese(int number, int len) {
        String cnnum = "";
        for (int i = 0; i < (len - 1)/ 4 + 1; i++) {
            cnnum = to_chinese_4(number % 10000) + b4.get(i) + cnnum;
            number /= 10000;
        }
        return cnnum;
    }

    public static String transNumberToChinese(String name) {
        if (StringUtils.isBlank(name)) {
            return name;
        }

        if (name.length() <= 1) {
            if (StringUtils.isNotBlank(num2ch.get(name))) {
                return num2ch.get(name);
            } else {
                return name;
            }
        }
        try {
            String temp = to_chinese(Integer.parseInt(name), name.length());
            name = temp;

        } catch (Exception e) {
        }

        return name;
    }

    public static String to_number(String name) {
        int result = 0;
        int temp = 1;
        int count = 0;
        for (int i = 0; i < name.length(); i++) {
            boolean b = true;
            char c = name.charAt(i);
            for (int j = 0; j < nums.size(); j++) {
                if (nums.get(j).equals(String.valueOf(c))) {
                    if (count != 0) {
                        result += temp;
                        temp = 1;
                        count = 0;
                    }
                    temp = j;
                    b = false;
                    break;
                }
            }

            if (b) {
                switch (c) {
                    case '十':
                        temp *= 10; break;
                    case '百':
                        temp *= 100; break;
                    case '千':
                        temp *= 1000; break;
                    case '万':
                        temp *= 10000; break;
                    case '亿':
                        temp *= 100000000; break;
                    default:
                        break;
                }
                count++;
            }

            if (i == name.length() - 1) {
                result += temp;
            }
        }

        return String.valueOf(result);
    }


    public static String transChineseToNumber(String name) {

        if (StringUtils.isBlank(name)) {
            return name;
        }

        if (name.length() <= 2) {
            if (StringUtils.isNotBlank(ch2num.get(name))) {
                return ch2num.get(name);
            } else {
                return name;
            }
        }

        try {
            String temp = to_number(name);
            name = temp;
        } catch (Exception e) {
        }

        return name;
    }


    public static void main(String [] args) {

        if (arrCh.length == arrNum.length) {
            for (int i = 0; i < arrNum.length; i++) {
                ch2num.put(arrCh[i], arrNum[i]);
                num2ch.put(arrNum[i], arrCh[i]);
            }
        }

        System.out.println(transChineseToNumber("一万零一十一"));
        System.out.println(transChineseToNumber("一百二十二"));
        System.out.println(transChineseToNumber("一千一百二十二"));
        System.out.println(transChineseToNumber("一千二百二十二"));

        System.out.println(transNumberToChinese("892"));








    }









}
