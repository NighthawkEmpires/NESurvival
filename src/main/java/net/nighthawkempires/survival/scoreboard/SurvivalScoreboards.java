package net.nighthawkempires.survival.scoreboard;

import net.nighthawkempires.core.language.Lang;
import net.nighthawkempires.core.scoreboard.Scoreboards;
import net.nighthawkempires.survival.NESurvival;
import net.nighthawkempires.survival.user.UserModel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.concurrent.atomic.AtomicReference;

public class SurvivalScoreboards extends Scoreboards {

    private int task = 0;

    public String getName() {
        return "survival";
    }

    public int getTaskID() {
        return task;
    }

    public int getNumber() {
        return 1;
    }

    public Scoreboard getFor(Player player) {
        final Scoreboard[] scoreboard = {Bukkit.getScoreboardManager().getNewScoreboard()};
        final Objective[] objective = {scoreboard[0].registerNewObjective("test", "dummy")};
        objective[0].setDisplaySlot(DisplaySlot.SIDEBAR);
        objective[0].setDisplayName(Lang.SCOREBOARD.getServerBoard());
        UserModel user = NESurvival.getUserRegistry().getUser(player.getUniqueId());
        Team kills = scoreboard[0].registerNewTeam("kills");
        kills.addEntry(ChatColor.GRAY + " ➛   " + ChatColor.GREEN + "" + ChatColor.BOLD);
        kills.setPrefix("");
        kills.setSuffix("");
        Team death = scoreboard[0].registerNewTeam("death");
        death.addEntry(ChatColor.GRAY + " ➛   " + ChatColor.RED + "" + ChatColor.BOLD);
        death.setPrefix("");
        death.setSuffix("");
        Team ratio = scoreboard[0].registerNewTeam("ratio");
        ratio.addEntry(ChatColor.GRAY + " ➛   " + ChatColor.GOLD + "" + ChatColor.BOLD);
        ratio.setPrefix("");
        ratio.setSuffix("");

        objective[0].getScore(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "------------").setScore(10);
        objective[0].getScore(ChatColor.GRAY + "" + ChatColor.BOLD + " Kills" + ChatColor.GRAY + ": ").setScore(9);
        objective[0].getScore(ChatColor.GRAY + " ➛   " + ChatColor.GREEN + "" + ChatColor.BOLD).setScore(8);
        kills.setSuffix(ChatColor.GREEN + "" + ChatColor.BOLD + String.valueOf(user.getKills()));
        objective[0].getScore(ChatColor.DARK_PURPLE + " ").setScore(7);
        objective[0].getScore(ChatColor.GRAY + "" + ChatColor.BOLD + " Deaths" + ChatColor.GRAY + ": ").setScore(6);
        objective[0].getScore(ChatColor.GRAY + " ➛   " + ChatColor.RED + "" + ChatColor.BOLD).setScore(5);
        death.setSuffix(ChatColor.RED + "" + ChatColor.BOLD + String.valueOf(user.getDeaths()));
        objective[0].getScore(ChatColor.YELLOW + "  ").setScore(4);
        objective[0].getScore(ChatColor.GRAY + "" + ChatColor.BOLD + " K/D Ratio" + ChatColor.GRAY + ": ").setScore(3);
        objective[0].getScore(ChatColor.GRAY + " ➛   " + ChatColor.GOLD + "" + ChatColor.BOLD).setScore(2);
        AtomicReference<Double> kdratio = new AtomicReference<>(((double) user.getKills()) / ((double) user.getDeaths()));
        try {
            ratio.setSuffix(ChatColor.GOLD + "" + ChatColor.BOLD + String.valueOf(kdratio));
        } catch (Exception e) {
            ratio.setSuffix("n/a");
        }
        objective[0].getScore(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "" + ChatColor.BOLD + "-----------").setScore(1);

        this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(NESurvival.getPlugin(), () -> {
            kills.setSuffix(ChatColor.GREEN + "" + ChatColor.BOLD + String.valueOf(user.getKills()));
            death.setSuffix(ChatColor.RED + "" + ChatColor.BOLD + String.valueOf(user.getDeaths()));
            kdratio.set(((double) user.getKills()) / ((double) user.getDeaths()));
            try {
                ratio.setSuffix(ChatColor.GOLD + "" + ChatColor.BOLD + String.valueOf(kdratio));
            } catch (Exception e) {
                ratio.setSuffix("n/a");
            }        }, 0, 5);
        Bukkit.getScheduler().scheduleSyncDelayedTask(NESurvival.getPlugin(), () -> Bukkit.getScheduler().cancelTask(this.task), 14*20);
        return scoreboard[0];
    }
}
