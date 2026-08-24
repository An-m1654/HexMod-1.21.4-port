# NOTE (PLEASE READ THROUGH THIS WHOLE SECTION AND THE IMPORTANT SECTION)
This is an unofficial port of the mod HexCasting (or HexMod, as they call it) made by FallingColors ([link](https://github.com/FallingColors/HexMod))

This is the actual Hex Casting mod that is port to 1.21.4

As I not only ported Hex Casting itself but also the dependencies of Hex Casting, there are a lot more files in the libs folder. All of them are dependencies of Hex Casting which is required to compile the mod.

Source code for all these ports can be found in my account. Notably:
- [Inline](https://github.com/An-m1654/inline-1.21.4-port)
- [Patchouli](https://github.com/An-m1654/Patchouli-1.21.4-port)
- [PAUCAL](https://github.com/An-m1654/PAUCAL-1.21.4-port)

The trinket jar is just a build of [this commit](https://github.com/emilyploszaj/trinkets/commit/49ae1f1002f5ea6cf16481b9803a21175d036a62) of the actual source code of trinket.

Ported dependencies required to actually run Hex Casting (except trinket) can be found in their respective repos listed above. For trinket, just take the binary in the libs folder. For the others, please don't take them from the libs folder as some of them are dev builds and probably will not work in Minecraft.

Also, I have no idea what is json5 so I wrote a script (in scripts folder) that converts them to json.
Also iirc I copied some json files from an original Hex Casting release binary and put it here.

Because I deleted Neoforge, I did something pretty unnecessary: I rewrote the entire block & item model datagen in Fabric.

Because of those changes which breaks Hex Casting's way of doing things, I won't be making a pull request to Hex Casting.

# IMPORTANT

This port only has Fabric in it. Not Neoforge. I don't plan on doing that port myself as I don't have sufficient knowledge of Neoforge. I also don't plan on taking pull requests for Neoforge.

Also I didn't touch the Jenkins file so it most likely doesn't work. I only ported the actual mod, nothing else.

This port of the mod is NOT from the main branch. I used the 1.21 branch as a base instead.

This port of the mod is also NOT from the latest commit of the 1.21 branch. While I was porting this mod, there are quite a number of commits that were made to the original 1.21 branch. Exactly, [this commit](https://github.com/FallingColors/HexMod/commit/e1f841cde10a45176f764062d7696bf7ee0f940a).

# Hex Casting

[Curseforge](https://www.curseforge.com/minecraft/mc-mods/hexcasting) | [Modrinth](https://modrinth.com/mod/hex-casting)
| [Source](https://github.com/gamma-delta/HexMod)

A minecraft mod about casting Hexes, powerful and programmable magical effects, inspired by PSI.

On Forge, this mod requires:

- PAUCAL
- Patchouli
- Kotlin for Forge
- Caelus elytra api

On Fabric, it requires:

- PAUCAL
- Patchouli
- Fabric Language Kotlin
- Cardinal Components
- ClothConfig and ModMenu

[Read the documentation online here!](https://fallingcolors.github.io/HexMod/)

[Discord link](https://discord.gg/4xxHGYteWk)

## The Branches

We are currently developing Hexcasting v0.11.x for 1.20.1, on the `main` branch.

The 0.10.x versions, for 1.19, are in long-term support. We probably won't be adding any new features, but we will try
to fix bugs. Those are on the `1.19` branch.

The 0.9.x versions, for 1.18.2, are in long-term support. We probably won't be adding any new features, but we will try
to fix bugs. Those are on the `1.18` branch.

The `gh-pages` branch is for the online Hex book.

Other branches are old detritus from potential features.

## For Developers

We publish artifacts on Maven at [https://maven.blamejared.com/at/petra-k/hexcasting/]. The modern coordinates are at:

> `hexcasting-[PLATFORM]-[MC VERSION]/[MOD VERSION]`

There are some other folders in the `hexcasting` folder from old CI configurations; ignore those, they're stale.

Please only use things in the `at.petrak.hexcasting.api` package. (We do try to keep the API fairly stable, but we don't
do a very good job.) If you find you need something not in there yell at me on Discord.

## Contributing

Contributions are welcome via pull requests on GitHub. Please [link your PR](https://docs.github.com/en/issues/tracking-your-work-with-issues/using-issues/linking-a-pull-request-to-an-issue) to any issues that it fixes. Note that if your PR makes many unrelated changes, we may ask you to split it up into several smaller PRs to make it simpler to review. Also, feel free to update the `[UNRELEASED]` section in [CHANGELOG.md](./CHANGELOG.md) to document the changes in your PR (in a human-readable and concise manner, not just copying the commit messages).
