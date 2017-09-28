import java.util.ArrayList;
import java.util.Map;

public class Util {

    /**
     * 组合选择
     * @param dataList 待选列表
     * @param dataIndex 待选开始索引
     * @param resultList 前面（resultIndex-1）个的组合结果
     * @param resultIndex 选择索引，从0开始
     */
    public static ArrayList<int[]> combinationSelect(ArrayList<int[]> lines, ArrayList<Integer> dataList,
                                                     int dataIndex, int[] resultList, int resultIndex) {
        int resultLen = resultList.length;
        int resultCount = resultIndex + 1;
        if (resultCount > resultLen) { // 全部选择完时，输出组合结果
//            System.out.println(Arrays.toString(resultList));
            lines.add(resultList.clone());
            return lines;
        }

        // 递归选择下一个
        for (int i = dataIndex; i < dataList.size() + resultCount - resultLen; i++) {
            resultList[resultIndex] = dataList.get(i);
            combinationSelect(lines, dataList, i + 1, resultList, resultIndex + 1);
        }
        return lines;
    }

    /**
     * 把字符串转换成整形数组
     * @param str 要转换的整形数组
     * @return balls 转换后的整形数组
     */
    public static ArrayList<Integer> StringToIntArray (String str) {
        String items[] = str.split(",");
        ArrayList<Integer> balls = new ArrayList<Integer>();
        for (int i = 0; i < items.length; i++) {
            balls.add(Integer.parseInt(items[i]));
        }
        return balls;
    }

    public static ArrayList<Integer> ArrayToArrayList (int[] arr) {
        ArrayList<Integer> balls = new ArrayList<Integer>();
        for (int i = 0; i < arr.length; i++) {
            balls.add(arr[i]);
        }
        return balls;
    }

    /**
     * 判断betcode对象是否已经在arraylist中，如果已经存在，修改倍数；
     * 如果不在则插入arraylist
     * @param chaiDanBetCode 存放betcode的arraylist
     * @param betCode 将要插入的betcode对象
     */
    public static void addWithMultiple (Map<Long, Integer> chaiDanBetCode, DLTBetCode betCode) {

        //红球蓝球都相等，增加倍数；不相等，插入队列
        Long key = Long.parseLong(betCode.toString());
        Integer multiple = chaiDanBetCode.get(key);
        if ( multiple != null ) {
            chaiDanBetCode.put(key, (chaiDanBetCode.get(key) + betCode.getMultiple()) );
        } else {
            chaiDanBetCode.put(key, betCode.getMultiple());
        }
//        chaiDanBetCode.add(betCode);
    }

    /**
     * 拼接红、蓝球为，如果号码小于10，在前面补0。
     * 如9，补0之后为 09
     * @param sb 拼接之后存入StringBuilder
     * @param balls 要拼接的红、蓝球列表
     */
    public static void combineBalls(StringBuilder sb, ArrayList<Integer> balls) {
        for (int i : balls) {
            if (i < 10)
                sb.append("0");
            sb.append(i);
        }
    }

    /**
     * 把betCode拼接成JSON字符串
     * @param str betCode字符串
     * @return JSON格式的字符串，包括betCode和倍数
     */
    public static String getJSONStr(String str) {
        String jsonStr;
        String mutiple = str.split("#")[1];
        jsonStr = "{ \"msg\": \"DONE\", \"result\": { \"success\": {\"list\": [  { \"code\": \"" +
                str.split("#")[0] + "\", \"multiple\":" + mutiple +
                "} ]}, \"failure\": {\"list\": []} }, \"code\": 0 }";
        return jsonStr;

    }

    public static String removeZhuiJia(String str) {
        return str.replace(":8","");
    }
}
