package model.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import model.GiftList;
import model.indexes.BPlusTree;
import model.indexes.ExtensibleHashTable;
import model.indexes.pairs.indirect.PairEmailId;
import model.indexes.pairs.indirect.PairNameId;
import model.indexes.pairs.indirect.PairIdId;
import model.indexes.pairs.indirect.PairNanoIdId;

public class GiftListDAO extends DAO<GiftList> {
    ExtensibleHashTable<PairNanoIdId> indirectIndexNanoId;
    BPlusTree<PairIdId> indirectIndexIdId;
    BPlusTree<PairNameId> indirectIndexNameId;

    public GiftListDAO() throws Exception {
        super("giftlist", GiftList.class.getConstructor());
        indirectIndexNanoId = new ExtensibleHashTable<>(PairNanoIdId.class.getConstructor(), 3, "./data/giftlist.nanoid.d.db", "./data/giftlist.nanoid.c.db");
        indirectIndexIdId = new BPlusTree<>(PairIdId.class.getConstructor(), 3, "./data/user.giftlist.id.db");
        indirectIndexNameId = new BPlusTree<>(PairNameId.class.getConstructor(), 3, "./data/user.giftlist.name.db");
    }

    public int create(GiftList giftList, int userId) throws Exception {
        int id = super.create(giftList);
        indirectIndexNanoId.create(new PairNanoIdId(giftList.getShareableCode(), id));
        indirectIndexIdId.create(new PairIdId(userId, id));
        indirectIndexNameId.create(new PairNameId(userId, giftList.getName()));
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
        ArrayList<GiftList> nonNullGiftLists = new ArrayList<>();

        for (int i = 0; i < pairs.size(); i++) {
            GiftList giftList = super.read(pairs.get(i).getIdAggregate());
            if (giftList != null) {
                nonNullGiftLists.add(giftList);
            }
        }

        return nonNullGiftLists.toArray(new GiftList[0]);
    }

    public GiftList[] readByUserIdTheName(int userId) throws Exception {
        GiftList[] giftLists = readByUserId(userId);

        if (giftLists.length > 1) {
            // Ordena as listas por nome usando Comparator
            Arrays.sort(giftLists, new Comparator<GiftList>() {
                @Override
                public int compare(GiftList a, GiftList b) {
                    return a.getName().compareTo(b.getName());
                }
            });
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
                indirectIndexNameId.create(new PairNameId(newGiftList.getUserId(), newGiftList.getName()));
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
                && indirectIndexIdId.delete(new PairIdId(userId, id)) 
                && indirectIndexNameId.delete(new PairNameId(userId, giftList.getName()))) return true;
        }
        
        return false;
    }
}