package com.tuplugin.pds;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob; // Importamos Mob específicamente
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PdsPlugin extends JavaPlugin implements CommandExecutor, Listener {

    @Override
    public void onEnable() {
        // Registrar el executor del comando 'pds'
        this.getCommand("pds").setExecutor(this);
        // Desactivamos el registro de eventos ya que solo tiene comandos.
        // getServer().getPluginManager().registerEvents(this, this); 
        getLogger().info("PdsPlugin habilitado correctamente.");
    }

    @Override
    public void onDisable() {
        getLogger().info("PdsPlugin deshabilitado.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 1. Verificar si el remitente es un jugador
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando solo puede ser usado por un jugador.");
            return true;
        }

        Player player = (Player) sender;

        // 2. Verificar la sintaxis del comando: /pds mods agry
        if (args.length == 2 && args[0].equalsIgnoreCase("mods") && args[1].equalsIgnoreCase("agry")) {
            hacerMobsAgresivos(player);
            player.sendMessage("§aTodos los mobs en todos los mundos ahora te han sido asignados como objetivo. ¡Buena suerte!");
            return true;
        }

        // 3. Mensaje de uso incorrecto (si no coincide con la sintaxis esperada)
        player.sendMessage("§eUso correcto: §b/pds mods agry");
        return true;
    }

    /**
     * Hace que todos los mobs en todos los mundos ataquen al jugador especificado.
     * Se utiliza BukkitRunnable para ejecutar la iteración en el hilo principal de Minecraft.
     * @param player El jugador que será el objetivo.
     */
    private void hacerMobsAgresivos(Player player) {
        // Ejecutar en el hilo principal para interactuar con entidades del mundo (obligatorio en Bukkit)
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        // Verificamos si la entidad es un Mob (cualquier criatura con inteligencia y salud)
                        if (entity instanceof Mob mob) {
                            // Si el mob no es el jugador y no está muerto, establecer al jugador como objetivo.
                            if (!entity.equals(player) && entity.isValid()) {
                                mob.setTarget(player);
                            }
                        }
                    }
                }
            }
        }.runTask(this); // runTask(this) ejecuta la tarea inmediatamente en el siguiente tick del servidor.
    }
}

