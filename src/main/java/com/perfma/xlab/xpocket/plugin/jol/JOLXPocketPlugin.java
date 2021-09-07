package com.perfma.xlab.xpocket.plugin.jol;

import com.perfma.xlab.xpocket.spi.AbstractXPocketPlugin;
import com.perfma.xlab.xpocket.spi.context.SessionContext;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Dou
 * @version 1.0
 * @description 这个类主要用于插件整体的声明周期管理和日志输出等，如非必要可以不实现
 * @date 7/31/2021 12:29
 */
public class JOLXPocketPlugin extends AbstractXPocketPlugin {

    //Logo
    private static final String LOGO = "        .'|   .'|=|`.     .'|      \n" +
                                       "      .'  | .'  | |  `. .'  |      \n" +
                                       "      |   | |   | |   | |   |      \n" +
                                       " ___  |   | `.  | |  .' |   |  ___ \n" +
                                       " `._|=|__.'   `.|=|.'   |___|=|_.' \n";

    //获取不同操作系统下的用户空间
    private static final String USER_HOME = System.getProperty("user.home");

    //在用户空间下创建目录存放需要用到的Lib文件，目前路径暂定为  /用户空间/.xpocket/.jol
    private static final String TMP_LIB_PATH = USER_HOME + File.separator + ".xpocket" + File.separator + ".jol" + File.separator;

    //当前工程Lib文件所在位置
    private static final String PRO_LIB_PATH = ".jol" + File.separator + "lib";

    //插件所需要的Lib文件
    private static final String JOL_LIB = "jol-cli-latest.jar";

    private XPocketProcess process;

    private Process jolProcess;

    /**
     * 插件首次被初始化时被调用
     * 创建用户空间下的.xpocket目录
     * @param process
     */
    @Override
    public void init(XPocketProcess process) {

        File file = new File(TMP_LIB_PATH);

        //目标文件夹是否已经存在
        if (file.exists()) {
            return;
        }

        //不存在则创建
        file.mkdirs();

        //将所需Lib放置到TMP_LIB_PATH
        try (InputStream inputStream = JOLXPocketPlugin.class.getClassLoader().getResourceAsStream(".jol/lib/" + JOL_LIB)){
            Path copyFilePath = new File(TMP_LIB_PATH + JOL_LIB).toPath();
            Files.copy(inputStream,copyFilePath);
        }catch (Throwable e){
            e.printStackTrace();
            process.output(e.getMessage());
        }
    }


    /**
     * 用于输出自定义LOGO
     * @param process 
     */
    @Override
    public void printLogo(XPocketProcess process) {
        process.output(LOGO);
    }

    /**
     * 插件会话被切出时被调用
     * @param context 
     */
    @Override
    public void switchOff(SessionContext context) {
        super.switchOff(context); 
    }

    /**
     * 插件会话被切入时被调用
     * @param context 
     */
    @Override
    public void switchOn(SessionContext context) {
        super.switchOn(context); 
    }

    /**
     * XPocket整体退出时被调用，用于清理插件本身使用的资源
     * @throws Throwable 
     */
    @Override
    public void destory() throws Throwable {
        //销毁子进程
        if (jolProcess.isAlive()){
            jolProcess.getOutputStream().write("quit".getBytes());
            jolProcess.getOutputStream().flush();
            jolProcess.destroy();
        }
        super.destory(); 
    }

    //主要启动逻辑    通过Runtime.getRuntime().exec调用解压出来的Lib
    public void invoke(XPocketProcess process) throws Throwable{
        this.process = process;
        //获取命令行
        String cmd = process.getCmd();
        //获取传递的参数
        String[] args = process.getArgs();

        if (cmd == null || cmd.trim().length() == 0){
            process.output("Please enter the correct parameters.");
            printHelp();
            process.end();
        }

        if (cmd.equals("help")){
            printHelp();
            process.end();
        } else if (cmd.equals("internals") || cmd.equals("externals") || cmd.equals("estimates") || cmd.equals("footprint") || cmd.equals("heapdumpstats")  || cmd.equals("shapes") || cmd.equals("string-compress") ){
            printInfos(cmd,args);
            process.end();
        } else {
            jolProcess.getOutputStream().write(handleCmd(process.getCmd(),
                    process.getArgs()));
            jolProcess.getOutputStream().flush();
            process.end();
        }

        //废弃  除help外其余命令通用
        /*switch (cmd) {
            case "help":
                printHelp();
                process.end();
                break;
            case "internals":
                printInternals(cmd,args);
                process.end();
                break;
            default:
                jolProcess.getOutputStream().write(handleCmd(process.getCmd(),
                        process.getArgs()));
                jolProcess.getOutputStream().flush();
                process.end();
        }*/
    }

