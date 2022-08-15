package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.chat.ExTextColor;
import net.kyori.adventure.text.Component;
import org.bukkit.Instrument;
import org.bukkit.Note;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class LevelType<L extends Level<?>> {

    private final ExItemStack displayItem;
    private final String name;
    private final int baseLevel;
    private final int maxLevel;
    private final HashMap<Integer, L> levels;
    private int level;
    private List<LevelType<?>> conflictingTypes = new ArrayList<>();

    public LevelType(String name, ExItemStack displayItem, int baseLevel, int maxLevel, List<L> levels) {
        this.displayItem = displayItem;
        this.name = name;
        this.level = baseLevel;
        this.baseLevel = baseLevel;
        this.maxLevel = maxLevel;

        this.displayItem.setDisplayName("§6" + name);

        this.levels = new HashMap<>();

        for (L level : levels) {
            this.levels.put(level.getLevel(), level);
        }
    }

    protected LevelType(LevelType<L> levelType) {
        this.displayItem = levelType.displayItem.cloneWithId();
        this.name = levelType.name;
        this.level = levelType.baseLevel;
        this.baseLevel = levelType.baseLevel;
        this.maxLevel = levelType.maxLevel;
        this.levels = levelType.levels;

        this.updateDescription();
    }

    public ExItemStack getDisplayItem() {
        return displayItem;
    }

    public String getName() {
        return name;
    }

    public Collection<L> getLevels() {
        return this.levels.values();
    }

    public L getFirstLevel() {
        L level = null;

        for (int i = this.baseLevel; level == null && i <= this.maxLevel; i++) {
            level = this.levels.get(i);
        }

        return level;
    }

    public Level<?> getLastLevel() {
        return this.levels.get(this.maxLevel);
    }

    public boolean tryLevelUp(MobDefUser user) {
        if (user == null) {
            return false;
        }

        for (LevelType<?> conflictingLevelType : this.conflictingTypes) {
            if (conflictingLevelType.getLevel() > 0) {
                user.sendPluginMessage(Plugin.MOB_DEFENCE, Component.text("Conflicting with ", ExTextColor.WARNING)
                        .append(Component.text(conflictingLevelType.getName(), ExTextColor.VALUE)));
                return false;
            }
        }

        if (this.level == this.maxLevel) {
            user.sendPluginMessage(Plugin.MOB_DEFENCE, Component.text("Max level reached", ExTextColor.WARNING));
            user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
            return false;
        }

        L nextLevel = this.levels.get(this.level + 1);

        if (nextLevel == null) {
            user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
            return false;
        }

        if (MobDefServer.getWaveNumber() < nextLevel.getUnlockWave()) {
            user.sendPluginMessage(Plugin.MOB_DEFENCE,
                    Component.text("This level is locked until wave ", ExTextColor.WARNING)
                            .append(Component.text(nextLevel.getUnlockWave(), ExTextColor.VALUE)));
            user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
            return false;
        }

        if (!user.containsAtLeast(nextLevel.getPrice().asItem())) {
            user.sendPluginMessage(Plugin.MOB_DEFENCE, Component.text("Not enough money", ExTextColor.WARNING));
            user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
            return false;
        }

        user.removeCertainItemStack(nextLevel.getPrice().asItem());

        boolean successful = this.levelUp(user, nextLevel);

        this.level++;

        user.playNote(Instrument.STICKS, Note.natural(1, Note.Tone.C));

        this.updateDescription();

        return successful;
    }

    protected abstract boolean levelUp(MobDefUser user, L level);

    public void updateDescription() {
        this.displayItem.setAmount(this.level == 0 ? 1 : this.level);

        L nextLevel = this.levels.get(this.level + 1);

        StringBuilder sb = new StringBuilder();
        if (this.conflictingTypes.size() > 0) {
            sb.append("§7Conflicts: §c");
            for (LevelType<?> confType : this.conflictingTypes) {
                sb.append(confType.getName());
                sb.append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
        }

        if (nextLevel == null) {
            this.displayItem.setLore("§fLevel: §2" + this.level, "", "§7Next Level: §cmax level reached", "");
        } else {
            if (this.conflictingTypes.size() > 0) {
                this.displayItem.setLore("§7Level: §2" + this.level, "",
                        "§7Next Level: §9" + nextLevel.getDescription(), "",
                        "§7Price:        §2" + nextLevel.getPrice().toString(), "", sb.toString());
            } else {
                this.displayItem.setLore("§7Level: §2" + this.level, "",
                        "§7Next Level: §9" + nextLevel.getDescription(), "",
                        "§7Price:        §2" + nextLevel.getPrice().toString());
            }

        }
    }

    public List<LevelType<?>> getConflictingTypes() {
        return conflictingTypes;
    }

    public void setConflictingTypes(List<LevelType<?>> conflictingTypes) {
        this.conflictingTypes = conflictingTypes;
    }

    public int getLevel() {
        return level;
    }

    public void resetLevel() {
        this.level = this.baseLevel;
    }

}
