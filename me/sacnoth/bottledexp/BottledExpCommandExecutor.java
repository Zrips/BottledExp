package me.sacnoth.bottledexp;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BottledExpCommandExecutor implements CommandExecutor {

	public BottledExpCommandExecutor(BottledExp plugin) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if ((sender instanceof Player)) {
			Player player = (Player) sender;

			if (cmd.getName().equalsIgnoreCase("bottle") && BottledExp.checkPermission("bottle.use", player)) {
				int currentxp = Calculations.getPlayerExperience(player);				
				if (args.length == 0) {
					// statistics for player
					String sentence = BottledExp.langCurrentXP.replace("{xp}", String.valueOf(Calculations.getPlayerExperience(player)));
					sentence = sentence.replace("{level}", String.valueOf(player.getLevel()));

					String sentence2 = BottledExp.langCurrentXP2.replace("{xpdelta}", String.valueOf(Calculations.currentlevelxpdelta(player)));
					sentence2 = sentence2.replace("{bottles}", String.valueOf(Calculations.xptobottles(Calculations.currentlevelxpdelta(player))));

					player.sendMessage(ChatColor.GREEN + sentence);
					player.sendMessage(ChatColor.GREEN + sentence2);

					if (BottledExp.useBottleMoney) {
						sentence = BottledExp.langBottlecost.replace("{money}", String.valueOf(BottledExp.bottleCost * Calculations.currentlevelxpdelta(player)));
						player.sendMessage(ChatColor.GREEN + sentence);
					}
				} else if (args.length == 2) { // Until argument output
					if (args[0].equals("until")) {

						try {
							Integer.parseInt(args[1]);
							if (Integer.parseInt(args[1]) <= player.getLevel()) {
								sender.sendMessage(ChatColor.RED + BottledExp.langMorethan);
							} else {
								int NeedXpToLevel = Calculations.levelToExp(Integer.parseInt(args[1])) - Calculations.getPlayerExperience(player);

								String sentence = BottledExp.langUntil.replace("{xp}", String.valueOf(NeedXpToLevel));
								sentence = sentence.replace("{bottles}", String.valueOf(Calculations.xptobottles(NeedXpToLevel)));
								sentence = sentence.replace("{level}", String.valueOf(args[1]));
								sender.sendMessage(ChatColor.GREEN + sentence);

								if (BottledExp.useBottleMoney) {
									sentence = BottledExp.langBottlecost.replace("{money}", String.valueOf(BottledExp.bottleCost * Calculations.xptobottles(NeedXpToLevel)));
									sender.sendMessage(ChatColor.GREEN + sentence);
								}

							}
							return true;
						} catch (NumberFormatException nfe) {
							sender.sendMessage(ChatColor.RED + BottledExp.langPlzuse);
						}
					} else {
						return true;
					}
				} else if (args.length == 3) { // give argument output
					if (args[0].equals("give")) {
						if (!BottledExp.UseGiveCommand) {
							sender.sendMessage(ChatColor.RED + BottledExp.langGiveDisabled);
						} else {
							if (BottledExp.checkPermission("bottle.give", player)) {
								if (Bukkit.getPlayerExact(args[1]) != null) {
									Player receiver = Bukkit.getPlayerExact(args[1]);
									if (player == receiver) {
										sender.sendMessage(ChatColor.RED + BottledExp.langyourSelf);
									} else {
										try {
											Integer.parseInt(args[2]);
											if (Integer.parseInt(args[2]) > Calculations.getPlayerExperience(player)) {
												sender.sendMessage(ChatColor.RED + BottledExp.langnotEnough);
											} else {
												if (Integer.parseInt(args[2]) <= 0) {
													sender.sendMessage(ChatColor.RED + BottledExp.langpositive);
												} else {
													int giverExp = Calculations.getPlayerExperience(player) - Integer.parseInt(args[2]);
													int expToReceive = ((Integer.parseInt(args[2]) * (100 - BottledExp.LostDurringTransfer)) / 100);
													int lostExp = Integer.parseInt(args[2]) - expToReceive;
													int receiversExp = Calculations.getPlayerExperience(receiver) + expToReceive;

													player.setLevel(0);
													player.setExp(0);
													player.giveExp(giverExp);

													receiver.setLevel(0);
													receiver.setExp(0);
													receiver.giveExp(receiversExp);

													String SenderSentence = BottledExp.langsender.replace("{amount}", String.valueOf(args[2]));
													SenderSentence = SenderSentence.replace("{name}", receiver.getName());
													SenderSentence = SenderSentence.replace("{lost}", String.valueOf(lostExp));
													player.sendMessage(ChatColor.DARK_GREEN + SenderSentence);

													String ReceiverSentence = BottledExp.langreceiver.replace("{amount}", String.valueOf(expToReceive));
													ReceiverSentence = ReceiverSentence.replace("{name}", sender.getName());
													ReceiverSentence = ReceiverSentence.replace("{lost}", String.valueOf(lostExp));
													receiver.sendMessage(ChatColor.DARK_GREEN + ReceiverSentence);
												}
											}
											return true;
										} catch (NumberFormatException nfe) {
											sender.sendMessage(ChatColor.RED + BottledExp.langGivePlzUse);
										}
									}
								} else {
									player.sendMessage(ChatColor.RED + BottledExp.langnotOnline);
								}
							}
						}
					} else {
						return true;
					}
				} else if (args.length == 1) {

					int amount = 0;
					if (args[0].equals("max")) {
						if (BottledExp.checkPermission("bottle.max", player)) {
							amount = (int) Math.floor(currentxp / BottledExp.xpCost);
							if (BottledExp.settingUseItems) {
								amount = Math.min(Calculations.countItems(player, BottledExp.settingConsumedItem) / BottledExp.amountConsumed, amount);
							}
							if (BottledExp.useVaultEcon) {
								amount = Math.min((int) Math.floor(BottledExp.getBalance(player) / BottledExp.moneyCost), amount);
							}
						} else {
							return false;
						}

					} else if (args[0].equals("reload")) {
						if (BottledExp.checkPermission("bottle.reload", player)) {
							BottledExp.config.reload(sender);
							sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
							return true;
						} else {
							return false;
						}

					} else {
						if (BottledExp.checkPermission("bottle.amount", player)) {
							try {
								amount = Integer.valueOf(args[0]).intValue();
							} catch (NumberFormatException nfe) {
								sender.sendMessage(ChatColor.RED + BottledExp.errAmount);
								return false;
							}
						}
					}
					if (currentxp < amount * BottledExp.xpCost) {
						sender.sendMessage(ChatColor.RED + BottledExp.errXP);
						return true;
					} else if (amount <= 0) {
						amount = 0;

						String sentence = BottledExp.langOrder.replace("{bottles}", String.valueOf(amount));
						sender.sendMessage(ChatColor.GREEN + sentence);
						return true;
					}

					boolean money = false;
					if (BottledExp.useVaultEcon) // Check if the player has enough money
					{

						if (BottledExp.getBalance(player) > BottledExp.moneyCost * amount) {
							money = true;
						} else {
							player.sendMessage(BottledExp.errMoney);
							return true;
						}
					}

					boolean consumeItems = false;
					if (BottledExp.settingUseItems) // Check if the player has enough items
					{
						consumeItems = Calculations.checkInventory(player, BottledExp.settingConsumedItem, amount * BottledExp.amountConsumed);
						if (!consumeItems) {
							sender.sendMessage(ChatColor.RED + BottledExp.langItemConsumer);
							return true;
						}
					}

					PlayerInventory inventory = player.getInventory();
					ItemStack items = new ItemStack(384, amount);
					HashMap<Integer, ItemStack> leftoverItems = inventory.addItem(items);
					player.setTotalExperience(0);
					player.setLevel(0);
					player.setExp(0);
					player.giveExp(currentxp - (amount * BottledExp.xpCost));

					if (leftoverItems.containsKey(0)) {
						int refundAmount = leftoverItems.get(0).getAmount();
						player.giveExp(refundAmount * BottledExp.xpCost);

						String sentence = BottledExp.langRefund.replace("{amount}", String.valueOf(refundAmount));
						player.sendMessage(ChatColor.GREEN + sentence);
						amount -= refundAmount;
					}

					if (money) // Remove money from player
					{
						BottledExp.withdrawMoney(player, BottledExp.moneyCost * amount);

						String sentence = BottledExp.langMoney.replace("{cost}", String.valueOf(BottledExp.moneyCost * amount));
						player.sendMessage(ChatColor.GREEN + sentence);
					}

					if (consumeItems) // Remove items from Player
					{
						if (!Calculations.consumeItem(player, BottledExp.settingConsumedItem, amount * BottledExp.amountConsumed)) {
							sender.sendMessage(ChatColor.RED + BottledExp.langItemConsumer);
							return true;
						}
					}

					String sentence = BottledExp.langOrder.replace("{bottles}", String.valueOf(amount));
					sender.sendMessage(ChatColor.GREEN + sentence);
				}
				return true;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You have to be a player!");
			return false;
		}
		return false;
	}
}
