import java.io.*;
import java.util.ArrayList;

public class FileOperation {

    public void writeFile(StringBuilder sb, String fileName) {
        File f = new File(fileName);
        if (f.exists()) {
            f.delete();
        }
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(fileName,true);
            bw = new BufferedWriter(fw);
            bw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭
            try {
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> readFile(String fileName) {
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        String line;
        ArrayList<String> lines = new ArrayList<String>();
        try {
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }
}
