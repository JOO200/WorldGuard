/*
 * WorldGuard, a suite of tools for Minecraft
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldGuard team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.worldguard.session.handler;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.commands.CommandUtils;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;

import java.util.Set;

public class EntryFlag extends Handler {

    public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<EntryFlag> {
        @Override
        public EntryFlag create(Session session) {
            return new EntryFlag(session);
        }
    }

    private static final long MESSAGE_THRESHOLD = 1000 * 2;
    private long lastMessage;

    public EntryFlag(Session session) {
        super(session);
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        boolean allowed = toSet.testState(player, Flags.ENTRY);

        if (!getSession().getManager().hasBypass(player, (World) to.getExtent()) && !allowed && moveType.isCancellable()) {
            String message = toSet.queryValue(player, Flags.ENTRY_DENY_MESSAGE);
            long now = System.currentTimeMillis();

            if ((now - lastMessage) > MESSAGE_THRESHOLD && message != null && !message.isEmpty()) {
                player.printRaw(CommandUtils.replaceColorMacros(message));
                lastMessage = now;
            }

            return false;
        } else {
            return true;
        }
    }


}
