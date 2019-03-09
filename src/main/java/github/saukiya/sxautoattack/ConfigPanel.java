package github.saukiya.sxautoattack;

import com.mumfrey.liteloader.modconfig.AbstractConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;
import github.saukiya.sxautoattack.manager.Manager;
import net.minecraft.client.resources.I18n;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author 格洛
 * @Since 2019/3/5 18:19
 */
public class ConfigPanel extends AbstractConfigPanel {

    private int id = 1;

    private Map<String, ConfigTextField> map = new HashMap<>();

    @Override
    protected void addOptions(ConfigPanelHost host) {
        for (Manager manager : Manager.getList()) {
            manager.addOptions(this);
        }
        id = 1;
    }

    public void addLabel(int x, int y, int width, int height, int colour, String... lines) {
        super.addLabel(id++, x, y, width, height, colour, lines);
    }

    public ConfigTextField addTextField(String key, int x, int y, int width, int height) {
        ConfigTextField configTextField = super.addTextField(id++, x, y, width, height);
        map.put(key, configTextField);
        return configTextField;
    }

    public ConfigTextField getTextField(String key) {
        return map.get(key);
    }

    @Override
    public String getPanelTitle() {
        return I18n.format("settings.title");
    }

    @Override
    public void onPanelHidden() {
        for (Manager manager : Manager.getList()) {
            manager.onPanelHidden(this);
        }
        map.clear();
    }
}
