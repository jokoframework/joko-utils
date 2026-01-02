package io.github.jokoframework;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String banner = "==========================\n" +
                "   Hola mundo Joko         \n" +
                "   Fecha y hora: " + fechaHora + "\n" +
                "==========================\n";
        String tmpDir = System.getProperty("java.io.tmpdir");
        File bannerFile = new File(tmpDir, "joko-banner.txt");
        try (FileWriter writer = new FileWriter(bannerFile, true)) { // true para append
            writer.write(banner + System.lineSeparator());
            logger.info("Banner escrito en: {}", bannerFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Error escribiendo el banner: {}", e.getMessage());
        }
    }
}
