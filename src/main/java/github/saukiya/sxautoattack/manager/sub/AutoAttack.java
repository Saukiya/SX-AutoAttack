package github.saukiya.sxautoattack.manager.sub;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mumfrey.liteloader.modconfig.AbstractConfigPanel;
import github.saukiya.sxautoattack.ConfigPanel;
import github.saukiya.sxautoattack.manager.Manager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

/**
 * @Author 格洛
 * @Since 2019/3/7 13:09
 */
public class AutoAttack implements Manager {

    private int tick = 1;

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public void process(Minecraft minecraft, EntityPlayerSP player, boolean clock) {
        if (clock && mod.getAttackTick() > 0 && tick++ >= mod.getAttackTick()) {
            tick = 1;
            // 判断鼠标指针是否存在实体
            if (minecraft.objectMouseOver != null && minecraft.objectMouseOver.entityHit != null) {
                Entity entity = minecraft.objectMouseOver.entityHit;
                // 非玩家
                if (!(entity instanceof EntityPlayer)) {
                    double distance = Math.sqrt(Math.pow(Math.abs(player.posX - entity.posX),2) + Math.pow(Math.abs(player.posZ - entity.posZ), 2));// 勾股定理计算距离
                    // 距离小于1（方便捡东西
                    if (distance < mod.getAttackDistance()) {
                        // 挥动手臂
                        player.swingArm(EnumHand.MAIN_HAND);

                        // 攻击
                        minecraft.playerController.attackEntity(player, minecraft.objectMouseOver.entityHit);
                    }
                }
            }
        }
    }

    @Override
    public void addOptions(ConfigPanel config) {
        config.addLabel(0,0,90,15, 0x00FFFF, I18n.format("settings.attackTick"));
        config.addLabel(110,0,90,15, 0x00FFFF, I18n.format("settings.attackDistance"));

        config.addTextField("AttackTick", 0, 15, 90, 20)
                .setRegex("^[0-9]*$", false)
                .setMaxLength(5)
                .setText(String.valueOf(mod.getAttackTick()));

        config.addTextField("AttackDistance", 110, 15, 90, 20)
                .setRegex("^[0-9.]*$", false)
                .setMaxLength(5)
                .setText(String.valueOf(mod.getAttackDistance()));

    }

    @Override
    public void onPanelHidden(ConfigPanel config) {
        if (config.getTextField("AttackTick").getText().length() > 0)
            mod.setAttackTick(Integer.valueOf(config.getTextField("attackTick").getText()));
        if (config.getTextField("AttackDistance").getText().length() > 0)
            mod.setAttackDistance(Double.valueOf(config.getTextField("attackDistance").getText()));
    }

    @Override
    public void onEnable() {
        tick = mod.getAttackTick();
    }

    @Override
    public void onDisable() {
        tick = 1;
    }
}
