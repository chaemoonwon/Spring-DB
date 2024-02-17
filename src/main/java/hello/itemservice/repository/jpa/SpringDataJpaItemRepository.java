package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpringDataJpaItemRepository extends JpaRepository<Item, Long> {

    //검색 조건 쿼리

    //이름 조건만
    List<Item> findByItemNameLike(String itemName);

    //가격 조건만
    List<Item> findByPriceLessThanEqual(Integer price);


    //이름, 가격 조건 모두 포함
    //(아래 메서드는 모두 같은 기능 수행)
    //쿼리 메서드
    List<Item> findByItemNameLikeAndPriceLessThanEqual(String itemName, Integer price);
    //쿼리 직접 실행
    @Query("select i from Item i where i.itemName like :itemName and i.price <= :price")
    List<Item> findItems(@Param("itemName") String itemName, @Param("price") Integer price);

}
