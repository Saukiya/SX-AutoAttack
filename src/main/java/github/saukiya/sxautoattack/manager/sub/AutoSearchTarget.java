package github.saukiya.sxautoattack.manager.sub;

import github.saukiya.sxautoattack.ConfigPanel;
import github.saukiya.sxautoattack.manager.Manager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.*;

/**
 * @Author 格洛
 * @Since 2019/3/7 16:04
 */
public class AutoSearchTarget implements Manager {

    private Entity target = null;

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public void process(Minecraft minecraft, EntityPlayerSP player, boolean clock) {
        if (mod.getTargetRange() > 0) {
            World world = player.getEntityWorld();
            // TODO 处理
            if (target != null && !target.isDead) target.setGlowing(false);
            EntityLivingBase entity = (EntityLivingBase) searchTarget(player, world, EntityLivingBase.class);
            if (entity != null && entity.getHealth() > 0) {
                target = entity;
                entity.setGlowing(true);
                lockPerspective(player, target);
            }
        }
    }

    @Override
    public void addOptions(ConfigPanel config) {
        config.addLabel(145,0,55,15, 0x00FFFF, I18n.format("settings.targetRange"));
        config.addLabel(0,40,200,15, 0x00FFFF, I18n.format("settings.blackTarget"));

        config.addTextField("TargetRange", 145, 15, 55, 20)
                .setRegex("^[0-9.]*$", false)
                .setMaxLength(5)
                .setText(String.valueOf(mod.getTargetRange()));

        config.addTextField("BlackTarget", 0, 55, 200, 20)
                .setText(mod.getBlackTarget());

    }

    @Override
    public void onPanelHidden(ConfigPanel config) {
        if (config.getTextField("TargetRange").getText().length() > 0)
            mod.setTargetRange(Double.valueOf(config.getTextField("TargetRange").getText()));

        mod.setBlackTarget(config.getTextField("BlackTarget").getText());
    }

    public void lockPerspective(EntityPlayerSP player, Entity target) {
        double x = target.posX - player.posX;
        double y = (target.posY + target.getEyeHeight()) - (player.posY + player.getEyeHeight());
        double z = target.posZ - player.posZ;
        double hypotenuse = Math.sqrt(Math.pow(x,2) + Math.pow(z,2));

        player.rotationYaw = (float) (-180 * Math.atan2(x,z) / Math.PI);
        player.rotationPitch = (float) (-180 * Math.atan2(y, hypotenuse) / Math.PI);
    }

    @Override
    public void onDisable() {
        if (target != null && !target.isDead) target.setGlowing(false);
    }

    public Entity searchTarget(EntityPlayerSP player, World world, Class zlass) {
        Map<Double, Entity> entityMap = new TreeMap<>();

        for (Entity entity : world.getLoadedEntityList()) {
            double distance = getDistance(player, entity);

            if (mod.getBlackTarget().length() == 0) {
                Manager.sendMessage(player, entity.getClass().getSimpleName());
            }

            if (entity.getUniqueID().equals(player.getUniqueID()) || !mod.getBlackTarget().equals(entity.getClass().getSimpleName())) {
                continue;
            }

            if (zlass.isInstance(entity) && getDistance(player, entity) <= mod.getTargetRange()) {
                // 增加到临时储存区
                entityMap.put(distance, entity);
            }
        }

        return entityMap.size() > 0 ? new ArrayList<>(entityMap.values()).get(0) : null;
    }

    public double getDistance(Entity entity1, Entity entity2) {
        return Math.sqrt(Math.pow(Math.sqrt(Math.pow(entity1.posX - entity2.posX,2) + Math.pow(entity1.posZ - entity2.posZ, 2)), 2) + Math.pow(entity1.posY - entity2.posY, 2));
    }
}
