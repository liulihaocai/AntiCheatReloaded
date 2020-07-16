/*
 * AntiCheatReloaded for Bukkit and Spigot.
 * Copyright (c) 2012-2015 AntiCheat Team
 * Copyright (c) 2016-2020 Rammelkast
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.rammelkast.anticheatreloaded.check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.potion.PotionEffectType;

import com.rammelkast.anticheatreloaded.AntiCheatReloaded;
import com.rammelkast.anticheatreloaded.check.CheckResult.Result;
import com.rammelkast.anticheatreloaded.check.combat.KillAuraCheck;
import com.rammelkast.anticheatreloaded.check.combat.VelocityCheck;
import com.rammelkast.anticheatreloaded.check.movement.AimbotCheck;
import com.rammelkast.anticheatreloaded.check.movement.ElytraCheck;
import com.rammelkast.anticheatreloaded.check.movement.FlightCheck;
import com.rammelkast.anticheatreloaded.check.packet.MorePacketsCheck;
import com.rammelkast.anticheatreloaded.config.Configuration;
import com.rammelkast.anticheatreloaded.config.providers.Checks;
import com.rammelkast.anticheatreloaded.config.providers.Lang;
import com.rammelkast.anticheatreloaded.config.providers.Magic;
import com.rammelkast.anticheatreloaded.manage.AntiCheatManager;
import com.rammelkast.anticheatreloaded.util.Distance;
import com.rammelkast.anticheatreloaded.util.User;
import com.rammelkast.anticheatreloaded.util.Utilities;
import com.rammelkast.anticheatreloaded.util.VersionUtil;

public class Backend {
	public Map<UUID, Long> velocitized = new HashMap<UUID, Long>();
	private Map<UUID, Long> levitatingEnd = new HashMap<UUID, Long>();
	private List<UUID> isAscending = new ArrayList<UUID>();
	private Map<UUID, Integer> chatLevel = new HashMap<UUID, Integer>();
	private Map<UUID, Integer> commandLevel = new HashMap<UUID, Integer>();
	private Map<UUID, Integer> nofallViolation = new HashMap<UUID, Integer>();
	private Map<UUID, Integer> fastBreakViolation = new HashMap<UUID, Integer>();
	private Map<UUID, Integer> fastBreaks = new HashMap<UUID, Integer>();
	private Map<UUID, Boolean> blockBreakHolder = new HashMap<UUID, Boolean>();
	private Map<UUID, Long> lastBlockBroken = new HashMap<UUID, Long>();
	private Map<UUID, Integer> fastPlaceViolation = new HashMap<UUID, Integer>();
	private Map<UUID, Long> lastBlockPlaced = new HashMap<UUID, Long>();
	private Map<UUID, Long> lastBlockPlaceTime = new HashMap<UUID, Long>();
	private Map<UUID, Integer> blockPunches = new HashMap<UUID, Integer>();
	private Map<UUID, Integer> projectilesShot = new HashMap<UUID, Integer>();
	private Map<UUID, Integer> velocitytrack = new HashMap<UUID, Integer>();
	private Map<UUID, Long> startEat = new HashMap<UUID, Long>();
	private Map<UUID, Long> lastHeal = new HashMap<UUID, Long>();
	private Map<UUID, Long> projectileTime = new HashMap<UUID, Long>();
	private Map<UUID, Long> bowWindUp = new HashMap<UUID, Long>();
	private Map<UUID, Long> instantBreakExempt = new HashMap<UUID, Long>();
	private Map<UUID, Long> sprinted = new HashMap<UUID, Long>();
	private Map<UUID, Long> brokenBlock = new HashMap<UUID, Long>();
	public Map<UUID, Long> placedBlock = new HashMap<UUID, Long>();
	private Map<UUID, Long> blockTime = new HashMap<UUID, Long>();
	private Map<UUID, Integer> blocksDropped = new HashMap<UUID, Integer>();
	private Map<UUID, Long> lastInventoryTime = new HashMap<UUID, Long>();
	private Map<UUID, Long> inventoryTime = new HashMap<UUID, Long>();
	private Map<UUID, Integer> inventoryClicks = new HashMap<UUID, Integer>();
	private Map<UUID, Material> itemInHand = new HashMap<UUID, Material>();
	private HashSet<Byte> transparent = new HashSet<Byte>();
	private Map<UUID, Long> lastFallPacket = new HashMap<UUID, Long>();
	private Map<UUID, Integer> fastSneakViolations = new HashMap<UUID, Integer>();
	private Map<UUID, Long> lastSneak = new HashMap<UUID, Long>();

	private Magic magic;
	private Checks checksConfig;
	private AntiCheatManager manager = null;
	private Lang lang = null;
	private static final CheckResult PASS = new CheckResult(CheckResult.Result.PASSED);

	public Backend(AntiCheatManager instance) {
		magic = instance.getConfiguration().getMagic();
		checksConfig = instance.getConfiguration().getChecks();
		manager = instance;
		lang = manager.getConfiguration().getLang();
		transparent.add((byte) -1);
	}

	public Magic getMagic() {
		return magic;
	}

	public void updateConfig(Configuration config) {
		magic = config.getMagic();
		lang = config.getLang();
	}

	public void resetChatLevel(User user) {
		chatLevel.put(user.getUUID(), 0);
	}

	public void garbageClean(Player player) {
		UUID uuid = player.getUniqueId();

		blocksDropped.remove(uuid);
		blockTime.remove(uuid);
		brokenBlock.remove(uuid);
		placedBlock.remove(uuid);
		bowWindUp.remove(uuid);
		startEat.remove(uuid);
		lastHeal.remove(uuid);
		sprinted.remove(uuid);
		instantBreakExempt.remove(uuid);
		isAscending.remove(uuid);
		nofallViolation.remove(uuid);
		fastBreakViolation.remove(uuid);
		fastBreaks.remove(uuid);
		blockBreakHolder.remove(uuid);
		lastBlockBroken.remove(uuid);
		fastPlaceViolation.remove(uuid);
		lastBlockPlaced.remove(uuid);
		lastBlockPlaceTime.remove(uuid);
		blockPunches.remove(uuid);
		projectilesShot.remove(uuid);
		velocitized.remove(uuid);
		velocitytrack.remove(uuid);
		startEat.remove(uuid);
		lastHeal.remove(uuid);
		projectileTime.remove(uuid);
		bowWindUp.remove(uuid);
		instantBreakExempt.remove(uuid);
		sprinted.remove(uuid);
		brokenBlock.remove(uuid);
		placedBlock.remove(uuid);
		blockTime.remove(uuid);
		blocksDropped.remove(uuid);
		lastInventoryTime.remove(uuid);
		inventoryTime.remove(uuid);
		inventoryClicks.remove(uuid);
		lastFallPacket.remove(uuid);
		fastSneakViolations.remove(uuid);
		lastSneak.remove(uuid);
		levitatingEnd.remove(uuid);
		AimbotCheck.LAST_DELTA_YAW.remove(uuid);
		VelocityCheck.VIOLATIONS.remove(uuid);
		MorePacketsCheck.LAST_PACKET_TIME.remove(uuid);
		MorePacketsCheck.PACKET_BALANCE.remove(uuid);
		MorePacketsCheck.EXEMPT_TIMINGS.remove(uuid);
		ElytraCheck.JUMP_Y_VALUE.remove(uuid);
		KillAuraCheck.ANGLE_FLAGS.remove(uuid);
		FlightCheck.MOVING_EXEMPT.remove(uuid);
	}

	public CheckResult checkFastBow(Player player, float force) {
		// Ignore magic numbers here, they are minecrafty vanilla stuff.
		if (!bowWindUp.containsKey(player.getUniqueId())) {
			return PASS;
		}
		int ticks = (int) ((((System.currentTimeMillis() - bowWindUp.get(player.getUniqueId())) * 20) / 1000) + 3);
		bowWindUp.remove(player.getUniqueId());
		float f = (float) ticks / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		f = f > 1.0F ? 1.0F : f;
		if (Math.abs(force - f) > magic.BOW_ERROR()) {
			return new CheckResult(CheckResult.Result.FAILED,
					"fired their bow too fast (actual force=" + force + ", calculated force=" + f + ")");
		} else {
			return PASS;
		}
	}

	public CheckResult checkProjectile(Player player) {
		incrementOld(player, projectilesShot, 10);
		if (!projectileTime.containsKey(player.getUniqueId())) {
			projectileTime.put(player.getUniqueId(), System.currentTimeMillis());
			return new CheckResult(CheckResult.Result.PASSED);
		} else if (projectilesShot.get(player.getUniqueId()) == magic.PROJECTILE_CHECK()) {
			long time = System.currentTimeMillis() - projectileTime.get(player.getUniqueId());
			projectileTime.remove(player.getUniqueId());
			projectilesShot.remove(player.getUniqueId());
			if (time < magic.PROJECTILE_TIME_MIN()) {
				return new CheckResult(CheckResult.Result.FAILED, "wound up a bow too fast (actual time=" + time
						+ ", min time=" + magic.PROJECTILE_TIME_MIN() + ")");
			}
		}
		return PASS;
	}

	public CheckResult checkFastDrop(Player player) {
		incrementOld(player, blocksDropped, 10);
		if (!blockTime.containsKey(player.getUniqueId())) {
			blockTime.put(player.getUniqueId(), System.currentTimeMillis());
			return new CheckResult(CheckResult.Result.PASSED);
		} else if (blocksDropped.get(player.getUniqueId()) == magic.DROP_CHECK()) {
			long time = System.currentTimeMillis() - blockTime.get(player.getUniqueId());
			blockTime.remove(player.getUniqueId());
			blocksDropped.remove(player.getUniqueId());
			if (time < magic.DROP_TIME_MIN()) {
				return new CheckResult(CheckResult.Result.FAILED,
						"dropped an item too fast (actual time=" + time + ", min time=" + magic.DROP_TIME_MIN() + ")");
			}
		}
		return PASS;
	}
	
	public CheckResult checkSpider(Player player, double y) {
		if (y <= magic.LADDER_Y_MAX() && y >= magic.LADDER_Y_MIN()
				&& !Utilities.isClimbableBlock(player.getLocation().getBlock())
				&& !Utilities.isClimbableBlock(player.getEyeLocation().getBlock())
				&& !Utilities.isClimbableBlock(player.getLocation().clone().add(0, -0.98, 0).getBlock())) {
			return new CheckResult(CheckResult.Result.FAILED,
					"tried to climb a non-climbable block (" + player.getLocation().getBlock().getType() + ")");
		} else {
			return PASS;
		}
	}

	public CheckResult checkNoFall(Player player, double y) {
		UUID uuid = player.getUniqueId();
		if (player.getGameMode() != GameMode.CREATIVE && !player.isInsideVehicle() && !player.isSleeping()
				&& !isMovingExempt(player) && !justPlaced(player) && !Utilities.isNearWater(player)
				&& !Utilities.isInWeb(player) && !player.getLocation().getBlock().getType().name().endsWith("TRAPDOOR")
				&& !VersionUtil.isSlowFalling(player)) {
			if (player.getFallDistance() == 0) {
				if (nofallViolation.get(uuid) == null) {
					nofallViolation.put(uuid, 1);
				} else {
					nofallViolation.put(uuid, nofallViolation.get(player.getUniqueId()) + 1);
				}

				int i = nofallViolation.get(uuid);
				int vlBeforeFlag = checksConfig.getInteger(CheckType.NOFALL, "vlBeforeFlag");
				if (i >= vlBeforeFlag) {
					nofallViolation.put(player.getUniqueId(), 1);
					return new CheckResult(CheckResult.Result.FAILED, "tried to avoid fall damage (fall distance = 0 "
							+ i + " times in a row, max=" + vlBeforeFlag + ")");
				} else {
					return PASS;
				}
			} else {
				nofallViolation.put(uuid, 0);
				return PASS;
			}
		}
		return PASS;
	}

	public CheckResult checkSneak(Player player, Location location, double x, double z) {
		if (player.isSneaking() && !VersionUtil.isFlying(player) && !isMovingExempt(player) && !player.isInsideVehicle()
				&& !Utilities.cantStandAtExp(location)
				&& !Utilities.isSlab(location.getBlock().getRelative(BlockFace.DOWN))) {
			double xzMaxSneaking = checksConfig.getDouble(CheckType.SNEAK, "xzMaxSneaking");
			double i = x > xzMaxSneaking ? x : z > xzMaxSneaking ? z : -1;
			if (i != -1) {
				if (this.fastSneakViolations.containsKey(player.getUniqueId())) {
					int flags = this.fastSneakViolations.get(player.getUniqueId());
					if (flags >= checksConfig.getInteger(CheckType.SNEAK, "vlBeforeFlag")) {
						this.fastSneakViolations.put(player.getUniqueId(), flags - 1);
						return new CheckResult(CheckResult.Result.FAILED,
								"was sneaking too fast (speed=" + i + ", max=" + xzMaxSneaking + ")");
					}
					this.fastSneakViolations.put(player.getUniqueId(), flags + 1);
					return PASS;
				} else {
					this.fastSneakViolations.put(player.getUniqueId(), 0);
					return PASS;
				}
			} else {
				this.fastSneakViolations.remove(player.getUniqueId());
				return PASS;
			}
		} else {
			this.fastSneakViolations.remove(player.getUniqueId());
			return PASS;
		}
	}

	public CheckResult checkSneakToggle(Player player) {
		User user = AntiCheatReloaded.getManager().getUserManager().getUser(player.getUniqueId());
		final long lastSneak = this.lastSneak.getOrDefault(player.getUniqueId(), (long) 0);
		this.lastSneak.put(player.getUniqueId(), System.currentTimeMillis());
		if (System.currentTimeMillis() - lastSneak < 75
				&& (AntiCheatReloaded.getPlugin().getTPS() > 12 && !user.isLagging())) {
			if (!this.silentMode()) {
				player.teleport(user.getGoodLocation(player.getLocation()));
			}
			return new CheckResult(Result.FAILED,
					"toggled sneak too fast (time=" + (System.currentTimeMillis() - lastSneak) + ")");
		}
		return PASS;
	}

	public CheckResult checkSprintHungry(PlayerToggleSprintEvent event) {
		Player player = event.getPlayer();
		if (event.isSprinting() && player.getGameMode() != GameMode.CREATIVE
				&& player.getFoodLevel() <= magic.SPRINT_FOOD_MIN()) {
			return new CheckResult(CheckResult.Result.FAILED,
					"sprinted while hungry (food=" + player.getFoodLevel() + ", min=" + magic.SPRINT_FOOD_MIN() + ")");
		} else {
			return PASS;
		}
	}

	public CheckResult checkVClip(Player player, Distance distance) {
		double from = Math.round(distance.fromY());
		double to = Math.round(distance.toY());

		if (player.isInsideVehicle() || (from == to || from < to) || Math.round(distance.getYDifference()) < 2) {
			return PASS;
		}

		for (int i = 0; i < (Math.round(distance.getYDifference())) + 1; i++) {
			Block block = new Location(player.getWorld(), player.getLocation().getX(), to + i,
					player.getLocation().getZ()).getBlock();
			if (block.getType() != Material.AIR && block.getType().isSolid()) {
				return new CheckResult(CheckResult.Result.FAILED, "tried to move through a solid block",
						(int) from + 3);
			}
		}

		return PASS;
	}

	public void logAscension(Player player, double y1, double y2) {
		UUID name = player.getUniqueId();
		if (y1 < y2 && !isAscending.contains(name)) {
			isAscending.add(name);
		} else {
			isAscending.remove(name);
		}
	}

	public CheckResult checkSwing(Player player, Block block) {
		UUID uuid = player.getUniqueId();
		if (!isInstantBreakExempt(player)) {
			if (!VersionUtil.getItemInHand(player).containsEnchantment(Enchantment.DIG_SPEED)
					&& !(VersionUtil.getItemInHand(player).getType() == Material.SHEARS
							&& block.getType().name().endsWith("LEAVES"))) {
				if (blockPunches.get(uuid) != null && player.getGameMode() != GameMode.CREATIVE) {
					int i = blockPunches.get(uuid);
					if (i < magic.BLOCK_PUNCH_MIN()) {
						return new CheckResult(CheckResult.Result.FAILED, "tried to break a block of " + block.getType()
								+ " after only " + i + " punches (min=" + magic.BLOCK_PUNCH_MIN() + ")");
					} else {
						blockPunches.put(uuid, 0); // it should reset after EACH block break.
					}
				}
			}
		}
		return PASS;
	}

	public CheckResult checkFastBreak(Player player, Block block) {
		int violations = magic.FASTBREAK_MAXVIOLATIONS();
		long timemax = isInstantBreakExempt(player) ? 0
				: Utilities.calcSurvivalFastBreak(VersionUtil.getItemInHand(player), block.getType());
		if (player.getGameMode() == GameMode.CREATIVE) {
			violations = magic.FASTBREAK_MAXVIOLATIONS_CREATIVE();
			timemax = magic.FASTBREAK_TIMEMAX_CREATIVE();
		}
		UUID uuid = player.getUniqueId();
		if (!fastBreakViolation.containsKey(uuid)) {
			fastBreakViolation.put(uuid, 0);
		} else {
			Long math = System.currentTimeMillis() - lastBlockBroken.get(uuid);
			int i = fastBreakViolation.get(uuid);
			if (i > violations && math < magic.FASTBREAK_MAXVIOLATIONTIME()) {
				lastBlockBroken.put(uuid, System.currentTimeMillis());
				return new CheckResult(CheckResult.Result.FAILED,
						"broke blocks too fast " + i + " times in a row (max=" + violations + ")");
			} else if (fastBreakViolation.get(uuid) > 0 && math > magic.FASTBREAK_MAXVIOLATIONTIME()) {
				fastBreakViolation.put(uuid, 0);
			}
		}
		if (!fastBreaks.containsKey(uuid) || !lastBlockBroken.containsKey(uuid)) {
			if (!lastBlockBroken.containsKey(uuid)) {
				lastBlockBroken.put(uuid, System.currentTimeMillis());
			}
			if (!fastBreaks.containsKey(uuid)) {
				fastBreaks.put(uuid, 0);
			}
		} else {
			Long math = System.currentTimeMillis() - lastBlockBroken.get(uuid);
			if ((math != 0L && timemax != 0L)) {
				if (math < timemax) {
					if (fastBreakViolation.containsKey(uuid) && fastBreakViolation.get(uuid) > 0) {
						fastBreakViolation.put(uuid, fastBreakViolation.get(uuid) + 1);
					} else {
						fastBreaks.put(uuid, fastBreaks.get(uuid) + 1);
					}
					blockBreakHolder.put(uuid, false);
				}
				if (fastBreaks.get(uuid) >= magic.FASTBREAK_LIMIT() && math < timemax) {
					int i = fastBreaks.get(uuid);
					fastBreaks.put(uuid, 0);
					fastBreakViolation.put(uuid, fastBreakViolation.get(uuid) + 1);
					return new CheckResult(CheckResult.Result.FAILED, "tried to break " + i + " blocks in " + math
							+ " ms (max=" + magic.FASTBREAK_LIMIT() + " in " + timemax + " ms)");
				} else if (fastBreaks.get(uuid) >= magic.FASTBREAK_LIMIT() || fastBreakViolation.get(uuid) > 0) {
					if (!blockBreakHolder.containsKey(uuid) || !blockBreakHolder.get(uuid)) {
						blockBreakHolder.put(uuid, true);
					} else {
						fastBreaks.put(uuid, fastBreaks.get(uuid) - 1);
						if (fastBreakViolation.get(uuid) > 0) {
							fastBreakViolation.put(uuid, fastBreakViolation.get(uuid) - 1);
						}
						blockBreakHolder.put(uuid, false);
					}
				}
			}
		}

		lastBlockBroken.put(uuid, System.currentTimeMillis()); // always keep a log going.
		return PASS;
	}

	public CheckResult checkFastPlace(Player player) {
		int violations = player.getGameMode() == GameMode.CREATIVE ? magic.FASTPLACE_MAXVIOLATIONS_CREATIVE()
				: magic.FASTPLACE_MAXVIOLATIONS();
		long time = System.currentTimeMillis();
		UUID uuid = player.getUniqueId();
		if (!lastBlockPlaceTime.containsKey(uuid) || !fastPlaceViolation.containsKey(uuid)) {
			lastBlockPlaceTime.put(uuid, 0L);
			if (!fastPlaceViolation.containsKey(uuid)) {
				fastPlaceViolation.put(uuid, 0);
			}
		} else if (fastPlaceViolation.containsKey(uuid) && fastPlaceViolation.get(uuid) > violations) {
			Long math = System.currentTimeMillis() - lastBlockPlaced.get(uuid);
			if (lastBlockPlaced.get(uuid) > 0 && math < magic.FASTPLACE_MAXVIOLATIONTIME()) {
				lastBlockPlaced.put(uuid, time);
				return new CheckResult(CheckResult.Result.FAILED, "placed blocks too fast "
						+ fastPlaceViolation.get(uuid) + " times in a row (max=" + violations + ")");
			} else if (lastBlockPlaced.get(uuid) > 0 && math > magic.FASTPLACE_MAXVIOLATIONTIME()) {
				fastPlaceViolation.put(uuid, 0);
			}
		} else if (lastBlockPlaced.containsKey(uuid)) {
			long last = lastBlockPlaced.get(uuid);
			long lastTime = lastBlockPlaceTime.get(uuid);
			long thisTime = time - last;
			int minimumTime = checksConfig.getInteger(CheckType.FAST_PLACE, "minimumTime");

			if (lastTime != 0 && thisTime < minimumTime) {
				lastBlockPlaceTime.put(uuid, (time - last));
				lastBlockPlaced.put(uuid, time);
				fastPlaceViolation.put(uuid, fastPlaceViolation.get(uuid) + 1);
				return new CheckResult(CheckResult.Result.FAILED,
						"tried to place a block " + thisTime + " ms after the last one (min=" + minimumTime + " ms)");
			}
			lastBlockPlaceTime.put(uuid, (time - last));
		}
		lastBlockPlaced.put(uuid, time);
		return PASS;
	}

	public void logBowWindUp(Player player) {
		bowWindUp.put(player.getUniqueId(), System.currentTimeMillis());
	}

	public void logEatingStart(Player player) {
		startEat.put(player.getUniqueId(), System.currentTimeMillis());
	}

	public boolean isEating(Player player) {
		return startEat.containsKey(player.getUniqueId()) && startEat.get(player.getUniqueId()) < magic.EAT_TIME_MIN();
	}

	public void logHeal(Player player) {
		lastHeal.put(player.getUniqueId(), System.currentTimeMillis());
	}

	public CheckResult checkChatSpam(Player player, String msg) {
		UUID uuid = player.getUniqueId();
		User user = manager.getUserManager().getUser(uuid);
		if (user.getLastMessageTime() != -1) {
			for (int i = 0; i < 2; i++) {
				String m = user.getMessage(i);
				if (m == null) {
					break;
				}
				Long l = user.getMessageTime(i);

				if (System.currentTimeMillis() - l > magic.CHAT_REPEAT_MIN()) {
					user.clearMessages();
					break;
				} else {
					if (manager.getConfiguration().getConfig().blockChatSpamRepetition.getValue()
							&& m.equalsIgnoreCase(msg) && i == 1) {
						manager.getLoggingManager().logFineInfo(player.getName() + " spam-repeated \"" + msg + "\"");
						return new CheckResult(CheckResult.Result.FAILED, lang.SPAM_WARNING());
					} else if (manager.getConfiguration().getConfig().blockChatSpamSpeed.getValue()
							&& System.currentTimeMillis() - user.getLastCommandTime() < magic.CHAT_MIN()) {
						manager.getLoggingManager().logFineInfo(player.getName() + " spammed quickly \"" + msg + "\"");
						return new CheckResult(CheckResult.Result.FAILED, lang.SPAM_WARNING());
					}
				}
			}
		}
		user.addMessage(msg);
		return PASS;
	}

	public CheckResult checkChatUnicode(Player player, String msg) {
		UUID uuid = player.getUniqueId();
		User user = manager.getUserManager().getUser(uuid);
		for (char ch : msg.toCharArray()) {
			if (Character.UnicodeBlock.of(ch) != Character.UnicodeBlock.BASIC_LATIN
					&& Character.UnicodeBlock.of(ch) != Character.UnicodeBlock.LATIN_1_SUPPLEMENT
					&& Character.UnicodeBlock.of(ch) != Character.UnicodeBlock.LATIN_EXTENDED_A
					&& Character.UnicodeBlock.of(ch) != Character.UnicodeBlock.GENERAL_PUNCTUATION
					&& Character.UnicodeBlock.of(ch) != Character.UnicodeBlock.CYRILLIC
					&& Character.UnicodeBlock.of(ch) != Character.UnicodeBlock.CYRILLIC_EXTENDED_A) {
				return new CheckResult(CheckResult.Result.FAILED, "Unicode chat is not allowed.");
			}
		}
		user.addMessage(msg);
		return PASS;
	}

	public CheckResult checkCommandSpam(Player player, String cmd) {
		UUID uuid = player.getUniqueId();
		User user = manager.getUserManager().getUser(uuid);
		if (user.getLastCommandTime() != -1) {
			for (int i = 0; i < 2; i++) {
				String m = user.getCommand(i);
				if (m == null) {
					break;
				}
				Long l = user.getCommandTime(i);

				if (System.currentTimeMillis() - l > magic.COMMAND_REPEAT_MIN()) {
					user.clearCommands();
					break;
				} else {
					if (manager.getConfiguration().getConfig().blockCommandSpamRepetition.getValue()
							&& m.equalsIgnoreCase(cmd) && i == 1) {
						return new CheckResult(CheckResult.Result.FAILED, lang.SPAM_WARNING());
					} else if (manager.getConfiguration().getConfig().blockCommandSpamSpeed.getValue()
							&& System.currentTimeMillis() - user.getLastCommandTime() < magic.COMMAND_MIN()) {
						return new CheckResult(CheckResult.Result.FAILED, lang.SPAM_WARNING());
					}
				}
			}
		}
		user.addCommand(cmd);
		return PASS;
	}

	public CheckResult checkInventoryClicks(Player player) {
		if (player.getGameMode() == GameMode.CREATIVE) {
			return PASS;
		}
		UUID uuid = player.getUniqueId();
		int clicks = 1;
		if (inventoryClicks.containsKey(uuid)) {
			clicks = inventoryClicks.get(uuid) + 1;
		}
		inventoryClicks.put(uuid, clicks);
		if (clicks == 1) {
			inventoryTime.put(uuid, System.currentTimeMillis());
		} else if (clicks == magic.INVENTORY_CHECK()) {
			long time = System.currentTimeMillis() - inventoryTime.get(uuid);
			inventoryClicks.put(uuid, 0);
			if (time < magic.INVENTORY_TIMEMIN()) {
				return new CheckResult(CheckResult.Result.FAILED, "clicked inventory slots " + clicks + " times in "
						+ time + " ms (max=" + magic.INVENTORY_CHECK() + " in " + magic.INVENTORY_TIMEMIN() + " ms)");
			}
		}
		return PASS;
	}

	public CheckResult checkAutoTool(Player player) {
		if (itemInHand.containsKey(player.getUniqueId())
				&& itemInHand.get(player.getUniqueId()) != VersionUtil.getItemInHand(player).getType()) {
			return new CheckResult(CheckResult.Result.FAILED,
					"switched tools too fast (had " + itemInHand.get(player.getUniqueId()) + ", has "
							+ VersionUtil.getItemInHand(player).getType() + ")");
		} else {
			return PASS;
		}
	}

	public CheckResult checkSprintDamage(Player player) {
		if (isDoing(player, sprinted, magic.SPRINT_MAX())) {
			return new CheckResult(CheckResult.Result.FAILED,
					"sprinted and damaged an entity too fast (min sprint=" + magic.SPRINT_MAX() + " ms)");
		} else {
			return PASS;
		}
	}

	public CheckResult checkFastHeal(Player player) {
		User user = AntiCheatReloaded.getManager().getUserManager().getUser(player.getUniqueId());
		if (lastHeal.containsKey(player.getUniqueId())) // Otherwise it was modified by a plugin, don't worry about it.
		{
			double tps = AntiCheatReloaded.getPlugin().getTPS();
			if (tps < checksConfig.getDouble(CheckType.FAST_HEAL, "minimumTps")
					|| (user.isLagging() && checksConfig.getBoolean(CheckType.FAST_HEAL, "disableForLagging"))) {
				return PASS;
			}
			long minHealTime = checksConfig.getInteger(CheckType.FAST_HEAL, "minHealTime");
			long lastHealTime = lastHeal.get(player.getUniqueId());
			int ping = user.getPing();
			double pingCompensation = checksConfig.getInteger(CheckType.FAST_HEAL, "pingCompensation");
			long allowedHealTime = (long) (minHealTime - (ping * pingCompensation));
			lastHeal.remove(player.getUniqueId());
			if ((System.currentTimeMillis() - lastHealTime) < allowedHealTime) {
				return new CheckResult(CheckResult.Result.FAILED, "healed too quickly (time="
						+ (System.currentTimeMillis() - lastHealTime) + " ms, min=" + allowedHealTime + " ms)");
			}
		}
		return PASS;
	}

	public CheckResult checkFastEat(Player player) {
		User user = AntiCheatReloaded.getManager().getUserManager().getUser(player.getUniqueId());
		if (startEat.containsKey(player.getUniqueId())) // Otherwise it was modified by a plugin, don't worry about it.
		{
			if (AntiCheatReloaded.getPlugin().getTPS() < 17.5 || user.isLagging()) {
				return PASS;
			}
			long l = startEat.get(player.getUniqueId());
			startEat.remove(player.getUniqueId());
			if ((System.currentTimeMillis() - l) < magic.EAT_TIME_MIN()) {
				return new CheckResult(CheckResult.Result.FAILED, "ate too quickly (time="
						+ (System.currentTimeMillis() - l) + " ms, min=" + magic.EAT_TIME_MIN() + " ms)");
			}
		}
		return PASS;
	}

	public void logInstantBreak(final Player player) {
		instantBreakExempt.put(player.getUniqueId(), System.currentTimeMillis());
	}

	public boolean isInstantBreakExempt(Player player) {
		return isDoing(player, instantBreakExempt, magic.INSTANT_BREAK_TIME());
	}

	public void logSprint(final Player player) {
		sprinted.put(player.getUniqueId(), System.currentTimeMillis());
	}

	public void logBlockBreak(final Player player) {
		brokenBlock.put(player.getUniqueId(), System.currentTimeMillis());
	}

	public boolean justBroke(Player player) {
		return isDoing(player, brokenBlock, magic.BLOCK_BREAK_MIN());
	}

	public void logVelocity(final Player player) {
		velocitized.put(player.getUniqueId(), System.currentTimeMillis());
	}

	public void logLevitating(final Player player, final int duration) {
		levitatingEnd.put(player.getUniqueId(), System.currentTimeMillis() + (duration * 1000L));
	}

	public boolean justVelocity(Player player) {
		return (velocitized.containsKey(player.getUniqueId())
				? (System.currentTimeMillis() - velocitized.get(player.getUniqueId())) < magic.VELOCITY_CHECKTIME()
				: false);
	}

	public boolean justLevitated(Player player) {
		return (levitatingEnd.containsKey(player.getUniqueId())
				? (System.currentTimeMillis() - levitatingEnd.get(player.getUniqueId())) < magic.VELOCITY_CHECKTIME()
				: false);
	}

	public boolean extendVelocityTime(final Player player) {
		if (velocitytrack.containsKey(player.getUniqueId())) {
			velocitytrack.put(player.getUniqueId(), velocitytrack.get(player.getUniqueId()) + 1);
			if (velocitytrack.get(player.getUniqueId()) > magic.VELOCITY_MAXTIMES()) {
				velocitized.put(player.getUniqueId(), System.currentTimeMillis() + magic.VELOCITY_PREVENT());
				manager.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(manager.getPlugin(),
						new Runnable() {
							@Override
							public void run() {
								velocitytrack.put(player.getUniqueId(), 0);
							}
						}, magic.VELOCITY_SCHETIME() * 20L);
				return true;
			}
		} else {
			velocitytrack.put(player.getUniqueId(), 0);
		}

		return false;
	}

	public void logBlockPlace(final Player player) {
		placedBlock.put(player.getUniqueId(), System.currentTimeMillis());
	}

	public boolean justPlaced(Player player) {
		return isDoing(player, placedBlock, magic.BLOCK_PLACE_MIN());
	}

	public void logDamage(final Player player, int type) {
		long time;
		switch (type) {
		case 1:
			time = magic.DAMAGE_TIME();
			break;
		case 2:
			time = magic.KNOCKBACK_DAMAGE_TIME();
			break;
		case 3:
			time = magic.EXPLOSION_DAMAGE_TIME();
			break;
		default:
			time = magic.DAMAGE_TIME();
			break;

		}
		FlightCheck.MOVING_EXEMPT.put(player.getUniqueId(), System.currentTimeMillis() + time);
		// Only map in which termination time is calculated beforehand.
	}

	public void logEnterExit(final Player player) {
		FlightCheck.MOVING_EXEMPT.put(player.getUniqueId(), System.currentTimeMillis() + magic.ENTERED_EXITED_TIME());
	}

	public void logTeleport(final Player player) {
		manager.getUserManager().getUser(player.getUniqueId()).getMovementManager().lastTeleport = System
				.currentTimeMillis();
		/* Data for fly/speed should be reset */
		nofallViolation.remove(player.getUniqueId());
		ElytraCheck.JUMP_Y_VALUE.remove(player.getUniqueId());
	}

	public void logExitFly(final Player player) {
		FlightCheck.MOVING_EXEMPT.put(player.getUniqueId(), System.currentTimeMillis() + magic.EXIT_FLY_TIME());
	}

	public void logBoatCollision(final Player player) {
		// TODO config
		FlightCheck.MOVING_EXEMPT.put(player.getUniqueId(), System.currentTimeMillis() + 100 /* 2 ticks */);
	}

	public void logJoin(final Player player) {
		FlightCheck.MOVING_EXEMPT.put(player.getUniqueId(), System.currentTimeMillis() + magic.JOIN_TIME());
		MorePacketsCheck.EXEMPT_TIMINGS.put(player.getUniqueId(), System.currentTimeMillis() + magic.JOIN_TIME());
	}

	public boolean isMovingExempt(Player player) {
		return isDoing(player, FlightCheck.MOVING_EXEMPT, -1);
	}

	public boolean isAscending(Player player) {
		return isAscending.contains(player.getUniqueId());
	}

	public boolean isDoing(Player player, Map<UUID, Long> map, double max) {
		if (map.containsKey(player.getUniqueId())) {
			if (max != -1) {
				if (((System.currentTimeMillis() - map.get(player.getUniqueId())) / 1000) > max) {
					map.remove(player.getUniqueId());
					return false;
				} else {
					return true;
				}
			} else {
				// Termination time has already been calculated
				if (map.get(player.getUniqueId()) < System.currentTimeMillis()) {
					map.remove(player.getUniqueId());
					return false;
				} else {
					return true;
				}
			}
		} else {
			return false;
		}
	}

	public boolean hasJumpPotion(Player player) {
		return player.hasPotionEffect(PotionEffectType.JUMP);
	}

	public boolean hasSpeedPotion(Player player) {
		return player.hasPotionEffect(PotionEffectType.SPEED);
	}

	public void processChatSpammer(Player player) {
		User user = manager.getUserManager().getUser(player.getUniqueId());
		int level = chatLevel.containsKey(user.getUUID()) ? chatLevel.get(user.getUUID()) : 0;
		if (player != null && player.isOnline() && level >= magic.CHAT_ACTION_ONE_LEVEL()) {
			String event = level >= magic.CHAT_ACTION_TWO_LEVEL()
					? manager.getConfiguration().getConfig().chatSpamActionTwo.getValue()
					: manager.getConfiguration().getConfig().chatSpamActionOne.getValue();
			manager.getUserManager().execute(manager.getUserManager().getUser(player.getUniqueId()),
					Utilities.stringToList(event), CheckType.CHAT_SPAM, lang.SPAM_KICK_REASON(),
					Utilities.stringToList(lang.SPAM_WARNING()), lang.SPAM_BAN_REASON());
		}
		chatLevel.put(user.getUUID(), level + 1);
	}

	public void processCommandSpammer(Player player) {
		User user = manager.getUserManager().getUser(player.getUniqueId());
		int level = commandLevel.containsKey(user.getUUID()) ? commandLevel.get(user.getUUID()) : 0;
		if (player != null && player.isOnline() && level >= magic.COMMAND_ACTION_ONE_LEVEL()) {
			String event = level >= magic.COMMAND_ACTION_TWO_LEVEL()
					? manager.getConfiguration().getConfig().commandSpamActionTwo.getValue()
					: manager.getConfiguration().getConfig().commandSpamActionOne.getValue();
			manager.getUserManager().execute(manager.getUserManager().getUser(player.getUniqueId()),
					Utilities.stringToList(event), CheckType.COMMAND_SPAM, lang.SPAM_KICK_REASON(),
					Utilities.stringToList(lang.SPAM_WARNING()), lang.SPAM_BAN_REASON());
		}
		commandLevel.put(user.getUUID(), level + 1);
	}

	public int increment(Player player, Map<UUID, Integer> map, int num) {
		UUID name = player.getUniqueId();
		if (map.get(name) == null) {
			map.put(name, 1);
			return 1;
		} else {
			int amount = map.get(name) + 1;
			if (amount < num + 1) {
				map.put(name, amount);
				return amount;
			} else {
				map.put(name, num);
				return num;
			}
		}
	}

	public int incrementOld(Player player, Map<UUID, Integer> map, int num) {
		UUID uuid = player.getUniqueId();
		if (map.get(uuid) == null) {
			map.put(uuid, 1);
			return 1;
		} else {
			int amount = map.get(uuid) + 1;
			if (amount < num + 1) {
				map.put(uuid, amount);
				return amount;
			} else {
				map.put(uuid, num);
				return num;
			}
		}
	}

	public boolean silentMode() {
		return manager.getConfiguration().getConfig().silentMode.getValue();
	}
}
