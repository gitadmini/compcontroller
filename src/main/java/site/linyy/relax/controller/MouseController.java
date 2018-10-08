package site.linyy.relax.controller;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import site.linyy.relax.common.FileUtil;

@Controller
public class MouseController {

    static Robot robot; // 鼠标键盘

    static Toolkit toolkit; // 屏幕

    static double screenWidth;

    static double screenHeight;

    static Clipboard clip; // 剪切板

    static boolean rec = false; // false停止 true录制中

    static long recTime = 0; // 录制开始时间

    static List<String> recList = new ArrayList<String>(); // 录制的内容缓存

    static boolean canPlay = true; // 是否可以播放（可以控制此变量来终止播放）

    static {
        System.setProperty("java.awt.headless", "false");
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        toolkit = Toolkit.getDefaultToolkit();
        screenWidth = toolkit.getScreenSize().getWidth();
        screenHeight = toolkit.getScreenSize().getHeight();
        clip = toolkit.getSystemClipboard();
    }

    // 页面
    @GetMapping("")
    public String index(Model model) throws IOException {

        InetAddress ia = InetAddress.getLocalHost();
        List<Map<String, String>> playList = FileUtil.getList("./rec/");
        if (playList != null && playList.size() > 0) {
            for (int i = 0; i < playList.size(); i++) {
                Map<String, String> map = playList.get(i);
                map.remove("path");
                map.remove("flag");
                map.put("name", map.get("name").split("\\.txt")[0]); // 去掉末尾的.txt
                set(map, i); // 设置背景颜色
            }
        }

        model.addAttribute("rec", rec);
        model.addAttribute("host_ip", ia.getHostAddress());
        model.addAttribute("playList", playList);
        return "mouse";
    }

    // 设置背景颜色
    private void set(Map<String, String> map, int i) {

        switch (i) {
            case 0:
                map.put("background", "background:#1dc;");
                break;
            case 1:
                map.put("background", "background:#1bc;");
                break;
            case 2:
                map.put("background", "background:#19c;");
                break;
            case 3:
                map.put("background", "background:#17c;");
                break;

            case 4:
                map.put("background", "background:#5dc;");
                break;
            case 5:
                map.put("background", "background:#5bc;");
                break;
            case 6:
                map.put("background", "background:#59c;");
                break;
            case 7:
                map.put("background", "background:#57c;");
                break;

            case 8:
                map.put("background", "background:#5da;");
                break;
            case 9:
                map.put("background", "background:#5ba;");
                break;
            case 10:
                map.put("background", "background:#59a;");
                break;
            case 11:
                map.put("background", "background:#57a;");
                break;

            default:
                break;
        }
    }

    // 开始录制
    @PostMapping("/m/start_rec")
    @ResponseBody
    public String startRec() {

        rec = true;
        recTime = new Date().getTime();
        slip("-999999", "-999999");
        return null;
    }

