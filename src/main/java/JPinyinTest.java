import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.IOException;

/**
 * date 17/3/15 15:14
 *
 * @author: shuhan.lyn@alibaba-inc.com
 */
public class JPinyinTest {
    private static HanyuPinyinOutputFormat format = null;

    static {
        format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }


    private static String getCharacterPinYin(char c) {
        String[] pinyin = null;
        try {
            pinyin = net.sourceforge.pinyin4j.PinyinHelper.toHanyuPinyinStringArray(c, format);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        // 如果c不是汉字，toHanyuPinyinStringArray会返回null
        if (pinyin == null) return null;
        // 只取一个发音，如果是多音字，仅取第一个发音
        return pinyin[0];

    }


    public static String getStringPinYin(String str) {
        if(str == null){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            String tempPinyin = getCharacterPinYin(str.charAt(i));
            if (tempPinyin == null) {
                sb.append(str.charAt(i));
            } else {
                sb.append(" ").append(tempPinyin);
            }
        }
        return sb.toString();
    }


    public static void main(String[] args) throws Exception{
        long b = System.currentTimeMillis();
        String pinyin = "";

        for (int i = 0; i < 10000; i++) {

                String str = "曾志伟";
                pinyin = PinyinHelper.convertToPinyinString(str, " ", PinyinFormat.WITHOUT_TONE);



        }


        System.out.println("jpinyin " + pinyin + " cost " + (System.currentTimeMillis() - b) );

        b = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            String str = "曾志伟";
            pinyin = getStringPinYin(str);
        }

        System.out.println("pinyin4j " + pinyin + " cost " + (System.currentTimeMillis() - b) );

    }

}
