package com.yuanno.block_clover.curios.server.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.yuanno.block_clover.api.curios.CuriosApi;
import com.yuanno.block_clover.api.curios.type.inventory.ICurioStacksHandler;
import net.minecraft.command.arguments.EntityOptions;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CuriosSelectorOptions {
    public static void register() {
        EntityOptions.register("curios", CuriosSelectorOptions::curioArgument,
                entitySelectorParser -> true,
                new TranslationTextComponent("argument.entity.options.curios.description"));
    }

    private static void curioArgument(EntitySelectorParser parser) throws CommandSyntaxException {
        StringReader reader = parser.getReader();
        boolean invert = parser.shouldInvertValue();
        CompoundNBT compoundtag = (new JsonToNBT(reader)).readStruct();
        ListNBT listTag = compoundtag.getList("slot", Constants.NBT.TAG_STRING);
        Set<String> slots = new HashSet<>();

        for (int i = 0; i < listTag.size(); i++) {
            slots.add(listTag.getString(i));
        }
        listTag = compoundtag.getList("index", Constants.NBT.TAG_INT);
        int min = 0;
        int max = -1;

        if (listTag.size() == 2) {
            min = Math.max(0, listTag.getInt(0));
            max = Math.max(min + 1, listTag.getInt(1));
        }
        ItemStack stack =
                compoundtag.contains("item") ? ItemStack.of(compoundtag.getCompound("item")) : null;

        if (stack != null) {
            stack.setCount(Math.max(1, stack.getCount()));
        }
        boolean exclusive = compoundtag.getBoolean("exclusive");
        int finalMin = min;
        int finalMax = max;
        parser.addPredicate(
                entity -> matches(entity, slots, finalMin, finalMax, stack, invert, exclusive));
    }

    private static boolean matches(Entity entity, Set<String> slots, int min, int max,
                                   ItemStack stack,
                                   boolean invert, boolean exclusive) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return CuriosApi.getCuriosHelper().getCuriosHandler(livingEntity).map(handler -> {
                Map<String, ICurioStacksHandler> curios = handler.getCurios();

                if (stack != null) {

                    if (exclusive) {
                        return hasOnlyItem(curios, slots, min, max, stack, invert);
                    } else {
                        return hasItem(curios, slots, min, max, stack, invert);
                    }
                } else if (!slots.isEmpty()) {

                    if (exclusive) {
                        return hasOnlySlot(curios, slots, max, invert);
                    } else {
                        return hasSlot(curios, slots, max, invert);
                    }
                }
                return true;
            }).orElse(false);
        } else {
            return false;
        }
    }

    private static boolean hasOnlySlot(Map<String, ICurioStacksHandler> curios, Set<String> slots,
                                       int max, boolean invert) {

        boolean foundSlot = false;

        if (invert) {

            for (Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {

                if (slots.contains(entry.getKey()) && (max == -1 || entry.getValue().getSlots() >= max)) {
                    foundSlot = true;
                } else if (foundSlot) {
                    return true;
                }
            }
            return false;
        } else {

            for (Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {

                if (slots.contains(entry.getKey()) && (max == -1 || entry.getValue().getSlots() >= max)) {
                    foundSlot = true;
                } else if (foundSlot) {
                    return false;
                }
            }
            return foundSlot;
        }
    }

    private static boolean hasSlot(Map<String, ICurioStacksHandler> curios, Set<String> slots,
                                   int max, boolean invert) {

        for (Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {

            if (slots.contains(entry.getKey()) && (max == -1 || entry.getValue().getSlots() >= max)) {
                return !invert;
            }
        }
        return invert;
    }

    private static boolean hasOnlyItem(Map<String, ICurioStacksHandler> curios, Set<String> slots,
                                       int min, int max, ItemStack stack, boolean invert) {

        boolean foundItem = false;

        if (invert) {

            for (Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {

                if (slots.isEmpty() || slots.contains(entry.getKey())) {
                    ICurioStacksHandler stacks = entry.getValue();
                    int limit = max == -1 ? stacks.getSlots() : Math.min(stacks.getSlots(), max);

                    for (int i = min; i < limit; i++) {
                        ItemStack current = stacks.getStacks().getStackInSlot(i);

                        if (ItemStack.matches(current, stack)) {
                            foundItem = true;
                        } else if (foundItem) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } else {

            for (Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {

                if (slots.isEmpty() || slots.contains(entry.getKey())) {
                    ICurioStacksHandler stacks = entry.getValue();
                    int limit = max == -1 ? stacks.getSlots() : Math.min(stacks.getSlots(), max);

                    for (int i = min; i < limit; i++) {
                        ItemStack current = stacks.getStacks().getStackInSlot(i);

                        if (ItemStack.matches(current, stack)) {
                            foundItem = true;
                        } else if (foundItem) {
                            return false;
                        }
                    }
                }
            }
            return foundItem;
        }
    }

    private static boolean hasItem(Map<String, ICurioStacksHandler> curios, Set<String> slots,
                                   int min, int max, ItemStack stack, boolean invert) {

        for (Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {

            if (slots.isEmpty() || slots.contains(entry.getKey())) {
                ICurioStacksHandler stacks = entry.getValue();
                int limit = max == -1 ? stacks.getSlots() : Math.min(stacks.getSlots(), max);

                for (int i = min; i < limit; i++) {
                    ItemStack current = stacks.getStacks().getStackInSlot(i);

                    if (ItemStack.matches(current, stack)) {
                        return !invert;
                    }
                }
            }
        }
        return invert;
    }
}
