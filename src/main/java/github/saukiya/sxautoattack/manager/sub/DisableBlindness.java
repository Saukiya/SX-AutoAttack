package github.saukiya.sxautoattack.manager.sub;

import github.saukiya.sxautoattack.ConfigPanel;
import github.saukiya.sxautoattack.manager.Manager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * @Author 格洛
 * @Since 2019/3/7 16:14
 */
public class DisableBlindness implements Manager {
    @Override
    public int priority() {
        return 4;
    }

    @Override
    public void process(Minecraft minecraft, EntityPlayerSP player, boolean clock) {
        PotionEffect effect;
        if (clock && (effect = player.getActivePotionMap().get(Potion.getPotionById(15))) != null && effect.getDuration() > 0) {
            player.getActivePotionMap().remove(Potion.getPotionById(15));
        }
    }

    @Override
    public void addOptions(ConfigPanel config) {

    }

    @Override
    public void onPanelHidden(ConfigPanel config) {

    }
}
