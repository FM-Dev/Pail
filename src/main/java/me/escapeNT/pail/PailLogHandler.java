package me.escapeNT.pail;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import me.escapeNT.pail.Util.ScrollableTextArea;
import me.escapeNT.pail.Util.Util;
import org.bukkit.Bukkit;

/**
 * Log Handler to print the console output to the GUI
 *
 * @author escapeNT
 */
public class PailLogHandler extends Handler {
    private ScrollableTextArea output;
    private static Pattern pattern = Pattern.compile("\\x1B\\[([0-9]{1,2}(;[0-9]{1,2})?)?[m|K]");
    private static String lastMessage = "";

    /**
     * Constructs a new log handler using the specified text area for output.
     *
     * @param output The JTextArea to write the log data to.
     */
    public PailLogHandler() {
        output = new ScrollableTextArea();
        output.setAutoscrolls(true);

        // TODO: ..........
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(Util.getPlugin(), new Runnable() {
            public void run() {
                PailLogHandler.lastMessage = "";
            }
        }, 10, 10);
    }

    public synchronized void publish(final LogRecord record) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String message = pattern.matcher(record.getMessage()).replaceAll("");

                if (message.equals(PailLogHandler.lastMessage)) {
                    return;
                }
                PailLogHandler.lastMessage = message;

                output.append(Color.GRAY, true, new SimpleDateFormat("hh:mm a").format(new Date(record.getMillis())));

                Color color = Color.BLACK;
                Level lv = record.getLevel();
                if (lv == Level.INFO) {
                    color = Color.BLUE;
                } else if (lv == Level.WARNING) {
                    color = Color.ORANGE;
                } else if (lv == Level.SEVERE) {
                    color = Color.RED;
                }

                output.append(color, " [" + record.getLevel().toString() + "] ");

                if (UIManager.getLookAndFeel().getName().equals("HiFi")) {
                    color = Color.WHITE;
                } else {
                    color = Color.BLACK;
                }
                for (String s : message.toString().trim().split(" ")) {
                    output.append(color, (((s.startsWith("[") && s.contains("]"))
                            || (s.startsWith("<") && s.contains(">")))), s.trim() + " ");
                }
                output.append(color, "\n");
            }
        });
    }

    public void flush() {
    }

    public void close() throws SecurityException {
    }

    public ScrollableTextArea getTextArea() {
        return output;
    }
}