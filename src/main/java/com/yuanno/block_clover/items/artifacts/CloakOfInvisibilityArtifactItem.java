package com.yuanno.block_clover.items.artifacts;

import com.yuanno.block_clover.api.curios.CuriosApi;
import com.yuanno.block_clover.api.curios.SlotTypePreset;
import com.yuanno.block_clover.api.curios.type.capability.ICurioItem;
import com.yuanno.block_clover.items.ArtifactItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import javax.annotation.Nonnull;

public class CloakOfInvisibilityArtifactItem extends ArtifactItem implements ICurioItem {

    public CloakOfInvisibilityArtifactItem()
    {
        this.artifactInformation = "This cloak seems to come from another place with magicians?";
    }
    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack)
    {
        PlayerEntity player = (PlayerEntity) livingEntity;
        if (!livingEntity.getEntity().level.isClientSide && livingEntity.tickCount % 20 == 0)
        {
            livingEntity.addEffect(new EffectInstance(Effects.INVISIBILITY, 40, 2, true, true));
            stack.hurtAndBreak(1, player,
                    damager -> CuriosApi.getCuriosHelper().onBrokenCurio(SlotTypePreset.RING.getIdentifier(), index, damager));
        }
    }



    @Override
    public boolean isFoil(@Nonnull ItemStack stack) {
        return true;
    }
}
