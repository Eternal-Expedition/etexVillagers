package me.uranusdestroyer.etexvillagers.features.objects;

public class etexVillagerTrade {
    private int slotId;

    private String tradeId;


    private int used;

    public etexVillagerTrade(int slotId, String tradeId, int used) {
        this.slotId = slotId;
        this.tradeId = tradeId;
        this.used = used;
    }

    // Getter and setter methods (or make fields public)

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public int getUsed() {
        return used;}

    public void setUsed(int used) {
        this.used = used;}

}

