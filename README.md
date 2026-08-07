# BottledExp

[![Issues](https://img.shields.io/github/issues/Zrips/BottledExp?label=issues)](https://github.com/Zrips/BottledExp/issues)
[![Download](https://img.shields.io/badge/download-SpigotMC-orange)](https://www.spigotmc.org/resources/bottledexp.2815/)
[![Website](https://img.shields.io/badge/website-zrips.net-blue)](https://www.zrips.net/)

BottledExp is a Bukkit/Spigot plugin that lets players convert their experience into experience bottles, store custom amounts of XP, and transfer XP to other players.

Originally created by **Sacnoth0** and published on the [Bukkit website](http://dev.bukkit.org/bukkit-plugins/bottledexp/). The plugin was later recoded and expanded by **Zrips**.

## Features

- Convert XP into experience bottles with commands or configurable exchange blocks.
- Store custom amounts of XP in named bottles and consume them later.
- Check XP totals and estimate the bottles needed to reach another level.
- Transfer XP between players with an optional configurable loss.
- Optionally charge items or money for conversions.
- Configure custom bottle recipes and selected XP-related behavior.

## Requirements

- A supported Bukkit/Spigot-compatible Minecraft server; check the [SpigotMC page](https://www.spigotmc.org/resources/bottledexp.2815/) for currently tested versions.
- [CMILib](https://www.spigotmc.org/resources/cmilib.87610/) is required.
- [Vault](https://www.spigotmc.org/resources/vault.34315/) and a Vault-compatible economy plugin are required only when money-related features are enabled.

## Installation

1. Stop the server and make a backup.
2. Download BottledExp from [SpigotMC](https://www.spigotmc.org/resources/bottledexp.2815/).
3. Place `BottledExp.jar` and the required `CMILib` jar in the server's `plugins/` directory.
4. Add Vault and an economy plugin if money-related features will be used.
5. Start the server, then review the generated files under `plugins/BottledExp/`.

## Commands

Run `/bottle` to see the commands available to you.

| Command | Description |
|---|---|
| `/bottle stats [player]` | Show XP statistics for yourself or another online player. |
| `/bottle until <level>` | Estimate the XP and bottles needed to reach a level. |
| `/bottle get <amount\|max> [player]` | Convert XP into regular experience bottles. |
| `/bottle store <xp\|levelsL\|max> [bottle-count]` | Store XP in one or more custom bottles. |
| `/bottle consume [amount\|all] [player]` | Consume held experience bottles directly; defaults to one bottle. |
| `/bottle give <player> <xp>` | Transfer XP to another online player. |
| `/bottle eblock` | Add or remove the targeted custom exchange block. |
| `/bottle reload` | Reload BottledExp configuration files. |

## Permissions

Each subcommand uses `bottledexp.command.<subcommand>`, for example `bottledexp.command.get` or `bottledexp.command.store`. Additional permission checks used by the current source are:

| Permission | Purpose |
|---|---|
| `bottledexp.*` | Top-level group that grants `bottledexp.admin` as declared in `plugin.yml`. |
| `bottledexp.admin` | Group that grants `bottledexp.command.*` as declared in `plugin.yml`. |
| `bottledexp.command.*` | Declared group for `stats`, `stats.others`, `until`, `get`, `get.max`, and `give`. |
| `bottledexp.command.stats.others` | View another player's XP statistics. |
| `bottledexp.command.get.others` | Create bottles for another online player. |
| `bottledexp.command.get.max` | Use `get max`. |
| `bottledexp.command.store.max` | Use `store max`. |
| `bottledexp.command.consume.others` | Consume bottles for another online player. |
| `bottledexp.interact` | Use the configured general exchange block. |
| `bottledexp.custominteract` | Use configured custom exchange blocks. |
| `bottledexp.expcontainer.craft` | Craft configured XP containers. |
| `bottledexp.versioncheck` | Receive update notifications when enabled. |

## Placeholders

The current public source does not register a PlaceholderAPI expansion. Tokens such as `[xp]`, `[level]`, and `[bottles]` in generated locale files are internal message placeholders, not PlaceholderAPI placeholders.

## Building from source

The current repository uses an Eclipse-style `src/` layout and does not include a portable Maven or Gradle build file.

To build it manually, add `src/` as the source directory; compile against the target Spigot API/server, CMILib, Vault API, PermissionsEx, and the legacy server dependencies referenced by the NMS adapters; then package the compiled classes together with `plugin.yml`, `recipes.yml`, and `Translations/` at the jar root. A reproducible one-command build is a useful future improvement.

## Support

- Report reproducible bugs through [GitHub Issues](https://github.com/Zrips/BottledExp/issues).
- Download precompiled releases and check supported versions on [SpigotMC](https://www.spigotmc.org/resources/bottledexp.2815/).
