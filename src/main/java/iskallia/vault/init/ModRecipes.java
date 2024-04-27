package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.recipe.RelicSetRecipe;

import iskallia.vault.recipe.UpgradeCrystalRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;

public class ModRecipes {

	public static class Serializer {
		public static SpecialRecipeSerializer<UpgradeCrystalRecipe> CRAFTING_SPECIAL_UPGRADE_CRYSTAL;

		public static void register(RegistryEvent.Register<IRecipeSerializer<?>> event) {
			CRAFTING_SPECIAL_UPGRADE_CRYSTAL = register(event, "crafting_special_upgrade_crystal", new SpecialRecipeSerializer<>(UpgradeCrystalRecipe::new));
		}

		private static <T extends IRecipe<?>> SpecialRecipeSerializer<T> register(RegistryEvent.Register<IRecipeSerializer<?>> event, String name, SpecialRecipeSerializer<T> serializer) {
			serializer.setRegistryName(Vault.id(name));
			event.getRegistry().register(serializer);
			return serializer;
		}
	}

}
