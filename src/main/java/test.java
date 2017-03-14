import com.taobao.igraph.client.core.IGraphClient;
import com.taobao.igraph.client.core.IGraphClientBuilder;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * date 17/2/17 12:31
 *
 * @author: shuhan.lyn@alibaba-inc.com
 */
public class test {


//    private static final Pattern ost_patt = Pattern.compile("\\s*(ost|o\\.s\\.t)\\s*(vol|part)*\\.*[0-9]*$");
    static int chineseMin = 19968;
    static int chineseMax = 171941;

    static ArrayList<String> nums = new ArrayList<String>(Arrays.asList("零", "一", "二", "三", "四", "五", "六", "七", "八", "九"));
    static ArrayList<String> b = new ArrayList<String>(Arrays.asList("", "十", "百", "千"));
    static ArrayList<String> b4 = new ArrayList<String>(Arrays.asList("", "万", "亿"));

    private static final Pattern ost_patt = Pattern.compile("\\s+(ost|o\\.s\\.t)\\s*(vol|part)*\\.*[0-9]*$");
    public static final Pattern brackets_patt = Pattern.compile("[\\[<{（(][\\s\\S]*?[)）}>\\]]", Pattern.CASE_INSENSITIVE);

   public static List<String> ost_keys = new ArrayList<String>();

    public static IGraphClient init (String searchDomain, String updateDomain) {


        IGraphClientBuilder builder = IGraphClientBuilder.create();

        builder.setSocketTimeout(3000);
        builder.setConnectTimeout(3000);
        builder.setConnectionRequestTimeout(3000);
        builder.setSearchCluster("DEFAULT");
        builder.setUpdateCluster("DEFAULT");
        builder.setMaxConnTotal(1024);
        builder.setMaxConnPerRoute(200);


        return builder.build("alimusic_trident", searchDomain, updateDomain);


    }


    static boolean isChinese(String word) {
        if (StringUtils.isBlank(word)) {
            return false;
        }
        char [] chars = word.toCharArray();
        for (char c : chars) {
            if (c >= chineseMin && c <= chineseMax) {
                return true;
            }
        }
        return false;
    }


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
    public static void to_chinese(int number, int len) {

        String cnnum = "";
        for (int i = 0; i < (len - 1)/ 4 + 1; i++) {
            cnnum = to_chinese_4(number % 10000) + b4.get(i) + cnnum;
            number /= 10000;
        }

        System.out.println(cnnum);


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
    public static void main(String[] args) {



        to_chinese(10, 2);
        System.out.println(to_number("二十"));


//       List<Term> terms = DicAnalysis.parse("一千零一喜欢").getTerms();
//
//        System.out.println(terms);
//
//        for (Term t : terms) {
//            if (t.getNatureStr().equals("m")) {
//                if (StringUtils.isNumeric(t.getRealName())) {
//                    System.out.println("the number " + t.getRealName() + " is number");
//                } else {
//                    System.out.println("the number " + t.getRealName() + " is not number");
//                }
//            }
//        }

//        IGraphClient client = init("ups.daily.proxy.taobao.org", "ups.daily.swift.proxy.taobao.org");
//        AtomicQuery atomicQuery = new AtomicQuery("r_xiami_trident_hotwords_ups", new KeyList("VigxBMbr/GQDAErDKg61OS1G"));
//
//        atomicQuery.setOrderby("-search_cnt").setRange(0, 5);
//
//        QueryResult queryResult = null;
//
//        try {
//            queryResult = client.search(atomicQuery);
//            SingleQueryResult singleQueryResult = queryResult.getSingleQueryResult();
//
//            if (singleQueryResult.hasError()) {
//
//            }
//
//
//            List<MatchRecord> matchRecords = singleQueryResult.getMatchRecords();
//
//            for (MatchRecord record : matchRecords) {
//                String query =  record.getString("query");
//                Long search_cnt = record.getLong("search_cnt");
//                Long click_cnt = record.getLong("click_cnt");
//                System.out.println(query + ":" + search_cnt + ":" + click_cnt);
//
//
//            }
//            List<SingleQueryResult> allQueryResult = queryResult.getAllQueryResult();
//
//            for (SingleQueryResult singleQueryResult : allQueryResult) {
//                List<MatchRecord> matchRecords = singleQueryResult.getMatchRecords();
//                for (MatchRecord matchRecord : matchRecords) {
//
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//

//
//        String testStr = "o.s.t 韩国电视剧原声带";
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < testStr.length(); i++) {
//           char c = testStr.charAt(i);
//            // 去掉特殊字符
//            if (Character.isDigit(c) || Character.isLetter(c) || c == '.' || c==':' ||c=='|'||c=='_'||c=='('||c==')' || c=='*' || c == '\'' || c=='/') {
//                sb.append(c);
//            } else {
//                sb.append(" ");
//            }
//        }
//        testStr = sb.toString();
//       System.out.println(testStr.replaceAll("[\\s]+"," ").trim());

//        float [] arr = new float[10];
//        for (int i = 0; i < 10; i++) {
//            System.out.println(arr[i]);
//        }



//        List<Integer> froms = new ArrayList<Integer>();
//
//        for (int i = 0 ; i < 10; i++) {
//            froms.add(i * 2);
//        }
//
//
//        List<Integer> tos = new ArrayList<Integer>();
//
//        for (int i = 0; i < 10; i++) {
//            tos.add(i * 5);
//        }
//
//        long b = System.currentTimeMillis();
//
//        List<Integer> mutual_users = new ArrayList<Integer>();
//        List<Integer> only_to_user = new ArrayList<Integer>();
//        List<Integer> only_from_user = new ArrayList<Integer>();
//
//
//        only_to_user.addAll(tos);
//        only_to_user.removeAll(froms);
//        mutual_users.addAll(tos);
//        mutual_users.retainAll(froms);
//
//        only_from_user.addAll(froms);
//        only_from_user.removeAll(tos);
//        System.out.println("cost " + (System.currentTimeMillis() - b));
//        List<Integer> inters = (List<Integer>)ListUtils.intersection(froms, tos);
//        List<Integer> only_to = (List<Integer>) ListUtils.removeAll(tos, froms);
//        List<Integer> only_from = (List<Integer>) ListUtils.removeAll(froms, tos);

//        Sets.intersection();

//
//        System.out.println("cost " + (System.currentTimeMillis() - b));
//        System.out.println(froms );
//        System.out.println(tos);
//
//
//        System.out.println(inters);
//        System.out.println(only_to);
//        System.out.println(only_from);


//        Matcher m = ost_patt.matcher("frost vol");
//        if (m.find()) {
//           System.out.println(m.replaceAll(""));
//        }














    }



    public static String filter_ost(String originalName) {

        if (StringUtils.isBlank(originalName)) {
            return null;
        }

        String name = originalName.toLowerCase();

        Matcher matcher= brackets_patt.matcher(name);

        if (matcher.find()) {
            String result = matcher.replaceAll("");
            if (StringUtils.isNotBlank(result)) {
                return result;
            }
        }

        Matcher ost_matcher = ost_patt.matcher(name);

        if (ost_matcher.find()) {
            String result = ost_matcher.replaceAll("");
            if (StringUtils.isNotBlank(result)) {
                return result;
            }
        }


        String filter_key = "";
        int max_len = 0;
        for (String key : ost_keys) {
            if (name.contains(key) && key.length() > max_len) {
                filter_key = key;
                max_len = key.length();
            }
        }

        String result = name.replace(filter_key, "");
        if (StringUtils.isNotBlank(result)) {
            return result;
        }

        return name;


    }
}
