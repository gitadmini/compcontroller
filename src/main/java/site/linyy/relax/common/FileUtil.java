package site.linyy.relax.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/** 文件工具类.
 */
public class FileUtil {

    /**删除字符串匹配的行.
     */
    public static void deleteLine(String path, String what) throws IOException {
        // 先读取所有不匹配的行到list中，重新保存一下
        List<String> list = new ArrayList<String>();
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String str = "";
        while ((str = br.readLine()) != null) {
            if (!str.equals(what)) {
                list.add(str);
            }
        }
        br.close();
        isr.close();
        fis.close();
        writeMulLine(path, list);
    }

    /**读文件，文件中一行对应List中一行.
     */
    public static List<String> readAllLine(String path) throws IOException {

        List<String> list = new ArrayList<String>();
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String str = "";
        while ((str = br.readLine()) != null) {
            list.add(str);
        }
        br.close();
        isr.close();
        fis.close();
        return list;
    }

    /**往文件写多行.
     */
    public static void writeMulLine(String path, List<String> list)
            throws IOException {

        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(file);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                writer.write(list.get(i) + "\n");
            }
        }
        writer.flush();
        writer.close();
    }

    /**往文件追加一行.
     */
    public static void writeOneLine(String path, String what)
            throws IOException {

        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(file, true);
        writer.write(what + "\n");
        writer.flush();
        writer.close();
    }

    /** 查询某个目录下的文件
     */
    public static List<Map<String, String>> getList(String path, String search)
            throws IOException {

        List<Map<String, String>> list = getList(path);
        if (StringUtils.isNotBlank(search)) {
            List<Map<String, String>> result = new ArrayList<Map<String, String>>();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).get("name").contains(search)) {
                        result.add(list.get(i));
                    }
                }
            }
            return result;
        } else {
            return list;
        }
    }

    /**获得文件夹中所有文件的绝对路径和文件类型.
     */
    public static List<Map<String, String>> getList(String path)
            throws IOException {

        if (StringUtils.isBlank(path)) {
            return null;
        }
        File file = new File(path);
        if (!file.isDirectory()) {
            return null;
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (int i = 0; i < files.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            String fullPath = files[i].getCanonicalPath();
            String[] paths = fullPath.split("\\\\");
            String name = paths[paths.length - 1];
            map.put("path", fullPath);
            map.put("name", name);
            map.put("flag", String.valueOf(files[i].isDirectory()));
            list.add(map);
        }
        return list;
    }

    // 获取根目录
    public static String[] getMenuFolder() {

        File[] files = File.listRoots();
        String[] p = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            try {
                String path = files[i].getCanonicalPath();
                p[i] = path;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return p;
    }
}
