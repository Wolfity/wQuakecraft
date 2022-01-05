# wQuakecraft
a Quakecraft minigame, step in the arena with different quake guns and battle to end up being the 
player with the most kills. Includes powerups.

### **How to set it up**
Setting up quakecraft is pretty simple. 
To get started, create a hub. You can do that by using the [/quake sethub] command. 

### **How to create arenas**
Now that you created the hub, you can get started creating arenas. 
You need to execute the command: [/quake createarena <name>]. This will create an arena.
You can configure per arena settings in the config.

Now that the arena is created, you will have to set player spawns. 
You can do that by executing the command: [/quake addspawn <arena>]. 
Make sure you have as many spawns as the player limit of your map. 
The last thing to do is create power-up spawn locations. 
These can be created using the [/quake addpowerupspawn <arena>] command.

### **Power Ups**
What are Power-Ups? Power-Ups are temporary effects that will give you an advantage over other 
players. Currently, there are two power-ups. There is Speed and Machinegun. 
Speed gives you a speed effect, allowing you to run around faster and be a more challenging target.
The Machine Gun effect will allow you to bypass the shooting cooldown. 

### **Rail Guns**
Rail Guns are entirely configurable. You can choose the name, 
the material, and the fire rate for the gun.

### **Admin Commands**
Requires the permission "quake.admin"\
``/quake createarena <name>`` - Creates a new arena\
``/quake removearena <name>`` - Removes an arena\
``/quake addspawn <arena>`` - Adds a player spawn to the arena\
``/quake addpowerupspawn <arena>`` - Adds a Power-Up spawn to the arena\
``/quake admin`` - Displays the admin help command

### **Player Commands**
``/quake joinarena <arena>`` - Joins the specified arena if possible\
``/quake joinany`` - Joins any available arena\
``/quake leavegame`` - Leave your current game\
``/quake arenas`` - Show a list of all available arenas

