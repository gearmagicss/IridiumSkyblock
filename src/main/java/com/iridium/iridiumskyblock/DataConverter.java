package com.iridium.iridiumskyblock;

import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.IslandUpgrade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DataConverter {

    private static IridiumSkyblock iridiumSkyblock;

    public static void run(IridiumSkyblock instance) {
        iridiumSkyblock = instance;

        v3_0_0();
    }

    public static void deleteDuplicateUpgrades() {
        List<String> islandUpgrades = new ArrayList<>();
        List<IslandUpgrade> remove = new ArrayList<>();
        for (IslandUpgrade islandUpgrade : IridiumSkyblock.getInstance().getDatabaseManager().getIslandUpgradeTableManager().getEntries()) {
            Optional<Island> island = islandUpgrade.getIsland();
            if (island.isPresent()) {
                if (islandUpgrades.contains(islandUpgrade.getUpgrade() + " - " + island.get().getId())) {
                    remove.add(islandUpgrade);
                } else {
                    islandUpgrades.add(islandUpgrade.getUpgrade() + " - " + island.get().getId());
                }
            } else {
                remove.add(islandUpgrade);
            }
        }
        IridiumSkyblock.getInstance().getDatabaseManager().getIslandUpgradeTableManager().delete(remove);
    }

    private static void v3_0_0() {
        String[] fileNames = Objects.requireNonNull(iridiumSkyblock.getDataFolder().list());
        if (!Arrays.asList(fileNames).contains("config.json")) {
            // Don't run this converter
            return;
        }

        iridiumSkyblock.getLogger().info("Starting 2.6.7 -> 3.0.0 conversion process...");
        new File(iridiumSkyblock.getDataFolder() + File.separator + "old_data" + File.separator + "schematics" + File.separator).mkdirs();

        Arrays.stream(Objects.requireNonNull(iridiumSkyblock.getDataFolder()
                .listFiles((file, fileName) -> fileName.endsWith(".json") || fileName.endsWith(".db"))))
                .forEach(file -> {
                    try {
                        Path source = Paths.get(file.getAbsolutePath());
                        Path target = Paths.get(file.getParentFile().getAbsolutePath() + File.separator + "old_data" + File.separator + file.getName());
                        iridiumSkyblock.getLogger().info("Moving " + file.getName() + " to " + target.toFile().getAbsolutePath() + "...");
                        Files.move(source, target);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        Arrays.stream(Objects.requireNonNull(new File(iridiumSkyblock.getDataFolder().getAbsolutePath() + File.separator + "schematics" + File.separator)
                .listFiles((file, fileName) -> fileName.endsWith(".schem") || fileName.endsWith(".schematic"))))
                .forEach(file -> {
                    try {
                        Path source = Paths.get(file.getAbsolutePath());
                        Path target = Paths.get(file.getParentFile().getParentFile().getAbsolutePath() + File.separator + "old_data" + File.separator + "schematics" + File.separator + file.getName());
                        iridiumSkyblock.getLogger().info("Moving " + file.getName() + " to " + target.toFile().getAbsolutePath() + "...");
                        Files.move(source, target);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

}
