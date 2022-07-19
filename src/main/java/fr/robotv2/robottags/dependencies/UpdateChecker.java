package fr.robotv2.robottags.dependencies;

import fr.robotv2.robottags.RobotTags;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {

    private final int resourceId;

    public UpdateChecker(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getVersion() {
        try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource="
                + resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {

            if (scanner.hasNext()) {
                return scanner.next();
            }
        } catch (IOException exception) {
            RobotTags.get().getLogger().info("Update: Cannot look for updates: " + exception.getMessage());
        }

        return null;
    }
}
