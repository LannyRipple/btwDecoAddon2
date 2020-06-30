package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapData extends WorldSavedData
{
    public int xCenter;
    public int zCenter;
    public byte dimension;
    public byte scale;

    /** colours */
    public byte[] colors = new byte[16384];

    /**
     * Holds a reference to the MapInfo of the players who own a copy of the map
     */
    public List playersArrayList = new ArrayList();

    /**
     * Holds a reference to the players who own a copy of the map and a reference to their MapInfo
     */
    private Map playersHashMap = new HashMap();
    public Map playersVisibleOnMap = new LinkedHashMap();

    public MapData(String par1Str)
    {
        super(par1Str);
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.dimension = par1NBTTagCompound.getByte("dimension");
        this.xCenter = par1NBTTagCompound.getInteger("xCenter");
        this.zCenter = par1NBTTagCompound.getInteger("zCenter");
        this.scale = par1NBTTagCompound.getByte("scale");

        if (this.scale < 0)
        {
            this.scale = 0;
        }

        if (this.scale > 4)
        {
            this.scale = 4;
        }

        short var2 = par1NBTTagCompound.getShort("width");
        short var3 = par1NBTTagCompound.getShort("height");

        if (var2 == 128 && var3 == 128)
        {
            this.colors = par1NBTTagCompound.getByteArray("colors");
        }
        else
        {
            byte[] var4 = par1NBTTagCompound.getByteArray("colors");
            this.colors = new byte[16384];
            int var5 = (128 - var2) / 2;
            int var6 = (128 - var3) / 2;

            for (int var7 = 0; var7 < var3; ++var7)
            {
                int var8 = var7 + var6;

                if (var8 >= 0 || var8 < 128)
                {
                    for (int var9 = 0; var9 < var2; ++var9)
                    {
                        int var10 = var9 + var5;

                        if (var10 >= 0 || var10 < 128)
                        {
                            this.colors[var10 + var8 * 128] = var4[var9 + var7 * var2];
                        }
                    }
                }
            }
        }
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setByte("dimension", this.dimension);
        par1NBTTagCompound.setInteger("xCenter", this.xCenter);
        par1NBTTagCompound.setInteger("zCenter", this.zCenter);
        par1NBTTagCompound.setByte("scale", this.scale);
        par1NBTTagCompound.setShort("width", (short)128);
        par1NBTTagCompound.setShort("height", (short)128);
        par1NBTTagCompound.setByteArray("colors", this.colors);
    }

    /**
     * Adds the player passed to the list of visible players and checks to see which players are visible
     */
    public void updateVisiblePlayers(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
        this.playersVisibleOnMap.clear();

        if (par2ItemStack.isOnItemFrame())
        {
            if (this.IsEntityLocationVisibleOnMap(par2ItemStack.getItemFrame()))
            {
                this.func_82567_a(1, par1EntityPlayer.worldObj, "frame-" + par2ItemStack.getItemFrame().entityId, (double)par2ItemStack.getItemFrame().xPosition, (double)par2ItemStack.getItemFrame().zPosition, (double)(par2ItemStack.getItemFrame().hangingDirection * 90 + 180));
            }
        }
        else
        {
            if (!this.playersHashMap.containsKey(par1EntityPlayer))
            {
                MapInfo var3 = new MapInfo(this, par1EntityPlayer);
                this.playersHashMap.put(par1EntityPlayer, var3);
                this.playersArrayList.add(var3);
            }

            if (!par1EntityPlayer.isDead && par1EntityPlayer.inventory.hasItemStack(par2ItemStack) && this.IsEntityLocationVisibleOnMap(par1EntityPlayer) && par1EntityPlayer.dimension == this.dimension)
            {
                this.func_82567_a(0, par1EntityPlayer.worldObj, par1EntityPlayer.getCommandSenderName(), par1EntityPlayer.posX, par1EntityPlayer.posZ, (double)par1EntityPlayer.rotationYaw);
            }
        }
    }

    private void func_82567_a(int par1, World par2World, String par3Str, double par4, double par6, double par8)
    {
        int var10 = 1 << this.scale;
        float var11 = (float)(par4 - (double)this.xCenter) / (float)var10;
        float var12 = (float)(par6 - (double)this.zCenter) / (float)var10;
        byte var13 = (byte)((int)((double)(var11 * 2.0F) + 0.5D));
        byte var14 = (byte)((int)((double)(var12 * 2.0F) + 0.5D));
        byte var15 = 63;
        byte var16;

        if (var11 >= (float)(-var15) && var12 >= (float)(-var15) && var11 <= (float)var15 && var12 <= (float)var15)
        {
            par8 += par8 < 0.0D ? -8.0D : 8.0D;
            var16 = (byte)((int)(par8 * 16.0D / 360.0D));

            if (this.dimension < 0)
            {
                int var17 = (int)(par2World.getWorldInfo().getWorldTime() / 10L);
                var16 = (byte)(var17 * var17 * 34187121 + var17 * 121 >> 15 & 15);
            }
        }
        else
        {
            if (Math.abs(var11) >= 320.0F || Math.abs(var12) >= 320.0F)
            {
                this.playersVisibleOnMap.remove(par3Str);
                return;
            }

            par1 = 6;
            var16 = 0;

            if (var11 <= (float)(-var15))
            {
                var13 = (byte)((int)((double)(var15 * 2) + 2.5D));
            }

            if (var12 <= (float)(-var15))
            {
                var14 = (byte)((int)((double)(var15 * 2) + 2.5D));
            }

            if (var11 >= (float)var15)
            {
                var13 = (byte)(var15 * 2 + 1);
            }

            if (var12 >= (float)var15)
            {
                var14 = (byte)(var15 * 2 + 1);
            }
        }

        this.playersVisibleOnMap.put(par3Str, new MapCoord(this, (byte)par1, var13, var14, var16));
    }

    /**
     * Get byte array of packet data to send to players on map for updating map data
     */
    public byte[] getUpdatePacketData(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        MapInfo var4 = (MapInfo)this.playersHashMap.get(par3EntityPlayer);
        return var4 == null ? null : var4.getPlayersOnMap(par1ItemStack);
    }

    /**
     * Marks a vertical range of pixels as being modified so they will be resent to clients. Parameters: X, lowest Y,
     * highest Y
     */
    public void setColumnDirty(int par1, int par2, int par3)
    {
        super.markDirty();

        for (int var4 = 0; var4 < this.playersArrayList.size(); ++var4)
        {
            MapInfo var5 = (MapInfo)this.playersArrayList.get(var4);

            if (var5.field_76209_b[par1] < 0 || var5.field_76209_b[par1] > par2)
            {
                var5.field_76209_b[par1] = par2;
            }

            if (var5.field_76210_c[par1] < 0 || var5.field_76210_c[par1] < par3)
            {
                var5.field_76210_c[par1] = par3;
            }
        }
    }

    public MapInfo func_82568_a(EntityPlayer par1EntityPlayer)
    {
        MapInfo var2 = (MapInfo)this.playersHashMap.get(par1EntityPlayer);

        if (var2 == null)
        {
            var2 = new MapInfo(this, par1EntityPlayer);
            this.playersHashMap.put(par1EntityPlayer, var2);
            this.playersArrayList.add(var2);
        }

        return var2;
    }

    public boolean IsEntityLocationVisibleOnMap(Entity var1)
    {
        int var2 = MathHelper.floor_double(var1.posX);
        int var3 = MathHelper.floor_double(var1.posY) + 2;
        int var4 = MathHelper.floor_double(var1.posZ);
        return var1.dimension == this.dimension ? this.IsLocationVisibleOnMap(var1.worldObj, var2, var3, var4) : false;
    }

    public boolean IsLocationVisibleOnMap(World var1, int var2, int var3, int var4)
    {
        int var5 = 1 << this.scale;
        float var6 = (float)((double)var2 - (double)this.xCenter) / (float)var5;
        float var7 = (float)((double)var4 - (double)this.zCenter) / (float)var5;

        if (Math.abs(var6) <= 64.0F && Math.abs(var7) <= 64.0F)
        {
            if (!var1.canBlockSeeTheSky(var2, var3, var4) && var1.getTopSolidOrLiquidBlock(var2, var4) > var3)
            {
                return false;
            }
            else
            {
                Material var8 = var1.getBlockMaterial(var2, var3, var4);
                return var8 == null || !var8.isLiquid();
            }
        }
        else
        {
            return false;
        }
    }
}