/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.world.ExBlock;

public record ShortPath(ExBlock start, ExBlock end, int heightDelta) {

}
