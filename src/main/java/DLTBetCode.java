import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

public class DLTBetCode {


    /**
     * 比较两种方式的拆单结果
     * @param chaiDanBetCode1 方法1的拆单结果
     * @param chaiDanBetCode2 方法2的拆单结果
     * @return 比较结果
     */
    public boolean compareChaiDan(Map<Long, Integer> chaiDanBetCode1, Map<Long, Integer> chaiDanBetCode2) {
        if (chaiDanBetCode1.size() != chaiDanBetCode2.size()) {
            System.out.println("Not the same!");
            System.out.println("chaiDanBetCode1:" + chaiDanBetCode1.size());
            System.out.println("chaiDanBetCode2:" + chaiDanBetCode2.size());
            return false;
        } else {
            Iterator<Map.Entry<Long, Integer>> iterator1 = chaiDanBetCode1.entrySet().iterator();
            Iterator<Map.Entry<Long, Integer>> iterator2 = chaiDanBetCode2.entrySet().iterator();
            while (iterator1.hasNext()) {
                Map.Entry entry1 = iterator1.next();
                Map.Entry entry2 = iterator2.next();
                if ( !entry1.equals(entry2) ) {
                    System.out.println("Not the same!");
                    System.out.println("chaiDanBetCode1:" + entry1.getKey() + " " + entry1.getValue());
                    System.out.println("chaiDanBetCode2:" + entry2.getKey() + " " + entry2.getValue());
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 拆单方法，分别调用拆复式、拆胆拖方法
     * @param str json格式的betcode
     * @return 拆单结果的集合，<拆单结果，倍数>的形式
     */
    public Map<Long, Integer> chaidan(String str) {
        ArrayList<DLTBetCode> betCodeArrayList = transferStr(str);
        Map<Long, Integer> chaiDanBetCode = new TreeMap<>();

        //调用拆复式或拆胆拖方法
        for (DLTBetCode bc : betCodeArrayList) {
            if (bc.getRedBalls() != null) {
                chaiFuShi(bc, chaiDanBetCode);
            } else {
                chaiDantuo(bc, chaiDanBetCode);
            }
        }
        return chaiDanBetCode;
    }

    /**
     * 复式的拆单方法
     * @param betCode 要被进行拆单的betcode对象
     * @param chaiDanBetCode 储存拆单结果
     */
    protected void chaiFuShi(DLTBetCode betCode, Map<Long, Integer> chaiDanBetCode) {

        ArrayList<int[]> redCodeCombination = new ArrayList<int[]>();
        ArrayList<int[]> blueCodeCombination = new ArrayList<int[]>();
        //拆分红球排列组合
        redCodeCombination = Util.combinationSelect(redCodeCombination, betCode.getRedBalls(),
                0, new int[5], 0);
        //拆分蓝球排列组合
        blueCodeCombination = Util.combinationSelect(blueCodeCombination, betCode.getBlueBalls(),
                0, new int[2], 0);
        DLTBetCode bc = null;
        for(int[] i : redCodeCombination) {
            for (int[] j : blueCodeCombination) {
                bc = new DLTBetCode();
                //存入红球
                bc.setRedBalls(Util.ArrayToArrayList(i));
                //存入蓝球
                bc.setBlueBalls(Util.ArrayToArrayList(j));
                //存入倍数
                bc.setMultiple(betCode.getMultiple());
                Util.addWithMultiple(chaiDanBetCode, bc);
            }
        }
    }

    /**
     * 胆拖的拆单方法
     * @param betCode 要被进行拆单的betcode对象
     * @param chaiDanBetCode 储存拆单结果
     */
    protected void chaiDantuo(DLTBetCode betCode, Map<Long, Integer> chaiDanBetCode) {
        ArrayList<int[]> redTuoBallCombination = new ArrayList<>();
        ArrayList<int[]> blueTuoBallCombination = new ArrayList<>();
        DLTBetCode bc;
        //拆分红球拖码排列组合
        if (betCode.getRedDanBalls() != null) {
            redTuoBallCombination = Util.combinationSelect(redTuoBallCombination, betCode.getRedTuoBalls(),
                    0, new int[5 - betCode.getRedDanBalls().size()], 0);
        } else {
            redTuoBallCombination = Util.combinationSelect(redTuoBallCombination, betCode.getRedTuoBalls(),
                    0, new int[5], 0);
        }

        //拆分蓝球拖码排列组合
        if (betCode.getBlueDanBalls() != null) {
            blueTuoBallCombination = Util.combinationSelect(blueTuoBallCombination, betCode.getBlueTuoBalls(),
                    0, new int[2 - betCode.getBlueDanBalls().size()], 0);
        } else {
            blueTuoBallCombination = Util.combinationSelect(blueTuoBallCombination, betCode.getBlueTuoBalls(),
                    0, new int[2], 0);
        }


        //合并红球胆码、拖码、蓝球胆码、拖码
        for (int[] redTuo : redTuoBallCombination) {
            for (int[] blueTuo : blueTuoBallCombination) {
                bc = new DLTBetCode();
                ArrayList<Integer> redTemp = new ArrayList<>();
                ArrayList<Integer> blueTemp = new ArrayList<>();

                //红球胆码拖码合并之后存入红球
                if (betCode.getRedDanBalls() != null) {
                    redTemp.addAll(betCode.getRedDanBalls());
                }
                redTemp.addAll(Util.ArrayToArrayList(redTuo));
                Collections.sort(redTemp);
                bc.setRedBalls(redTemp);

                //蓝球胆码拖码合并之后存入蓝球
                if (betCode.getBlueDanBalls() != null) {
                    blueTemp.addAll(betCode.getBlueDanBalls());
                }
                blueTemp.addAll(Util.ArrayToArrayList(blueTuo));
                Collections.sort(blueTemp);
                bc.setBlueBalls(blueTemp);

                //存入倍数
                bc.setMultiple(betCode.getMultiple());
                Util.addWithMultiple(chaiDanBetCode, bc);
            }
        }
    }

    /**
     * 把json格式的betcode字符串转换为betcode对象储存在列表
     * @param jsonStr
     * @return
     */
    public ArrayList<DLTBetCode> transferStr(String jsonStr) {
        JSONObject successObject = JSONObject.fromObject(jsonStr).getJSONObject("result").getJSONObject("success");
        JSONArray betCodes = successObject.getJSONArray("list");
        ArrayList<DLTBetCode> betCodeArrayList = new ArrayList<>();
        for (int i = 0; i < betCodes.size(); i++) {
            String betCodeStr = betCodes.getJSONObject(i).getString("code");
            int multiple = Integer.parseInt(betCodes.getJSONObject(i).getString("multiple"));
            //拆分多个行注
            String redBallStr,blueBallStr;
            if (betCodeStr.contains(";")) {
                String[] bcList = betCodeStr.split(";");
                for (String str : bcList) {
                    redBallStr = str.split("\\|")[0];
                    blueBallStr = str.split("\\|")[1];
                    storeBetCode(betCodeArrayList, str, redBallStr, blueBallStr, multiple);
                }
            } else {
                redBallStr = betCodeStr.split("\\|")[0];
                blueBallStr = betCodeStr.split("\\|")[1];
                storeBetCode(betCodeArrayList, betCodeStr, redBallStr, blueBallStr, multiple);
            }
        }
        return betCodeArrayList;
    }

    private void storeBetCode(ArrayList<DLTBetCode> betCodeArrayList, String betCodeStr,
                              String redBallStr, String blueBallStr, int multiple) {
        DLTBetCode betCode = new DLTBetCode();
        betCode.setMultiple(multiple);
        //区分胆拖和复式
        if(betCodeStr.contains("$")) {
            //红球有胆拖和没有胆拖
            if (betCodeStr.split("\\|")[0].contains("$")) {
                //红球胆码
                ArrayList<Integer> redDanBalls = Util.StringToIntArray(betCodeStr.
                        split("\\|")[0].split("\\$")[0]);
                betCode.setRedDanBalls(redDanBalls);

                //红球拖码
                ArrayList<Integer> redTuoBalls = Util.StringToIntArray(betCodeStr.
                        split("\\|")[0].split("\\$")[1]);
                betCode.setRedTuoBalls(redTuoBalls);
            } else {
                ArrayList<Integer> redTuoBalls = Util.StringToIntArray(betCodeStr.
                        split("\\|")[0]);
                betCode.setRedTuoBalls(redTuoBalls);
            }

            //蓝区有胆拖和没有胆拖（只有拖码）
            if (betCodeStr.split("\\|")[1].contains("$")) {
                //蓝球胆码
                ArrayList<Integer> blueDanBalls = Util.StringToIntArray(betCodeStr.
                        split("\\|")[1].split("\\$")[0]);
                betCode.setBlueDanBalls(blueDanBalls);

                //蓝球拖码
                ArrayList<Integer> blueTuoBalls = Util.StringToIntArray(betCodeStr.
                        split("\\|")[1].split("\\$")[1]);
                betCode.setBlueTuoBalls(blueTuoBalls);
            } else {
                //只有拖码
                ArrayList<Integer> blueTuoBalls = Util.StringToIntArray(betCodeStr.
                        split("\\|")[1]);
                betCode.setBlueTuoBalls(blueTuoBalls);
            }


            betCodeArrayList.add(betCode);
        } else {
            ArrayList<Integer> redBalls = Util.StringToIntArray(redBallStr);
            ArrayList<Integer> blueBalls = Util.StringToIntArray(blueBallStr);
            betCode.setRedBalls(redBalls);
            betCode.setBlueBalls(blueBalls);
            betCodeArrayList.add(betCode);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        //拼接红球
        Util.combineBalls(stringBuilder, redBalls);
        //拼接蓝球
        Util.combineBalls(stringBuilder, blueBalls);

        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        return redBalls.hashCode() + blueBalls.hashCode() + multiple;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DLTBetCode) {
            DLTBetCode bc = (DLTBetCode) obj;
            return redBalls.equals(bc.getRedBalls()) &&
                    blueBalls.equals(bc.getBlueBalls()) &&
                    multiple == bc.getMultiple();
        }
        return false;
    }


    //红球
    private ArrayList<Integer> redBalls;

    //蓝球
    private ArrayList<Integer> blueBalls;

    public ArrayList<Integer> getRedBalls() {
        return redBalls;
    }

    public void setRedBalls(ArrayList<Integer> redBalls) {
        this.redBalls = redBalls;
    }

    public ArrayList<Integer> getBlueBalls() {
        return blueBalls;
    }

    public void setBlueBalls(ArrayList<Integer> blueBalls) {
        this.blueBalls = blueBalls;
    }

    public int getMultiple() {
        return multiple;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;
    }

    public ArrayList<Integer> getRedDanBalls() {
        return redDanBalls;
    }

    public void setRedDanBalls(ArrayList<Integer> redDanBalls) {
        this.redDanBalls = redDanBalls;
    }

    public ArrayList<Integer> getRedTuoBalls() {
        return redTuoBalls;
    }

    public void setRedTuoBalls(ArrayList<Integer> redTuoBalls) {
        this.redTuoBalls = redTuoBalls;
    }

    public ArrayList<Integer> getBlueDanBalls() {
        return blueDanBalls;
    }

    public void setBlueDanBalls(ArrayList<Integer> blueDanBalls) {
        this.blueDanBalls = blueDanBalls;
    }

    public ArrayList<Integer> getBlueTuoBalls() {
        return blueTuoBalls;
    }

    public void setBlueTuoBalls(ArrayList<Integer> blueTuoBalls) {
        this.blueTuoBalls = blueTuoBalls;
    }

    //倍数
    private int multiple;

    //红球胆码
    private ArrayList<Integer> redDanBalls;

    //红球拖码
    private ArrayList<Integer> redTuoBalls;

    //蓝球胆码
    private  ArrayList<Integer> blueDanBalls;

    //蓝球拖码
    private  ArrayList<Integer> blueTuoBalls;


}
