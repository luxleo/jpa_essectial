package jpashop.executor;

import jpashop.domain.valuetype.Address;
import jpashop.domain.valuetype.AddressEntity;
import jpashop.domain.valuetype.People;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class EmbededTypeExecutor {
    private final EntityManager em;
    public void exec1() {
        System.out.println("=====[임베디드 타입 값 행동 테스트 시행====]");
        People people = new People();
        people.setName("빈지노");

        Address address1 = new Address("city1", "street", "zip");
        Address address2 = new Address("city2", "street", "zip");
        Address address3 = new Address("city3", "street", "zip");

        List<Address> addressHistory = people.getAddressHistory();
        addressHistory.add(address1);
        addressHistory.add(address2);
        addressHistory.add(address3);

        Set<String> foods = people.getFavoriteFoods();
        foods.add("치킨");
        foods.add("피자");
        foods.add("족발");

        System.out.println("\n임베디드 타입은 마치 cascade.ALL + orphan = true처럼 동작한다.");
        em.persist(people);
        em.flush();
        em.clear();

        System.out.println("\n임베디드 타입의 콜렉션은 수정시 저장된 모든 컬렉션 제거후 다시 삽입한다");
        People findPeople = em.find(People.class, people.getId());
        List<Address> findPeopleAddressHistory = findPeople.getAddressHistory();
        Address address = findPeopleAddressHistory.get(0);

        System.out.println("\n얼마나 비효율적인 쿼리가 나가는지 확인");
        findPeopleAddressHistory.remove(address);
        Address newAddress = new Address(address.getCity(), "changed street", address.getZipcode());
        findPeopleAddressHistory.add(newAddress);
        em.flush();
        em.clear();
        System.out.println("----------[exec1 end]---------");

    }
    public void exec2() {
        System.out.println("------------[exec2]------------");
        System.out.println("\n앞선 문제들을 해결하기 위해 값 타입을 wrapper entity로 승격하여 관리한다.");

        People people = new People();
        people.setName("binzino");
        em.persist(people);

        AddressEntity addressEntity = new AddressEntity("cityE1","streetE1","zipE1");
        AddressEntity addressEntity2 = new AddressEntity("cityE2","streetE2","zipE2");

        List<AddressEntity> addressHistoryV2 = people.getAddressHistoryV2();
        addressHistoryV2.add(addressEntity);
        addressHistoryV2.add(addressEntity2);

        Long peopleId = people.getId();
        System.out.println("peopleId = " + peopleId);

        em.flush();
        em.clear();

        System.out.println("엔티티로 승격이후에는 추가 쿼리가 발생하지 않는다. 수정이 자유롭다");
        People findPeople = em.find(People.class, peopleId);
        List<AddressEntity> historyV2 = findPeople.getAddressHistoryV2();

        AddressEntity findAddrEntity = historyV2.get(0);

        historyV2.remove(findAddrEntity);
        historyV2.add(new AddressEntity("cityE2updated", "new street","new zip"));

        em.flush();

    }
}
