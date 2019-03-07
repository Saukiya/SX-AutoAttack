package github.saukiya.sxautoattack.manager;

import github.saukiya.sxautoattack.ConfigPanel;
import github.saukiya.sxautoattack.LiteModAutoAttack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentString;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author 格洛
 * @Since 2019/3/7 13:05
 */
public interface Manager {

    LiteModAutoAttack mod = LiteModAutoAttack.getMod();

    Map<Integer, Manager> map = new TreeMap<>();

    DecimalFormat df = new DecimalFormat("#.#");

    // 优先级
    int priority();

    // 启动时每tick 处理
    void process(Minecraft minecraft, EntityPlayerSP player, boolean clock);

    // 输入配置选项
    void addOptions(ConfigPanel config);

    // 接收配置信息
    void onPanelHidden(ConfigPanel config);

    // 按键启动方法
    default void onEnable() {}

    // 按键关闭方法
    default void onDisable() {}

    // 列表
    static List<Manager> getList() {
        return new ArrayList<>(map.values());
    }

    // 注册
    static void register(Manager manager){
        map.put(manager.priority(), manager);
    }

    static void sendMessage(EntityPlayerSP player, String message) {
        player.sendMessage(new TextComponentString(message));
    }

}
