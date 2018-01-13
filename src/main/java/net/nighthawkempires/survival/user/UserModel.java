package net.nighthawkempires.survival.user;

import com.demigodsrpg.util.datasection.DataSection;
import com.demigodsrpg.util.datasection.Model;
import net.nighthawkempires.survival.NESurvival;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserModel implements Model {

    private UUID uuid;
    private String name;
    private int kills;
    private int deaths;

    public UserModel(UUID uuid) {
        this.uuid = uuid;
        this.name = Bukkit.getOfflinePlayer(uuid).getName();
        this.kills = 0;
        this.deaths = 0;
    }

    public UserModel(String key, DataSection data) {
        this.uuid = UUID.fromString(key);
        this.name = Bukkit.getOfflinePlayer(uuid).getName();
        this.kills = data.getInt("kills");
        this.deaths = data.getInt("deaths");
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        NESurvival.getUserRegistry().register(this);
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
        NESurvival.getUserRegistry().register(this);
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        NESurvival.getUserRegistry().register(this);
    }

    @Override
    public String getKey() {
        return uuid.toString();
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("kills", kills);
        map.put("deaths", deaths);
        return map;
    }
}
