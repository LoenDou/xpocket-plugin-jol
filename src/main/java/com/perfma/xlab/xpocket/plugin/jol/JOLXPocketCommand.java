package com.perfma.xlab.xpocket.plugin.jol;

import com.perfma.xlab.xpocket.spi.XPocketPlugin;
import com.perfma.xlab.xpocket.spi.command.AbstractXPocketCommand;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

import java.util.Arrays;

/**
 * @author Dou
 * @version 1.0
 * @description 用于实现每个命令的核心逻辑，一个或者多个命令指向一个类。
 * @date 7/31/2021 12:29
 */
@CommandInfo(name = "help", usage = "How to use JOL(如何使用JOL).", index = 0)
@CommandInfo(name = "internals", usage = "Show the object internals: field layout and default contents, object header(显示对象内部:字段布局和默认内容，对象头).", index = 1)
@CommandInfo(name = "externals", usage = "Show the object externals: the objects reachable from a given instance(显示对象外部:从实例可达的对象).", index = 2)
@CommandInfo(name = "footprint", usage = "Estimate the footprint of all objects reachable from a given instance(估计从实例可达的对象的引用信息).", index = 3)
@CommandInfo(name = "estimates", usage = "Simulate the class layout in different VM modes(模拟在不同VM模式下类的布局).", index = 4)
@CommandInfo(name = "heapdumpstats", usage = "Consume the heap dump and print the most frequent instances(使用heap dump并打印最频繁的实例).", index = 5)
@CommandInfo(name = "shapes", usage = "Dump the object shapes present in JAR files or heap dumps(Dump JAR文件或heap dumps中存在的对象结构).", index = 6)
@CommandInfo(name = "string-compress", usage = "Consume the heap dumps and figures out the savings attainable with compressed strings(使用heap dumps并计算压缩字符串可获得的节省).", index = 7)
public class JOLXPocketCommand extends AbstractXPocketCommand {

    private JOLXPocketPlugin jolxPocketPlugin;

    @Override
    public void init(XPocketPlugin plugin) {
        this.jolxPocketPlugin = (JOLXPocketPlugin)plugin;
    }

    @Override
    public void invoke(XPocketProcess process) throws Throwable {

        jolxPocketPlugin.invoke(process);

    }



}
