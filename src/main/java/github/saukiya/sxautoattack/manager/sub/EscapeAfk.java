package github.saukiya.sxautoattack.manager.sub;

import github.saukiya.sxautoattack.ConfigPanel;
import github.saukiya.sxautoattack.manager.Manager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.MoverType;

/**
 * @Author 格洛
 * @Since 2019/3/7 14:36
 */
public class EscapeAfk implements Manager {

    private int tick1 = 1;
    private int tick2 = 1;

    private boolean afkMove = false;
    private boolean afkTurn = false;

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public void process(Minecraft minecraft, EntityPlayerSP player, boolean clock) {
        if (clock) {
            if (mod.getEscapeAfkTick() > 0 && tick1++ >= mod.getEscapeAfkTick() && !afkMove) {
                tick1 = 1;
                afkMove = true;
            }
            if (afkMove) {
                if (tick2++ >= mod.getAfkForSize()) {
                    tick2 = 1;
                    afkMove = false;
                    afkTurn = !afkTurn;
                }

                if (afkTurn) {
                    player.move(MoverType.PLAYER, mod.getAfkX(),0, mod.getAfkZ());
                } else {
                    player.move(MoverType.PLAYER, -mod.getAfkX(), 0, -mod.getAfkZ());
                }
            }
        }
    }

    @Override
    public void addOptions(ConfigPanel config) {
        config.addLabel(0,40,200,15, 0x00FFFF, I18n.format("settings.escapeAfkTick"));
        config.addLabel(0,80,60,15, 0x00FFFF, "Afk-ForSize");
        config.addLabel(80,80,40,15, 0x00FFFF, "Afk-X");
        config.addLabel(140,80,60,15, 0x00FFFF, "Afk-Z");

        config.addTextField("EscapeAfkTick", 0, 55, 200, 20)
                .setRegex("^[0-9]*$", false)
                .setMaxLength(5)
                .setText(String.valueOf(mod.getEscapeAfkTick()));
        config.addTextField("AfkForSize", 0, 95, 60, 20)
                .setRegex("^[0-9]*$", false)
                .setMaxLength(5)
                .setText(String.valueOf(mod.getAfkForSize()));
        config.addTextField("AfkX", 80, 95, 40, 20)
                .setRegex("^[0-9.]*$", false)
                .setMaxLength(5)
                .setText(String.valueOf(mod.getAfkX()));
        config.addTextField("AfkZ", 140, 95, 60, 20)
                .setRegex("^[0-9.]*$", false)
                .setMaxLength(5)
                .setText(String.valueOf(mod.getAfkZ()));

    }

    @Override
    public void onPanelHidden(ConfigPanel config) {
        if (config.getTextField("EscapeAfkTick").getText().length() > 0)
            mod.setEscapeAfkTick(Integer.valueOf(config.getTextField("EscapeAfkTick").getText()));
        if (config.getTextField("AfkSize").getText().length() > 0)
            mod.setAfkForSize(Integer.valueOf(config.getTextField("AfkSize").getText()));
        if (config.getTextField("AfkX").getText().length() > 0)
            mod.setAfkX(Double.valueOf(config.getTextField("AfkX").getText()) % 1);
        if (config.getTextField("AfkZ").getText().length() > 0)
            mod.setAfkZ(Double.valueOf(config.getTextField("AfkZ").getText()) % 1);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        tick1 = 1;
        tick2 = 1;
    }
}
