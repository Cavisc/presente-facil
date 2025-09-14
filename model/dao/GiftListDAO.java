package model.dao;

import java.util.ArrayList;

import model.GiftList;
import model.indexes.BPlusTree;
import model.indexes.ExtensibleHashTable;
import model.indexes.pairs.indirect.PairEmailId;
import model.indexes.pairs.indirect.PairIdId;
import model.indexes.pairs.indirect.PairNanoIdId;

public class GiftListDAO extends DAO<GiftList> {
    ExtensibleHashTable<PairNanoIdId> indirectIndexNanoId;
    BPlusTree<PairIdId> indirectIndexIdId;

    public GiftListDAO() throws Exception {
        super("giftlist", GiftList.class.getConstructor());
        indirectIndexNanoId = new ExtensibleHashTable<>(PairNanoIdId.class.getConstructor(), 3, "./data/giftlist.nanoid.d.db", "./data/giftlist.nanoid.c.db");
        indirectIndexIdId = new BPlusTree<>(PairIdId.class.getConstructor(), 3, "./data/user.giftlist.db");
    }

    public int create(GiftList giftList, int userId) throws Exception {
        int id = super.create(giftList);
        indirectIndexNanoId.create(new PairNanoIdId(giftList.getShareableCode(), id));
        indirectIndexIdId.create(new PairIdId(userId, id));
        return id;
    }

    public GiftList readByNanoId(String nanoId) throws Exception {
        PairNanoIdId pairNanoIdId = indirectIndexNanoId.read(PairNanoIdId.hashCode(nanoId));

        if (pairNanoIdId == null) return null;
        int id = pairNanoIdId.getId();

        return super.read(id);
    }

    public GiftList[] readByUserId(int userId) throws Exception {
        ArrayList<PairIdId> pairs = indirectIndexIdId.read(new PairIdId(userId, -1));
        GiftList[] giftLists = new GiftList[pairs.size()];
        
        for (int i = 0; i < pairs.size(); i++) {
            giftLists[i] = super.read(pairs.get(i).getIdAggregate());
        }

        return giftLists;
    }

    public boolean update(GiftList newGiftList) throws Exception {
        GiftList oldGiftList = super.read(newGiftList.getId());
        String oldNanoId = oldGiftList.getShareableCode();

        if (super.update(newGiftList)) {
            if (newGiftList.getShareableCode().compareTo(oldNanoId) != 0) {
                indirectIndexNanoId.delete(PairEmailId.hashCode(oldNanoId));
                indirectIndexNanoId.create(new PairNanoIdId(newGiftList.getShareableCode(), newGiftList.getId()));
            }

            return true;
        }
        else return false;
    }

    public boolean delete(int id, int userId) throws Exception {
        GiftList giftList = super.read(id);
        
        if (giftList == null) return false;

        if (super.delete(id)) {
            if (indirectIndexNanoId.delete(PairNanoIdId.hashCode(giftList.getShareableCode())) 
                && indirectIndexIdId.delete(new PairIdId(userId))) return true;
        }
        
        return false;
    }
}
