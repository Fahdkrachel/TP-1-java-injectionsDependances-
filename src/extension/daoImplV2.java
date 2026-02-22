package extension;

import dao.IDao;

public class daoImplV2 implements IDao {

    @Override
    public double getDta() {
        System.out.println("version 2");
        return 32;
    }
}
