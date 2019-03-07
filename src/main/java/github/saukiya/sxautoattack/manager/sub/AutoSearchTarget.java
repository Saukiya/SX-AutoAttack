package github.saukiya.sxautoattack.manager.sub;

import github.saukiya.sxautoattack.ConfigPanel;
import github.saukiya.sxautoattack.manager.Manager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author 格洛
 * @Since 2019/3/7 16:04
 */
public class AutoSearchTarget implements Manager {

    private int tick = 1;

    private Map<Class<? extends Entity>, List<Entity>> targetMap = new HashMap<>();

    private Entity target = null;

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public void process(Minecraft minecraft, EntityPlayerSP player, boolean clock) {

        if (clock && mod.getTargetRange() > 0 && mod.getUpdateTargetTick() > 0) {
            World world = player.getEntityWorld();
            // 搜索目标
            if (tick++ >= mod.getUpdateTargetTick()) {
                tick = 1;
                if (searchTarget(player, world, EntityLiving.class).size() == 0) {
                    Manager.sendMessage(player, MessageFormat.format(I18n.format("messages.noSearchTarget"), EntityLiving.class.getSimpleName(), df.format(player.posX), df.format(player.posY), df.format(player.posZ)));
                    if (searchTarget(player, world, EntityItem.class).size() == 0) {
                        Manager.sendMessage(player, MessageFormat.format(I18n.format("messages.noSearchTarget"), EntityItem.class.getSimpleName(), df.format(player.posX), df.format(player.posY), df.format(player.posZ)));
                    }
                }
            } else {
                if (targetMap.getOrDefault(EntityLiving.class, new ArrayList<>()).size() == 0) {
                    tick = 1;
                    searchTarget(player, world, EntityLiving.class);
                } else if (targetMap.getOrDefault(EntityItem.class, new ArrayList<>()).size() == 0) {
                    searchTarget(player, world, EntityItem.class);
                }
            }

            // TODO 处理
            List<Entity> entityList = targetMap.get(EntityLiving.class);
            List<Entity> dropsList = targetMap.get(EntityItem.class);
            if (entityList.size() > 0) {
                EntityLiving entityLiving = null;
                for (Entity entity : entityList) {
                    entity.setGlowing(true);
                }
                for (int i = entityList.size() - 1; i >= 0; i--) {
                    entityLiving = (EntityLiving) entityList.get(i);
                    if (!entityLiving.isDead && isRange(player, entityLiving)) {
                        break;
                    }
                    entityLiving = null;
                    entityList.remove(i);
                }
                if (entityLiving != null) {
                    entityLiving.setGlowing(true);
                }
            } else if (dropsList.size() > 0){
                EntityItem entityItem = (EntityItem) dropsList.get(0);
                entityItem.setGlowing(true);
            }
        }
    }

    @Override
    public void addOptions(ConfigPanel config) {
        config.addLabel(0,120,90,15, 0x00FFFF, I18n.format("settings.updateTargetTick"));
        config.addLabel(110,120,90,15, 0x00FFFF, I18n.format("settings.targetRange"));

        config.addTextField("UpdateTargetTick", 0, 135, 90, 20)
                .setRegex("^[0-9]*$", false)
                .setMaxLength(5)
                .setText(String.valueOf(mod.getUpdateTargetTick()));
        config.addTextField("TargetRange", 110, 135, 90, 20)
                .setRegex("^[0-9.]*$", false)
                .setMaxLength(5)
                .setText(String.valueOf(mod.getTargetRange()));

    }

    @Override
    public void onPanelHidden(ConfigPanel config) {
        if (config.getTextField("UpdateTargetTick").getText().length() > 0)
            mod.setUpdateTargetTick(Integer.valueOf(config.getTextField("UpdateTargetTick").getText()));
        if (config.getTextField("TargetRange").getText().length() > 0)
            mod.setTargetRange(Double.valueOf(config.getTextField("TargetRange").getText()));
    }

    @Override
    public void onEnable() {
        tick = mod.getUpdateTargetTick();
    }

    @Override
    public void onDisable() {
        for (Entity entity : targetMap.getOrDefault(EntityLiving .class, new ArrayList<>())) {
            entity.setGlowing(false);
        }
        targetMap.clear();
    }

    public List<Entity> searchTarget(EntityPlayerSP player, World world, Class zlass) {
        List<Entity> entityList = targetMap.computeIfAbsent(zlass, k -> new ArrayList<>());
        entityList.clear();

        for (Entity entity : world.getLoadedEntityList()) {
            // 判断是否为"活"的实体 并且在搜寻范围内 (死实体例如箭 - 掉落物
            if (zlass.isInstance(entity) && isRange(player, entity)) {
                // 增加到临时储存区
                entityList.add(entity);
            }
        }
        if (entityList.size() > 0) {
            Manager.sendMessage(player, MessageFormat.format(I18n.format("messages.searchTarget"), entityList.size(), zlass.getSimpleName(), df.format(player.posX), df.format(player.posY), df.format(player.posZ)));
        }
        return entityList;
    }

    public boolean isRange(Entity entity1, Entity entity2) {
        return Math.abs(entity1.posX - entity2.posX) <= mod.getTargetRange() && Math.abs(entity1.posY - entity2.posY) <= mod.getTargetRange() && Math.abs(entity1.posZ - entity2.posZ) <= mod.getTargetRange();
    }
}
