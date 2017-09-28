import java.util.ArrayList;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<Long, Integer> chaiDanBetCode1, chaiDanBetCode2;
        StringBuilder sb =  new StringBuilder();
        FileOperation fileOperation = new FileOperation();
        String resultPath = "result.txt";
        String betCodeFilePath = "betcode.txt";
        ArrayList<String> lines= fileOperation.readFile(betCodeFilePath);
        for (String line : lines) {
            String str1 = Util.getJSONStr(line.split("\t")[0]);
            String str2 = line.split("\t")[1];
            DLTBetCode betCode = new DLTBetCode();
            chaiDanBetCode1 = betCode.chaidan(Util.removeZhuiJia(str1));
            chaiDanBetCode2 = betCode.chaidan(Util.removeZhuiJia(str2));
            boolean result = betCode.compareChaiDan(chaiDanBetCode1,chaiDanBetCode2);
            System.out.println("Line " + lines.lastIndexOf(line) + " " + result);
            sb.append(result);
            sb.append("\t");
            sb.append(line.split("\t")[0]);
            sb.append("\n");
        }
        //写所有拆单结果到文件
        fileOperation.writeFile(sb, resultPath);
    }
}
