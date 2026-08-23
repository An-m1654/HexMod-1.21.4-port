package at.petrak.hexcasting.interop.pehkui

import at.petrak.hexcasting.api.HexAPI.modLoc
import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player

object OpGetScale : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getEntity(env.world, 0, argc)
        env.assertEntityInRange(target)
        return (target as Player).getAttribute(Attributes.SCALE)?.getModifier(modLoc("scale"))?.amount()?.plus(1)?.asActionResult ?: 1.asActionResult;
//        return IXplatAbstractions.INSTANCE.pehkuiApi.getScale(target).toDouble().asActionResult
    }
}
