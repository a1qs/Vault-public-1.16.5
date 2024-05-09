package iskallia.vault.network.message;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.ability.AbilityGroup;
import iskallia.vault.skill.ability.AbilityNode;
import iskallia.vault.skill.ability.AbilityTree;
import iskallia.vault.skill.ability.type.PlayerAbility;
import iskallia.vault.world.data.PlayerAbilitiesData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import java.util.function.Supplier;


import java.util.function.Supplier;

public class AbilityQuickselectMessage {
    private final String abilityName;

    public AbilityQuickselectMessage(String abilityName) {
        this.abilityName = abilityName;
    }

    public static void encode(AbilityQuickselectMessage pkt, PacketBuffer buffer) {
        buffer.writeUtf(pkt.abilityName);
    }

    public static AbilityQuickselectMessage decode(PacketBuffer buffer) {
        return new AbilityQuickselectMessage(buffer.readUtf(32767));
    }

    public static void handle(AbilityQuickselectMessage pkt, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity sender = context.getSender();

            if (sender == null) return;

            AbilityGroup<?> ability = ModConfigs.ABILITIES.getByName(pkt.abilityName);

            if (ability == null) return;

            PlayerAbilitiesData abilitiesData = PlayerAbilitiesData.get((ServerWorld)sender.level);

            AbilityTree abilityTree = abilitiesData.getAbilities((PlayerEntity)sender);

            AbilityNode<?> abilityNode = abilityTree.getNodeOf(ability);

            if (!abilityNode.isLearned()) return;

            // idk man, try focusedAbilityIndex later
            abilityTree.quickSelectAbility(sender.server, abilityTree.learnedNodes().indexOf(abilityNode));
            if(!abilityNode.equals(abilityTree.getFocusedAbility()) || abilityNode.getAbility().getBehavior() != PlayerAbility.Behavior.RELEASE_TO_PERFORM || abilityTree.cooldowns.getOrDefault(abilityTree.focusedAbilityIndex, 0) > 0) {
                return;
            }
            abilityTree.keyUp(sender.server);
        });
        context.setPacketHandled(true);
    }
}
