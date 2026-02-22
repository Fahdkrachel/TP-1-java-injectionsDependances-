package metier;

import dao.IDao;

public class MetierImpl implements IMetier {
    private IDao dao;

    public MetierImpl() {
    }

    public MetierImpl(IDao dao) {
        this.dao = dao;
    }
    @Override
    public double calcul() {
        double t = 2 * dao.getDta();
        return t;
    }

    public void setDao(IDao dao) {
        this.dao = dao;
    }
}
