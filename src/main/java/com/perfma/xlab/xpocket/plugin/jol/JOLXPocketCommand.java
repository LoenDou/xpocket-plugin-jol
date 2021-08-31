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
@CommandInfo(name = "help", usage = "Usage: <mode> [optional arguments]*", index = 0)
@CommandInfo(name = "internals", usage = "Show the object internals: field layout and default contents, object header.", index = 1)
@CommandInfo(name = "externals", usage = "Show the object externals: the objects reachable from a given instance.", index = 2)
@CommandInfo(name = "estimates", usage = "Simulate the class layout in different VM modes.", index = 3)
@CommandInfo(name = "footprint", usage = "Estimate the footprint of all objects reachable from a given instance.", index = 4)
@CommandInfo(name = "heapdumpstats", usage = "Consume the heap dump and print the most frequent instances.", index = 5)
@CommandInfo(name = "shapes", usage = "Dump the object shapes present in JAR files or heap dumps.", index = 6)
@CommandInfo(name = "string-compress", usage = "Consume the heap dumps and figures out the savings attainable with compressed strings.", index = 7)
public class JOLXPocketCommand extends AbstractXPocketCommand {

    private JOLXPocketPlugin jolxPocketPlugin;

    @Override
    public void init(XPocketPlugin plugin) {
        this.jolxPocketPlugin = (JOLXPocketPlugin)plugin;
    }

    @Override
    public void invoke(XPocketProcess process) throws Throwable {

        jolxPocketPlugin.invoke(process);
        /*XPocketProcessTemplate.execute(process,
                (String cmd, String[] args) -> 
                        String.format("EXECUTION %s %s",cmd , 
                                args == null ? null : Arrays.toString(args)));*/
    }



}
