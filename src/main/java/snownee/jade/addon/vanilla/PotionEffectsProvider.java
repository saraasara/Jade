package snownee.jade.addon.vanilla;

import java.util.Collection;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ui.IElementHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import snownee.jade.VanillaPlugin;

public class PotionEffectsProvider implements IEntityComponentProvider, IServerDataProvider<Entity> {
	public static final PotionEffectsProvider INSTANCE = new PotionEffectsProvider();

	@Override
	public void append(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
		if (!config.get(VanillaPlugin.EFFECTS) || !accessor.getServerData().contains("Potions")) {
			return;
		}
		IElementHelper helper = tooltip.getElementHelper();
		ITooltip box = helper.tooltip();
		ListNBT list = accessor.getServerData().getList("Potions", Constants.NBT.TAG_COMPOUND);
		ITextComponent[] lines = new ITextComponent[list.size()];
		for (int i = 0; i < lines.length; i++) {
			CompoundNBT compound = list.getCompound(i);
			int duration = compound.getInt("Duration");
			TranslationTextComponent name = new TranslationTextComponent(compound.getString("Name"));
			TranslationTextComponent amplifier = new TranslationTextComponent("potion.potency." + compound.getInt("Amplifier"));
			TranslationTextComponent s = new TranslationTextComponent("jade.potion", name, amplifier, getPotionDurationString(duration));
			box.add(s.mergeStyle(compound.getBoolean("Bad") ? TextFormatting.RED : TextFormatting.GREEN));
		}
		tooltip.add(helper.box(box));
	}

	public static String getPotionDurationString(int duration) {
		if (duration >= 32767) {
			return "**:**";
		} else {
			int i = MathHelper.floor(duration);
			return ticksToElapsedTime(i);
		}
	}

	public static String ticksToElapsedTime(int ticks) {
		int i = ticks / 20;
		int j = i / 60;
		i = i % 60;
		return i < 10 ? j + ":0" + i : j + ":" + i;
	}

	@Override
	public void appendServerData(CompoundNBT tag, ServerPlayerEntity player, World arg2, Entity entity) {
		LivingEntity living = (LivingEntity) entity;
		Collection<EffectInstance> effects = living.getActivePotionEffects();
		if (effects.isEmpty()) {
			return;
		}
		ListNBT list = new ListNBT();
		for (EffectInstance effect : effects) {
			CompoundNBT compound = new CompoundNBT();
			compound.putString("Name", effect.getEffectName());
			compound.putInt("Amplifier", effect.getAmplifier());
			int duration = Math.min(32767, effect.getDuration());
			compound.putInt("Duration", duration);
			compound.putBoolean("Bad", !effect.getPotion().isBeneficial());
			list.add(compound);
		}
		tag.put("Potions", list);
	}
}
