# PlayerDoll Refreshed

Server-side fake player (doll) plugin for Paper, continued from [PlayerDoll](https://github.com/sjavi4/PlayerDoll) 2.4.

Dolls are real `ServerPlayer`s spawned in-process (no fake TCP client), driven by a Carpet-style action pack: attack, use, move, look, sneak, sprint, mount, drop, and more. Doll data stays compatible with PlayerDoll 2.x (`plugins/PlayerDoll/doll/*.yml` + vanilla playerdata).

## Supported versions

Paper only, Mojang mappings. Spigot is no longer supported.

| Addon | Minecraft |
|---|---|
| Addon-Doll-1216_1218 | 1.21.6 – 1.21.8 |
| Addon-Doll-1219_12111 | 1.21.9 – 1.21.11 |
| Addon-Doll-261_2612 | 26.1 – 26.1.2 |

Java 21 for 1.21.x servers, Java 25+ for 26.x servers.

## Install

1. Drop `PlayerDoll-Main-<ver>.jar` into `plugins/`
2. Drop the addon jar matching your server version into `plugins/PlayerDoll/addon/`

Upgrading from PlayerDoll 2.x: replace both jars; doll configs and playerdata carry over. `convert-player` and proxy options were removed.

## Build

```
gradlew build collectOutput
```

Jars are collected into `Output/`. Each addon module compiles against its own paperweight dev bundle; supporting a new Minecraft version usually means adding one addon module.

## Commands

`/doll` — create, spawn, despawn, remove, give, info, menu, inv, echest, exp, tp, set/gset/pset, slot, attack, use, jump, drop, lookAt, move, turn, look, sneak, sprint, mount, stop...
`/dollmanage` — same tree, bypasses per-doll permission checks (op).

See the original [wiki](https://github.com/sjavi4/PlayerDoll/wiki) for usage details.
