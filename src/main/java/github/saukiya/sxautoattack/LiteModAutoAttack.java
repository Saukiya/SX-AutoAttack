package github.saukiya.sxautoattack;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;
import github.saukiya.sxautoattack.manager.Manager;
import github.saukiya.sxautoattack.manager.sub.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.text.MessageFormat;

/**
 * @Author 格洛
 * @Since 2019/3/4 19:46
 */

@ExposableOptions(strategy = ConfigStrategy.Versioned, filename = "sxautoattack.json")
public class LiteModAutoAttack implements Tickable, Configurable{

    // 预设按键
    private static KeyBinding clockKeyBinding = new KeyBinding("key.autoAttack.toggle", Keyboard.KEY_F9, "key.categories.litemods");

    private static LiteModAutoAttack mod;

    public static LiteModAutoAttack getMod() {
        return mod;
    }

    @Override
    public String getVersion() {
        return "0.3.2";
    }

    @Override
    public void init(File configPath) {
        mod = this;
        // 注册按键
        LiteLoader.getInput().registerKeyBinding(LiteModAutoAttack.clockKeyBinding);
        Manager.register(new AutoAttack());
        Manager.register(new AutoSearchTarget());
        Manager.register(new EscapeAfk());
        Manager.register(new DisableBlindness());
        Manager.register(new AutoSupplement());
    }


    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) { }

    @Override
    public String getName() {
        return "SX-AutoAttack";
    }

    @Expose
    @SerializedName("attack_tick")
    private int attackTick = 1;

    @Expose
    @SerializedName("attack_distance")
    private double attackDistance = 4;

    @Expose
    @SerializedName("target_range")
    private double targetRange = 8;

    @Expose
    @SerializedName("black_target")
    private String blackTarget = "Player";

    @Expose
    @SerializedName("escape_afk_tick")
    private int escapeAfkTick = 100;

    @Expose
    @SerializedName("afk_for_size")
    private int afkForSize = 40;

    @Expose
    @SerializedName("afk_x")
    private double afkX = 0;

    @Expose
    @SerializedName("afk_z")
    private double afkZ = 0.05;


    private boolean enable = false;


    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        // 在游戏时被按下切换自动攻击功能
        if (inGame && clockKeyBinding.isPressed()) {
            enable = !enable;
            if (enable) {
                for (Manager manager : Manager.getList()) {
                    manager.onEnable();
                }
                //
//                i1 = getAttackTick();
//                i4 = getUpdateTargetTick();
            } else {
                for (Manager manager : Manager.getList()) {
                    manager.onDisable();
                }
                //
            }
            Manager.sendMessage(minecraft.player, MessageFormat.format(I18n.format("messages.autoAttackToggle"), enable));
        }
        // 不在游戏时关闭自动攻击
        if (!inGame && enable) {
            enable = false;
            for (Manager manager : Manager.getList()) {
                manager.onDisable();
            }
        }

        if (enable) {
            EntityPlayerSP player = minecraft.player;
            if (!inGame) {
                enable = false;
                for (Manager manager : Manager.getList()) {
                    manager.onDisable();
                }
                return;
            }
            for (Manager manager : Manager.getList()) {
                manager.process(minecraft, player, clock);
            }
        }
    }

    @Override
    public Class<? extends com.mumfrey.liteloader.modconfig.ConfigPanel> getConfigPanelClass() {
        return ConfigPanel.class;
    }

    public int getAttackTick() {
        return attackTick;
    }

    public void setAttackTick(int attackTick) {
        this.attackTick = attackTick;
    }

    public int getEscapeAfkTick() {
        return escapeAfkTick;
    }

    public void setEscapeAfkTick(int escapeAfkTick) {
        this.escapeAfkTick = escapeAfkTick;
    }

    public double getAfkX() {
        return afkX;
    }

    public void setAfkX(double afkX) {
        this.afkX = afkX;
    }

    public double getAfkZ() {
        return afkZ;
    }

    public void setAfkZ(double afkZ) {
        this.afkZ = afkZ;
    }

    public int getAfkForSize() {
        return afkForSize;
    }

    public void setAfkForSize(int afkForSize) {
        this.afkForSize = afkForSize;
    }

    public double getAttackDistance() {
        return attackDistance;
    }

    public void setAttackDistance(double attackDistance) {
        this.attackDistance = attackDistance;
    }

    public double getTargetRange() {
        return targetRange;
    }

    public void setTargetRange(double targetRange) {
        this.targetRange = targetRange;
    }

    public String getTargetList() {
        return blackTarget;
    }

    public void setBlackTarget(String blackTarget) {
        this.blackTarget = blackTarget;
    }
}
