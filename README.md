# InteractiveChat-PacketEvents (WIP)
An addon plugin for InteractiveChat to support PacketEvents instead of ProtocolLib.


## Why use another plugin?
PacketEvents shades Adventure, but doesn't relocate it.\
InteractiveChat shades Adventure **and** relocates it.

This creates several issues when it comes to using Adventure, which IC uses heavily.\
It is possible to do this through Reflection within IC itself, however that impacts maintainability and readability.

In the end, we decided to split PacketEvents support into two parts:
1. Remove the inherent dependency on ProtocolLib.
2. Allow external plugins like this to register their own ProtocolProvider.

You can see the corresponding PR here: https://github.com/LOOHP/InteractiveChat/pull/256
