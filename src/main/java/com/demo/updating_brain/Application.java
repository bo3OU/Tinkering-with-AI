package com.demo.updating_brain;

import com.demo.updating_brain.prreview.service.PrReviewTools;
import com.demo.updating_brain.shipping.repository.ItemRepository;
import com.demo.updating_brain.shipping.repository.OrderRepository;
import com.demo.updating_brain.shipping.repository.UserRepository;
import com.demo.updating_brain.shipping.service.ShippingMcpTools;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@EntityScan("com.demo.updating_brain.shipping.entity")
@EnableJpaRepositories(basePackages = "com.demo.updating_brain.shipping.repository")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public List<ToolCallback> springIOShippingTools(ShippingMcpTools shippingMcpTools, PrReviewTools prReviewTools) {
        return Stream
                .concat(Stream.of(ToolCallbacks.from(prReviewTools)),
                    Stream.of(ToolCallbacks.from(shippingMcpTools)))
                .collect(Collectors.toList());
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepo, OrderRepository orderRepo, ItemRepository itemRepo) {
        return args -> {
//			itemRepo.deleteAll();
//			orderRepo.deleteAll();
//			userRepo.deleteAll();
//
//			User user1 = new User("@Ali_bagho", "ali bagho");
//			User user2 = new User("@Sara", "sara bagho");
//			userRepo.save(user1);
//			userRepo.save(user2);
//
//			Order order1 = new Order(user1.getId(), OrderStatus.PENDING, "Paris", "Berlin", LocalDate.of(2025, 11, 15));
//			Order order2 = new Order(user1.getId(), OrderStatus.SHIPPED, "Lyon", "Madrid", LocalDate.of(2025, 11, 10));
//			Order order3 = new Order(user2.getId(), OrderStatus.DELIVERED, "Marseille", "Rome", LocalDate.of(2025, 11, 8));
//			Order order4 = new Order(user2.getId(), OrderStatus.PENDING, "Toulouse", "Barcelona", LocalDate.of(2025, 11, 20));
//			orderRepo.saveAll(List.of(order1, order2, order3, order4));
//
//			// Create items for orders
//			// Order 1: Small electronics (fits in Small Box)
//			Item laptop = new Item();
//			laptop.setName("Laptop");
//			laptop.setDescription("13-inch laptop");
//			laptop.setLength(30.0);
//			laptop.setWidth(20.0);
//			laptop.setHeight(2.0);
//			laptop.setWeightKg(1.5);
//			laptop.setFragile(true);
//			laptop.setCategory("electronics");
//			laptop.setOrderId(order1.getId());
//
//			Item mouse = new Item();
//			mouse.setName("Wireless Mouse");
//			mouse.setLength(10.0);
//			mouse.setWidth(6.0);
//			mouse.setHeight(4.0);
//			mouse.setWeightKg(0.1);
//			mouse.setFragile(false);
//			mouse.setCategory("electronics");
//			mouse.setOrderId(order1.getId());
//
//			// Order 2: Books (needs Medium Box)
//			Item book1 = new Item();
//			book1.setName("Programming Book");
//			book1.setLength(24.0);
//			book1.setWidth(18.0);
//			book1.setHeight(3.0);
//			book1.setWeightKg(0.8);
//			book1.setFragile(false);
//			book1.setCategory("books");
//			book1.setOrderId(order2.getId());
//
//			Item book2 = new Item();
//			book2.setName("Design Book");
//			book2.setLength(24.0);
//			book2.setWidth(18.0);
//			book2.setHeight(3.0);
//			book2.setWeightKg(0.8);
//			book2.setFragile(false);
//			book2.setCategory("books");
//			book2.setOrderId(order2.getId());
//
//			Item book3 = new Item();
//			book3.setName("History Book");
//			book3.setLength(24.0);
//			book3.setWidth(18.0);
//			book3.setHeight(4.0);
//			book3.setWeightKg(1.0);
//			book3.setFragile(false);
//			book3.setCategory("books");
//			book3.setOrderId(order2.getId());
//
//			// Order 3: Clothing (Large Box)
//			Item tshirt = new Item();
//			tshirt.setName("T-Shirt");
//			tshirt.setLength(40.0);
//			tshirt.setWidth(30.0);
//			tshirt.setHeight(2.0);
//			tshirt.setWeightKg(0.2);
//			tshirt.setFragile(false);
//			tshirt.setCategory("clothing");
//			tshirt.setOrderId(order3.getId());
//
//			Item jeans = new Item();
//			jeans.setName("Jeans");
//			jeans.setLength(50.0);
//			jeans.setWidth(40.0);
//			jeans.setHeight(5.0);
//			jeans.setWeightKg(0.5);
//			jeans.setFragile(false);
//			jeans.setCategory("clothing");
//			jeans.setOrderId(order3.getId());
//
//			Item jacket = new Item();
//			jacket.setName("Winter Jacket");
//			jacket.setLength(60.0);
//			jacket.setWidth(50.0);
//			jacket.setHeight(15.0);
//			jacket.setWeightKg(1.2);
//			jacket.setFragile(false);
//			jacket.setCategory("clothing");
//			jacket.setOrderId(order3.getId());
//
//			// Order 4: Fragile home decor (needs careful packaging)
//			Item vase = new Item();
//			vase.setName("Ceramic Vase");
//			vase.setLength(25.0);
//			vase.setWidth(25.0);
//			vase.setHeight(35.0);
//			vase.setWeightKg(1.5);
//			vase.setFragile(true);
//			vase.setCategory("home-decor");
//			vase.setOrderId(order4.getId());
//
//			Item plates = new Item();
//			plates.setName("Dinner Plates (4pc)");
//			plates.setLength(30.0);
//			plates.setWidth(30.0);
//			plates.setHeight(10.0);
//			plates.setWeightKg(2.0);
//			plates.setFragile(true);
//			plates.setCategory("kitchenware");
//			plates.setOrderId(order4.getId());
//
//			itemRepo.saveAll(List.of(laptop, mouse, book1, book2, book3,
//			                          tshirt, jeans, jacket, vase, plates));
        };
    }
}
