/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.chat;

import de.timesnake.library.basic.util.LogHelper;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Plugin extends de.timesnake.basic.game.util.user.Plugin {

    public static final Plugin MOB_DEFENCE = new Plugin("MobDefence", "GMD", LogHelper.getLogger("MobDefence", Level.INFO));

    protected Plugin(String name, String code, Logger logger) {
        super(name, code, logger);
    }
}
