package com.yuanno.block_clover.client.gui;

import com.yuanno.block_clover.Main;
import com.yuanno.block_clover.api.Beapi;
import com.yuanno.block_clover.api.ability.Ability;
import com.yuanno.block_clover.api.ability.sorts.ContinuousAbility;
import com.yuanno.block_clover.api.ability.sorts.PassiveAbility;
import com.yuanno.block_clover.data.ability.IAbilityData;
import com.yuanno.block_clover.networking.CEquipAbilityPacket;
import com.yuanno.block_clover.networking.CTogglePassiveAbilityPacket;
import com.yuanno.block_clover.networking.PacketHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.ScrollPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class AbilitiesListScreenPanel extends ScrollPanel
{
	private SelectHotbarAbilitiesScreen parent;
	private IAbilityData props;
	private List<Entry> allAbilities = new ArrayList<Entry>();
	private List<Entry> activeAbilities = new ArrayList<Entry>();
	private List<Entry> passiveAbilities = new ArrayList<Entry>();
	private static final int ENTRY_HEIGHT = 20;

	public AbilitiesListScreenPanel(SelectHotbarAbilitiesScreen parent, IAbilityData abilityProps, Ability[] abilities)
	{
		super(parent.getMinecraft(), 215, 130, parent.height / 2 - 98, parent.width / 2 - 109);

		this.parent = parent;
		this.props = abilityProps;

		for (int i = 0; i <= abilities.length - 1; i++)
		{
			boolean hideInGUI = abilities[i].isHideInGUI();
			boolean isPassive = abilities[i] instanceof PassiveAbility;

			if (abilities[i] != null)
			{
				if (!isPassive)
					this.activeAbilities.add(new Entry(abilities[i]));
				else if (isPassive && !hideInGUI)
					this.passiveAbilities.add(new Entry(abilities[i]));
			}
		}
		
		this.allAbilities.addAll(this.activeAbilities);
		
		if(this.passiveAbilities.size() > 0)
			this.passiveAbilities.add(0, null);
		
		this.allAbilities.addAll(this.passiveAbilities);
	}

	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_)
	{
		return true;
	}
	
	@Override
	protected int getContentHeight()
	{
		int size = this.activeAbilities.stream().collect(Collectors.toList()).size();
		if(this.passiveAbilities.size() > 0)
			size += this.passiveAbilities.size();
		return (int) ((size * (ENTRY_HEIGHT * 1.25)) + 2);
	}

	@Override
	protected int getScrollAmount()
	{
		return 15;
	}

	@Override
	protected void drawPanel(MatrixStack matrixStack, int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY)
	{	
		float y = relativeY;
		float x = (this.parent.width / 2 - 109) + 40;
	
		// Render active abilities
		for (Entry entry : this.activeAbilities)
		{
			if(entry == null)
				continue;

			boolean flag = false;

			if (this.props.hasEquippedAbility(entry.ability))
				flag = true;

			ResourceLocation rs = entry.ability.getCore().getRegistryName();
			TranslationTextComponent abilityName = new TranslationTextComponent("ability." + rs.getNamespace() + "." + rs.getPath());
			Minecraft.getInstance().font.drawShadow(matrixStack, abilityName, x, y + 4, flag ? 0xFF0000 : 0xFFFFFF);

			Beapi.drawIcon(entry.ability.getIcon(), MathHelper.floor(x) - 30, MathHelper.floor(y), 1, 16, 16, 1, 1, 1);


			y += ENTRY_HEIGHT * 1.25;
		}
		
		// Render only the passives and tempos abilities
		if(this.passiveAbilities.size() > 0)
		{		
			Minecraft.getInstance().font.drawShadow(matrixStack, "Passives", x - 20, y + 4, 0x8B8B8B);
			
			y += ENTRY_HEIGHT * 1.25;
			
			for (Entry entry : this.passiveAbilities)
			{
				if(entry == null)
					continue;

				Color textColor = Beapi.hexToRGB("#aaff77");
				Color iconColor = Beapi.hexToRGB("#FFFFFF");

				if(entry.ability instanceof PassiveAbility && ((PassiveAbility)entry.ability).isPaused())
					textColor = iconColor = Beapi.hexToRGB("#FF0000");


				ResourceLocation rs = entry.ability.getCore().getRegistryName();
				TranslationTextComponent abilityName = new TranslationTextComponent("ability." + rs.getNamespace() + "." + rs.getPath());
				Minecraft.getInstance().font.drawShadow(matrixStack, abilityName, x, y + 4, textColor.getRGB());

				Beapi.drawIcon(entry.ability.getIcon(), MathHelper.floor(x) - 30, MathHelper.floor(y), 1, 16, 16, iconColor.getRed() / 255.0f, iconColor.getGreen() / 255.0f, iconColor.getBlue() / 255.0f);
				
				y += ENTRY_HEIGHT * 1.25;
			}
		}
	}

	private Entry findAbilityEntry(final int mouseX, final int mouseY)
	{
		double offset = (mouseY - this.top) + this.scrollDistance;

		if (offset <= 0)
			return null;

		int lineIdx = (int) (offset / (ENTRY_HEIGHT * 1.25));
		if (lineIdx >= this.allAbilities.size())
			return null;

		Entry entry = this.allAbilities.get(lineIdx);
		if (entry != null)
		{
			return entry;
		}

		return null;
	}

	@Override
	public boolean mouseClicked(final double mouseX, final double mouseY, final int button)
	{
		Entry entry = this.findAbilityEntry((int) mouseX, (int) mouseY);

		boolean flag = true;
		
		if(entry != null)
		{
			if(entry.ability instanceof PassiveAbility)
			{
				PassiveAbility passive = ((PassiveAbility)entry.ability);
				passive.setPause(!passive.isPaused());
				PacketHandler.sendToServer(new CTogglePassiveAbilityPacket(entry.ability, passive.isPaused()));
			}
			else
			{
				Ability ability = this.props.getEquippedAbility(entry.ability);
				if(ability != null && !ability.isOnStandby())
					flag = false;
				
				if (this.isMouseOver(mouseX, mouseY) && this.parent.slotSelected >= 0 && flag && entry.ability != null)
				{
					this.props.setEquippedAbility(this.parent.slotSelected, entry.ability);
					PacketHandler.sendToServer(new CEquipAbilityPacket(this.parent.slotSelected, entry.ability));
					this.parent.markDirty();
				}
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}
	
    @Override
	public boolean isMouseOver(double mouseX, double mouseY)
    {
        return mouseX >= this.left && mouseY >= this.top && mouseX < this.left + this.width - 5 && mouseY < this.top + this.height;
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
    	super.render(matrixStack, mouseX, mouseY, partialTicks);
    	
		if (true && this.isMouseOver(mouseX, mouseY))
		{
			Entry hoveredEntity = this.findAbilityEntry(mouseX, mouseY);
			
			if(hoveredEntity == null || hoveredEntity.ability.getDescription() == null)
				return;

			double posX = this.parent.width - 220;
			double width = posX + 220;
			int fgColor = 16777215;
			int bgColor1 = Beapi.hexToRGB("#222222FF").getRGB();
			int bgColor2 = Beapi.hexToRGB("#686868EE").getRGB();


			double cooldown = hoveredEntity.ability.getMaxCooldown() / 20;
			double posYCooldown = this.parent.height - 15;
			double heightCooldown = posYCooldown + 120;
			this.fillGradient(matrixStack, (int) posX, (int) posYCooldown, (int) width, (int) heightCooldown, bgColor1, bgColor2);
			List<IReorderingProcessor> stringsCooldown = this.parent.getMinecraft().font.split(new StringTextComponent("Cooldown: " + cooldown), 210);

			float manaCost = hoveredEntity.ability.getmanaCost();
			double posYMana = this.parent.height - 30;
			double heightMana = posYMana + 120;
			this.fillGradient(matrixStack, (int) posX, (int) posYMana, (int) width, (int) heightMana, bgColor1, bgColor2);
			List<IReorderingProcessor> stringsMana = this.parent.getMinecraft().font.split(new StringTextComponent("Mana cost: " + manaCost), 210);

			String tooltip = hoveredEntity.ability.getDescription().getString();
			double posY = this.parent.height - 120;
			double height = posY + 120;
			this.fillGradient(matrixStack, (int) posX, (int) posY, (int) width, (int) height, bgColor1, bgColor2);
			List<IReorderingProcessor> strings = this.parent.getMinecraft().font.split(new StringTextComponent(tooltip), 210);
			for (int b = 0; b < strings.size(); b++)
				Beapi.drawStringWithBorder(this.parent.getMinecraft().font, matrixStack, strings.get(b), (int) posX + 5, 5 + (int) posY + 10 * b, fgColor);
				//Beapi.drawStringWithBorder(this.parent.getMinecraft().font, matrixStack, stringsMana.get(b), (int) posX + 5, 5 + (int) posYMana + 10 * b, fgColor);
				//Beapi.drawStringWithBorder(this.parent.getMinecraft().font, matrixStack, stringsCooldown.get(b), (int) posX + 5, 5 + (int) posYCooldown + 10 * b, fgColor);
			for (int b = 0; b < stringsCooldown.size(); b++)
				Beapi.drawStringWithBorder(this.parent.getMinecraft().font, matrixStack, stringsCooldown.get(b), (int) posX + 5, 5 + (int) posYCooldown + 10 * b, fgColor);
			for (int b = 0; b <stringsMana.size(); b++)
				Beapi.drawStringWithBorder(this.parent.getMinecraft().font, matrixStack, stringsMana.get(b), (int) posX + 5, 5 + (int) posYMana + 10 * b, fgColor);
		}
    }

	class Entry
	{
		private Ability ability;

		public Entry(Ability ability)
		{
			this.ability = ability;
		}
	}
}
