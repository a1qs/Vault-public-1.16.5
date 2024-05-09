package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.skill.ability.AbilityGroup;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.awt.event.KeyEvent;

@OnlyIn(Dist.CLIENT)
public class ModKeybinds {

    public static KeyBinding openAbilityTree;
    public static KeyBinding abilityKey;
    public static KeyBinding abilityWheelKey;
    public static Map<String, KeyBinding> abilityQuickfireKey = new HashMap();

    public static void register(final FMLClientSetupEvent event) {
        openAbilityTree = createKeyBinding("open_ability_tree", KeyEvent.VK_H);
        abilityKey = createKeyBinding("ability_key", KeyEvent.VK_G);
        abilityWheelKey = createKeyBinding("ability_wheel_key", 342); // --> Supposed to be R_ALT

        for(AbilityGroup<?> group : ModConfigs.ABILITIES.getAll()) {
            String abilityDescription = group.getParentName().toLowerCase().replace(' ', '_');
            abilityQuickfireKey.put(group.getParentName(), createKeyBinding("quickselect." + abilityDescription, KeyEvent.VK_F13));
        }
    }

    private static KeyBinding createKeyBinding(String name, int key) {
        KeyBinding keyBind = new KeyBinding("key.the_vault." + name, key, "key.category.the_vault");
        ClientRegistry.registerKeyBinding(keyBind);
        return keyBind;

    }
}
