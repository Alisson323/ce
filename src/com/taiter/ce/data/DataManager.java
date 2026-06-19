package com.taiter.ce.data;

import java.io.File;
import com.taiter.ce.Main;

public class DataManager {

    public File getDataFolder() {
        return Main.plugin.getDataFolder();
    }

    public boolean createDataDirectory() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            return dataFolder.mkdirs();
        }
        return true;
    }
}
