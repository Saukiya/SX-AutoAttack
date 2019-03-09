package github.saukiya.sxautoattack.manager.sub;

import github.saukiya.sxautoattack.ConfigPanel;
import github.saukiya.sxautoattack.manager.Manager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec2f;

/**
 * @Author 格洛
 * @Since 2019/3/7 16:14
 */
public class AutoSupplement implements Manager {
    @Override
    public int priority() {
        return 5;
    }

    @Override
    public void process(Minecraft minecraft, EntityPlayerSP player, boolean clock) {
        if (clock && player.getFoodStats().needFood() && player.getMaxHealth() - player.getHealth() > 5) {
            //TODO 进食
        }
    }

    @Override
    public void addOptions(ConfigPanel config) {

    }

    @Override
    public void onPanelHidden(ConfigPanel config) {

    }
}
