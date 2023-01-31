package com.artingl.easyrg.Logger;

import com.artingl.easyrg.PluginMain;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MWLogger extends Logger {

    public MWLogger() {
        super(PluginMain.instance.getPluginInfo().getName(), null);
        setParent(PluginMain.instance.getServer().getLogger());
        setLevel(Level.ALL);
    }

}
