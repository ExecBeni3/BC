/*
 * Copyright (c) 2018-2020 C4
 *
 * This file is part of Curios, a mod made for Minecraft.
 *
 * Curios is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Curios is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Curios.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.yuanno.block_clover.client.curios;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyRegistry {

  public static KeyBinding openCurios;

  public static void registerKeys() {
    openCurios = registerKeybinding(
        new KeyBinding("key.curios.open.desc", GLFW.GLFW_KEY_G, "key.curios.category"));
  }

  private static KeyBinding registerKeybinding(KeyBinding key) {
    ClientRegistry.registerKeyBinding(key);
    return key;
  }
}
