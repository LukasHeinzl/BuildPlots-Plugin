package com.lheinzl.buildplots

import org.bukkit.plugin.java.JavaPlugin

class Buildplots : JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(PlotListener(), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}