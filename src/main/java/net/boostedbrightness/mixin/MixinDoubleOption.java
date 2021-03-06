package net.boostedbrightness.mixin;

import net.boostedbrightness.BoostedBrightness;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DoubleOption.class)
public class MixinDoubleOption
{
	@Shadow
	@Final
	@Mutable
	private BiFunction<GameOptions, DoubleOption, Text> displayStringGetter;
	@Shadow
	private double max;

	@Inject(at = @At("RETURN"), method = "<init>")
	private void init(String key, double min, double max, float step, Function<GameOptions, Double> getter,
					  BiConsumer<GameOptions, Double> setter, BiFunction<GameOptions, DoubleOption, Text> displayStringGetter,
					  CallbackInfo info)
	{
		// Modifies the max and displayStringGetter of the brightness slider
		if (key.equals("options.gamma"))
		{
			this.max = BoostedBrightness.MAX_BRIGHTNESS;
			this.displayStringGetter = this::displayStringGetter;
		}
	}

	private Text displayStringGetter(GameOptions gameOptions, DoubleOption doubleOption)
	{
		MutableText text = new TranslatableText("options.gamma").append(": ");
		return gameOptions.gamma == 0.0 ?
			   text.append(new TranslatableText("options.gamma.min")) :
			   gameOptions.gamma == 1.0 ?
			   text.append(new TranslatableText("options.gamma.max")) :
			   text.append(Math.round(gameOptions.gamma * 100) + "%");
	}
}