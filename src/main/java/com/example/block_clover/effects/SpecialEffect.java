package com.example.block_clover.effects;

import com.example.block_clover.init.ModRenderTypes;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class SpecialEffect extends Effect
{
	protected SpecialEffect(EffectType typeIn, int liquidColorIn)
	{
		super(typeIn, liquidColorIn);
	}

	public abstract float[] getOverlayColor();

	public abstract boolean hasBodyOverlayColor();
	
	public abstract Block getBlockOverlay();
	
	public abstract boolean isBlockingMovement();
	
	public abstract ResourceLocation getResourceLocation(int duration);

	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType()
	{
		return ModRenderTypes.TRANSPARENT_COLOR;
	}

}