    // 停止录制
    @PostMapping("/m/stop_rec")
    @ResponseBody
    public String stopRec() {

        rec = false;
        try {
            FileUtil.writeMulLine("./rec/" + recTime + ".txt", recList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        recList.clear();
        return null;
    }

    // 播放录制
    @PostMapping("/m/play")
    @ResponseBody
    public String play(String name) throws IOException, NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, InterruptedException {

        canPlay = true;
        List<String> list = FileUtil.readAllLine("./rec/" + name + ".txt");
        if (list != null && list.size() > 1) {
            long lastTime = Long.parseLong(list.get(0).split("\\$")[0]);
            for (int i = 0; i < list.size(); i++) {
                if (canPlay) {
                    String[] strs = list.get(i).split("\\$");
                    long time = Long.parseLong(strs[0]);
                    long sleepTime = time - lastTime; // 间隔时间
                    lastTime = time;
                    String methodName = strs[1]; // 函数名
                    String[] params = null; // 参数数组
                    Class[] parameterTypes = null;// 参数数组对应的类
                    if (strs.length == 3) {
                        params = strs[2].split("_");
                        parameterTypes = getParameterTypes(params);
                    }
                    Class clazz = this.getClass();
                    Method method = clazz.getMethod(methodName, parameterTypes);

                    Thread.sleep(sleepTime);
                    method.invoke(this, params);
                } else {
                    canPlay = true;
                    return null;
                }
            }
        }
        return null;
    }

    // 参数数组对应的类
    private Class[] getParameterTypes(String[] params) {

        if (params != null && params.length > 0) {
            Class[] result = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                result[i] = String.class;
            }
            return result;
        }
        return null;
    }

    // 停止播放
    @PostMapping("/m/kill_play")
    @ResponseBody
    public String killPlay() {

        canPlay = false;
        return null;
    }

    // 录制方法
    private void rec(String funcName, String... args) {

        if (rec) {
            // 时间$方法名$参数(1_2_3)
            long time = new Date().getTime();
            String param = "";
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    param += args[i] + "_";
                }
                param = param.substring(0, param.length() - 1);
            }
            String result = time + "$" + funcName + "$" + param;
            recList.add(result);
        }
    }

    /**
     *****************************以下为鼠标键盘操作****************************
     */

    // 滑动
    @GetMapping("/m/slip")
    @ResponseBody
    public String slip(String x, String y) {

        rec("slip", x, y);
        Point point = MouseInfo.getPointerInfo().getLocation();
        double mouseX = point.getX();
        double mouseY = point.getY();
        double targetX = mouseX + Double.parseDouble(x);
        double targetY = mouseY + Double.parseDouble(y);
        if (targetX < 0) {
            targetX = 0;
        }
        if (targetX > screenWidth) {
            targetX = screenWidth;
        }
        if (targetY < 0) {
            targetY = 0;
        }
        if (targetY > screenHeight) {
            targetY = screenHeight;
        }
        robot.mouseMove((int) targetX, (int) targetY);
        return null;
    }

    // 右击
    @PostMapping("/m/right")
    @ResponseBody
    public String right() {

        rec("right");
        robot.mousePress(InputEvent.BUTTON3_MASK);// 按下右键(1,2,3对应左，中，右键)
        robot.mouseRelease(InputEvent.BUTTON3_MASK);// 释放右键
        return null;
    }

    // 单击
    @PostMapping("/m/click")
    @ResponseBody
    public String click() {

        rec("click");
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        return null;
    }

    // 双击
    @PostMapping("/m/double")
    @ResponseBody
    public String doubleClick() {

        rec("doubleClick");
        robot.mousePress(InputEvent.BUTTON1_MASK);// 按下左键
        robot.mouseRelease(InputEvent.BUTTON1_MASK);// 释放左键
        robot.delay(100);// 停顿100毫秒,即0.1秒
        robot.mousePress(InputEvent.BUTTON1_MASK);// 按下左键
        robot.mouseRelease(InputEvent.BUTTON1_MASK);// 释放左键
        return null;
    }

    // 字符串输入
    @PostMapping("/m/str")
    @ResponseBody
    public String str(String str) {

        rec("str", str);
        if (str != null && !"".equals(str)) {
            Transferable tText = new StringSelection(str);
            clip.setContents(tText, null);
            robot.keyPress(KeyEvent.VK_CONTROL); // 按下ctrl
            robot.keyPress(KeyEvent.VK_V);// 按下v
            robot.keyRelease(KeyEvent.VK_V);// 释放v
            robot.keyRelease(KeyEvent.VK_CONTROL);// 释放ctrl
        }
        return null;
    }

    // 键盘输入
    @PostMapping("/m/keyboard")
    @ResponseBody
    public String keyboard(String str) {

        rec("keyboard", str);
        if (str != null && !"".equals(str)) {
            switch (str) {
                case "f5":
                    robot.keyPress(KeyEvent.VK_F5);
                    robot.keyRelease(KeyEvent.VK_F5);
                    break;
                case "esc":
                    robot.keyPress(KeyEvent.VK_ESCAPE);
                    robot.keyRelease(KeyEvent.VK_ESCAPE);
                    break;
                case "backspace":
                    robot.keyPress(KeyEvent.VK_BACK_SPACE);
                    robot.keyRelease(KeyEvent.VK_BACK_SPACE);
                    break;
                case "enter":
                    robot.keyPress(KeyEvent.VK_ENTER);
                    robot.keyRelease(KeyEvent.VK_ENTER);
                    break;
                default:
                    break;
            }
        }
        return null;
    }

}
