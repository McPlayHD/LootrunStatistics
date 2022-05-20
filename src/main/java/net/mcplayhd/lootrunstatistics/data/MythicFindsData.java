package net.mcplayhd.lootrunstatistics.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcplayhd.lootrunstatistics.helpers.FileHelper;
import net.mcplayhd.lootrunstatistics.utils.Loc;
import net.mcplayhd.lootrunstatistics.utils.MythicFind;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.*;

public class MythicFindsData {

    private static final File file = new File(MODID + "/" + getPlayerUUID() + "/mythicFinds.json");
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .setPrettyPrinting()
            .create();

    protected List<MythicFind> mythicFinds = new ArrayList<>();

    public static MythicFindsData load() {
        try {
            return gson.fromJson(FileHelper.readFile(file), MythicFindsData.class);
        } catch (Exception ignored) {
            return new MythicFindsData();
        }
    }

    public void addMythic(String mythic, Loc loc) {
        MythicFind find = new MythicFind(
                mythic,
                getChestCountData().getTotalChests(),
                getDryData().getChestsDry(),
                loc.getX(), loc.getY(), loc.getZ(),
                new Date(),
                getDryData().getItemsDry(),
                getDryData().getEmeraldsDry()
        );
        mythicFinds.add(find);
        save();
        getDryData().reset();
    }

    public MythicFind getLastMythic() {
        if (mythicFinds.isEmpty()) {
            return null;
        } else {
            return mythicFinds.get(mythicFinds.size() - 1);
        }
    }

    public void save() {
        try {
            FileHelper.writeFile(file, gson.toJson(this));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
