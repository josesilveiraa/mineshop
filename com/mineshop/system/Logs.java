package com.mineshop.system;

import com.mineshop.*;
import java.util.*;
import java.text.*;
import java.io.*;

public class Logs
{
    public static void logToFile(final String message) {
        if (Main.getInstance().getConfig().getBoolean("gerar-logs")) {
            try {
                final File dataFolder = new File("plugins" + File.separator + "MineSHOP");
                if (!dataFolder.exists()) {
                    dataFolder.mkdir();
                }
                final File saveTo = new File("plugins" + File.separator + "MineSHOP", "mineshop-log.txt");
                if (!saveTo.exists()) {
                    saveTo.createNewFile();
                }
                final FileWriter fw = new FileWriter(saveTo, true);
                final PrintWriter pw = new PrintWriter(fw);
                final Calendar cal = Calendar.getInstance();
                final SimpleDateFormat sdf = new SimpleDateFormat("[dd/MM/yyyy hh:mm:ss] ");
                final String data = sdf.format(cal.getTime());
                pw.println(String.valueOf(data) + message);
                pw.flush();
                pw.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
