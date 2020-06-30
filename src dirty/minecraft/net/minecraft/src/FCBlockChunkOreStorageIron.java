package net.minecraft.src;

public class FCBlockChunkOreStorageIron extends FCBlockChunkOreStorage
{
    protected FCBlockChunkOreStorageIron(int var1)
    {
        super(var1);
        this.setUnlocalizedName("fcBlockChunkOreStorageIron");
    }

    public boolean DropComponentItemsOnBadBreak(World var1, int var2, int var3, int var4, int var5, float var6)
    {
        this.DropItemsIndividualy(var1, var2, var3, var4, FCBetterThanWolves.fcItemChunkIronOre.itemID, 9, 0, var6);
        return true;
    }

    public int GetItemIndexDroppedWhenCookedByKiln(IBlockAccess var1, int var2, int var3, int var4)
    {
        return Item.ingotIron.itemID;
    }
}