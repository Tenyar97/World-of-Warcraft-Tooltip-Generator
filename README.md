I've been playing with the idea on making custom classes with custom spells and needed to make custom tooltips for them. I started out downloading icons from wowhead and importing into GIMP, and using the text tool to make changes and the results were..less than ideal.

So obviously the only thing to do was take three days to write a program to mimic the style of the tooltips in game; What I came up with is the following, maybe someone here in the modding section, or, someone stumbling across this from a Google search will find it useful.

You can select from a variety of spell icons (Over 23,000 found from https://github.com/Gethe/wow-ui-textures), input a unique spell name, and specify parameters such as resource cost, type, cast time, cooldown, and rank level. Additionally, the program allows for the setting of range and a comprehensive description of the spell's effects and characteristics. There are also options to denote whether a spell is a talent, class requirements, or level requirement. Whenever finished you can save completed tooltips directly as PNG files.

There are a few settings that will allow setting a default save path, and a limit of 'remembered' icons that can be reselected instead of searching through the entire list again.

