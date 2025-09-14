package model.dao;

import model.User;
import model.indexes.ExtensibleHashTable;
import model.indexes.pairs.indirect.PairEmailId;

public class UserDAO extends DAO<User> {
    ExtensibleHashTable<PairEmailId> indirectIndexEmailId;

    public UserDAO() throws Exception {
        super("user", User.class.getConstructor());
        this.indirectIndexEmailId = new ExtensibleHashTable<>(PairEmailId.class.getConstructor(), 3, "./data/user.email.d.db", "./data/user.email.c.db");
    }

    public int create(User user) throws Exception {
        int id = super.create(user);
        this.indirectIndexEmailId.create(new PairEmailId(user.getEmail(), id));

        return id;
    }

    public User readByEmail(String email) throws Exception {
        PairEmailId pei = this.indirectIndexEmailId.read(PairEmailId.hashCode(email));

        if (pei == null) return null;
        int id = pei.getId();
        
        return super.read(id);
    }

    public boolean update(User newUser) throws Exception {
        User oldUser = super.read(newUser.getId());
        String oldEmail = oldUser.getEmail();

        if (super.update(newUser)) {
            if (newUser.getEmail().compareTo(oldEmail) != 0) {
                this.indirectIndexEmailId.delete(PairEmailId.hashCode(oldEmail));
                this.indirectIndexEmailId.create(new PairEmailId(newUser.getEmail(), newUser.getId()));
            }

            return true;
        }
        else return false;
    }

    @Override
    public boolean delete(int id) throws Exception {
        User user = super.read(id);
        
        if (user == null) return false;

        GiftListDAO giftListDAO = new GiftListDAO();

        if (giftListDAO.readByUserId(id).length == 0 && super.delete(id)) {
            this.indirectIndexEmailId.delete(PairEmailId.hashCode(user.getEmail()));
            return true;
        }
        else return false;
    }
}