    //打印帮助信息  原生方法  # 弃用   使用改造后的提示
    private void printHelp_native(){
        try {
            jolProcess = Runtime.getRuntime().exec("java -jar " + TMP_LIB_PATH + JOL_LIB + " help");
            try (InputStream in = jolProcess.getInputStream()){
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    process.output(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            process.output(e.getMessage());
        }
    }

    //打印帮助信息
    private void printHelp(){

        //line separator
        String lineSeparator = System.getProperty("line.separator");

        //print info
        StringBuilder sb = new StringBuilder();

        sb.append("Usage:<mode> -path: [optional arguments]*: \n\t mode(选择命令类型). \n\t -path:(要查看的Java类所在的绝对路径) \n\t [optional arguments]*(要查看的Java类名称，不用带后缀.class).").append(lineSeparator).append(lineSeparator);
        sb.append("Available modes: ").append(lineSeparator);

        sb.append(String.format("  %20s: %s%n", "internals", "Show the object internals: field layout and default contents, object header(显示对象内部:字段布局和默认内容，对象头)."))
                .append(String.format("  %20s: %s%n", "externals", "Show the object externals: the objects reachable from a given instance(显示对象外部:从实例可达的对象)."))
                .append(String.format("  %20s: %s%n", "footprint", "Estimate the footprint of all objects reachable from a given instance(估计从实例可达的对象的引用信息)."))
                .append(String.format("  %20s: %s%n", "estimates", "Simulate the class layout in different VM modes(模拟在不同VM模式下类的布局)."))
                .append(String.format("  %20s: %s%n", "heapdumpstats", "Consume the heap dump and print the most frequent instances(使用heap dump并打印最频繁的实例)."))
                .append(String.format("  %20s: %s%n", "shapes", "Dump the object shapes present in JAR files or heap dumps(Dump JAR文件或heap dumps中存在的对象结构)."))
                .append(String.format("  %20s: %s%n", "string-compress", "Consume the heap dumps and figures out the savings attainable with compressed strings(使用heap dumps并计算压缩字符串可获得的节省)."));

        process.output(sb.toString());

    }

    //打印internals等逻辑的信息
    private void printInfos(String cmd,String[] args){
        if (args == null || args.length == 0){
            process.output("Please enter the correct parameters.");
            return;
        }

        //java额外加载外部类-Xbootclasspath/a:路径(即所需查看class对象类所在jar路径)         windows示例：D:/Test.jar 或 D:\\\\Test.jar     linux示例：/home/dou/Test.jar
        String classPath = "";

        //所需查看class的名称，不用加后缀.class
        String className = "";

        if (args[0].contains("-path:")){
            classPath = args[0].substring(6,args[0].length());
            if (args.length > 1){
                className = args[1];
            }
        }else {
            //可以不写classpath，但是此时获取的class对象信息是当前JVM的类对象信息
            className = args[0];
        }

        try {
            String exeCmd = classPath.isEmpty()
                    ? "java " + " -jar " + TMP_LIB_PATH + JOL_LIB + " " + cmd + " " + className
                    : "java " + "-Xbootclasspath/a:" + classPath + " -jar " + TMP_LIB_PATH + JOL_LIB + " " + cmd + " " + className;

            jolProcess = Runtime.getRuntime().exec(exeCmd);
            try (InputStream in = jolProcess.getInputStream()){
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    process.output(line);
                }
            }
            //如果有错误，输出错误信息以便于排查问题
            try (InputStream in = jolProcess.getErrorStream()){
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    process.output(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            process.output(e.getMessage());
        }
    }

    private byte[] handleCmd(String cmd, String[] args) {
        StringBuilder cmdStr = new StringBuilder(cmd).append(' ');

        if (args != null) {
            for (String arg : args) {
                cmdStr.append(arg).append(' ');
            }
        }

        cmdStr.append("\n");

        return cmdStr.toString().getBytes();
    }
}
