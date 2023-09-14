package joo.project.my3d.service;

import joo.project.my3d.domain.Orders;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.dto.OrdersDto;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.OrdersRepository;
import joo.project.my3d.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 모델 파일")
@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @InjectMocks private OrdersService ordersService;
    @Mock private OrdersRepository ordersRepository;
    @Mock private UserAccountRepository userAccountRepository;

    @DisplayName("유저 이메일로 모든 주문 조회")
    @Test
    void getOrdersByEmail() {
        // Given
        String email = "a@gmail.com";
        given(ordersRepository.findByUserAccount_Email(anyString())).willReturn(anyList());
        // When
        ordersService.getOrders(email);
        // Then
        then(ordersRepository).should().findByUserAccount_Email(anyString());
    }

    @DisplayName("주문 추가")
    @Test
    void saveOrders() {
        // Given
        OrdersDto ordersDto = FixtureDto.getOrdersDto();
        given(userAccountRepository.getReferenceById(ordersDto.userAccountDto().email())).willReturn(ordersDto.userAccountDto().toEntity());
        given(ordersRepository.save(any(Orders.class))).willReturn(any(Orders.class));
        // When
        ordersService.saveOrders(ordersDto);
        // Then
        then(userAccountRepository).should().getReferenceById(ordersDto.userAccountDto().email());
        then(ordersRepository).should().save(any(Orders.class));
    }

    @DisplayName("주문 수정")
    @Test
    void updateOrders() {
        // Given
        OrdersDto ordersDto = FixtureDto.getOrdersDto();
        UserAccount userAccount = Fixture.getUserAccount();
        given(ordersRepository.getReferenceById(ordersDto.id())).willReturn(ordersDto.toEntity(userAccount));
        // When
        ordersService.updateOrders(ordersDto);
        // Then
        then(ordersRepository).should().getReferenceById(ordersDto.id());
    }

    @DisplayName("주문 삭제")
    @Test
    void deleteOrders() {
        // Given
        Orders orders = Fixture.getOrders(Fixture.getUserAccount());
        given(ordersRepository.getReferenceById(orders.getId())).willReturn(orders);
        willDoNothing().given(ordersRepository).deleteById(orders.getId());
        // When
        ordersService.deleteOrders(orders.getId(), orders.getUserAccount().getEmail());
        // Then
        then(ordersRepository).should().getReferenceById(orders.getId());
        then(ordersRepository).should().deleteById(orders.getId());
    }
}
