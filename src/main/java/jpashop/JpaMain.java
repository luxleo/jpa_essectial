package jpashop;

import jpashop.domain.car.ElectricCar;
import jpashop.domain.car.OilCar;
import jpashop.domain.item.Book;
import jpashop.executor.JpqlLearnExec;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            //carTest(em);
            //itemTest(em);
            //em.flush();
            //em.clear();

            //ProxyExec proxyExec = new ProxyExec(em);
            //proxyExec.typeCheck();
            //proxyExec.lazyAndEagerCheck();
            //proxyExec.CascadeAndOrphanObjectCheck();
            //proxyExec.CascadeAndOrphanObjectCheckV2();
            //proxyExec.CascadeAndOrphanObjectCheckV3();

            //EmbededTypeExecutor executor = new EmbededTypeExecutor(em);
            //executor.exec1();
            //executor.exec2();

            JpqlLearnExec jpqlExec = new JpqlLearnExec(em);
            //jpqlExec.projection_test();
            jpqlExec.paging_test();

            em.flush();
            em.clear();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }

    private static void carTest(EntityManager em) {
        ElectricCar electricCar = new ElectricCar();
        electricCar.setCapacity(1000);
        electricCar.setName("tesla");
        electricCar.setPrice(10000);
        electricCar.setPower(10000);

        OilCar oilCar = new OilCar();
        oilCar.setEngine("v8");
        oilCar.setName("rambo");
        oilCar.setPrice(10000);
        oilCar.setPower(10000);
        oilCar.setEfficiency(30);

        OilCar oilCar2 = new OilCar();
        oilCar2.setEngine("v12");
        oilCar2.setName("bugati");
        oilCar2.setPrice(100000);
        oilCar2.setPower(100000);
        oilCar2.setEfficiency(10);

        em.persist(electricCar);
        em.persist(oilCar);
        em.persist(oilCar2);
    }

    public static void itemTest(EntityManager em){
        Book book = new Book();
        book.setAuthor("dragon");
        book.setIsbn("1111");
        book.setName("good book");
        book.setPrice(10000);
        book.setStockQuantity(100);

        em.persist(book);
    }
}
