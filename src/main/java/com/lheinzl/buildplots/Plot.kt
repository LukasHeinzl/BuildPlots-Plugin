package com.lheinzl.buildplots

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.block.Sign
import org.bukkit.block.data.Rotatable
import org.bukkit.block.data.type.Slab
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*
import kotlin.collections.HashMap

object Plots{

    val plots = HashMap<UUID, Plot>()
    var plotSize = 16
    var pathSize = 5
    var rowWidth = 10
    val pathBlock = Material.GRAVEL
    val plotBorderBlock = Material.SMOOTH_STONE_SLAB

}

data class Plot(val x: Int, val z: Int, val playerName: String, val world: World){

    fun createPlot(){
        val startX = x * Plots.plotSize + x * Plots.pathSize
        val startZ = z * Plots.plotSize + z * Plots.pathSize
        val width = Plots.plotSize + Plots.pathSize * 2

        for (xx in 0 until width){
            for(zz in 0 until width){
                if(xx < Plots.pathSize || xx >= width - Plots.pathSize || zz < Plots.pathSize || zz >= width - Plots.pathSize){
                    world.getBlockAt(xx + startX, -1, zz + startZ).type = Plots.pathBlock
                }

                if(xx == Plots.pathSize - 1 || xx == width - Plots.pathSize){
                    if(zz >= Plots.pathSize - 1 && zz <= width - Plots.pathSize){
                        world.getBlockAt(xx + startX, 0, zz + startZ).type = Plots.plotBorderBlock
                    }
                }

                if(zz == Plots.pathSize - 1 || zz == width - Plots.pathSize){
                    if(xx >= Plots.pathSize - 1 && xx <= width - Plots.pathSize){
                        world.getBlockAt(xx + startX, 0, zz + startZ).type = Plots.plotBorderBlock
                    }
                }

                if((xx == width / 2 || xx == width / 2 - 1) && zz == Plots.pathSize - 1){
                    world.getBlockAt(xx + startX, 0, zz + startZ).type = Material.AIR
                }

                if((xx == width / 2 - 2) && zz == Plots.pathSize - 1){
                    val s = Plots.plotBorderBlock.createBlockData() as  Slab
                    s.type = Slab.Type.DOUBLE
                    world.setBlockData(xx + startX, 0, zz + startZ, s)

                    val b = world.getBlockAt(xx + startX, 1, zz + startZ)

                    b.type = Material.OAK_SIGN
                    val rot = (b.blockData as Rotatable)
                    rot.rotation = BlockFace.NORTH
                    world.setBlockData(xx + startX, 1, zz + startZ, rot)

                    val sign = b.state as Sign
                    sign.line(1, Component.text(playerName))
                    sign.update(true)
                }
            }
        }
    }

}

class PlotListener : Listener {

    @EventHandler
    fun onLogin(e: PlayerJoinEvent) {
        var plot = Plots.plots[e.player.uniqueId]

        if(plot == null){
            plot = Plot(Plots.plots.size % Plots.rowWidth, Plots.plots.size / Plots.rowWidth, e.player.name, e.player.world)
            Plots.plots[e.player.uniqueId] = plot
            plot.createPlot()
        }

        val x = (plot.x * Plots.plotSize + (plot.x + 1) * Plots.pathSize + Plots.plotSize / 2) * 1.0
        val z = (plot.z * Plots.plotSize + (plot.z + 1) * Plots.pathSize - Plots.pathSize / 2) * 1.0

        e.player.teleport(Location(e.player.world, x, 1.0, z))
        e.joinMessage(null)
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent){

    }

}