package org.zeith.squarry.inventory.slots;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.zeith.squarry.init.ItemsSQ;

public class SlotGhost
		extends Slot
{
	public final Runnable save;

	public SlotGhost(Container p_40223_, int p_40224_, int p_40225_, int p_40226_, Runnable save)
	{
		super(p_40223_, p_40224_, p_40225_, p_40226_);
		this.save = save;
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
//		if(stack.getItem() == ItemsSQ.UPGRADE_FILTER)
//			return false;

		ItemStack ghost = stack.copy();
		ghost.setCount(1);
		container.setItem(getSlotIndex(), ghost);

		if(save != null)
			save.run();

		return false;
	}

	@Override
	public boolean mayPickup(Player player)
	{
		container.setItem(getSlotIndex(), ItemStack.EMPTY);

		if(save != null)
			save.run();

		return false;
	}
}