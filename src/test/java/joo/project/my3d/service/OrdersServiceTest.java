package joo.project.my3d.service;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.Orders;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.dto.OrdersDto;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.AlarmRepository;
import joo.project.my3d.repository.CompanyRepository;
import joo.project.my3d.repository.OrdersRepository;
import joo.project.my3d.repository.UserAccountRepository;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 주문")
@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @InjectMocks private OrdersService ordersService;
    @Mock private OrdersRepository ordersRepository;
    @Mock private UserAccountRepository userAccountRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private AlarmRepository alarmRepository;
    @Mock private AlarmService alarmService;

    @DisplayName("유저 이메일로 모든 주문 조회")
    @Test
    void getOrdersByEmail() {
        // Given
        String email = "a@gmail.com";
        given(ordersRepository.findByUserAccount_Email(anyString())).willReturn(anyList());
        // When
        ordersService.getOrdersByEmail(email);
        // Then
        then(ordersRepository).should().findByUserAccount_Email(anyString());
    }

    @DisplayName("기업 id로 모든 주문 조회")
    @Test
    void getOrdersByCompanyId() {
        // Given
        Long companyId = 1L;
        given(ordersRepository.findByCompanyId(anyLong())).willReturn(anyList());
        // When
        ordersService.getOrdersByCompanyId(companyId);
        // Then
        then(ordersRepository).should().findByCompanyId(anyLong());
    }

    @DisplayName("주문 추가")
    @Test
    void saveOrders() throws IllegalAccessException {
        // Given
        OrdersDto ordersDto = FixtureDto.getOrdersDto();
        UserAccount userAccount = ordersDto.userAccountDto().toEntity();
        Alarm alarm = Fixture.getAlarm(userAccount);
        FieldUtils.writeField(alarm, "id", 1L, true);
        Company company = ordersDto.companyDto().toEntity();
        FieldUtils.writeField(company, "id", 1L, true);
        given(userAccountRepository.getReferenceById(ordersDto.userAccountDto().email())).willReturn(ordersDto.userAccountDto().toEntity());
        given(companyRepository.getReferenceById(ordersDto.companyDto().id())).willReturn(company);
        given(ordersRepository.save(any(Orders.class))).willReturn(ordersDto.toEntity(userAccount, company));
        given(userAccountRepository.findByCompanyId(anyLong())).willReturn(Optional.of(userAccount));
        given(alarmRepository.save(any(Alarm.class))).willReturn(alarm);
        willDoNothing().given(alarmService).send(eq(userAccount.getEmail()), eq(1L));
        // When
        ordersService.saveOrders(ordersDto);
        // Then
        then(userAccountRepository).should().getReferenceById(ordersDto.userAccountDto().email());
        then(companyRepository).should().getReferenceById(ordersDto.companyDto().id());
        then(ordersRepository).should().save(any(Orders.class));
        then(userAccountRepository).should().findByCompanyId(anyLong());
        then(alarmRepository).should().save(any(Alarm.class));
        then(alarmService).should().send(eq(userAccount.getEmail()), eq(1L));
    }

    @DisplayName("주문 수정")
    @Test
    void updateOrders() {
        // Given
        OrdersDto ordersDto = FixtureDto.getOrdersDto();
        UserAccount userAccount = Fixture.getUserAccount();
        Company company = Fixture.getCompany();
        given(ordersRepository.getReferenceById(ordersDto.id())).willReturn(ordersDto.toEntity(userAccount, company));
        // When
        ordersService.updateOrders(ordersDto);
        // Then
        then(ordersRepository).should().getReferenceById(ordersDto.id());
    }

    @DisplayName("주문 삭제 - 주문 요청 유저와 삭제 요청 유저가 일치")
    @Test
    void deleteOrdersByEmail() {
        // Given
        Orders orders = Fixture.getOrders(Fixture.getUserAccount(), Fixture.getCompany());
        given(ordersRepository.getReferenceById(orders.getId())).willReturn(orders);
        willDoNothing().given(ordersRepository).deleteById(orders.getId());
        // When
        ordersService.deleteOrders(orders.getId(), orders.getUserAccount().getEmail());
        // Then
        then(ordersRepository).should().getReferenceById(orders.getId());
        then(ordersRepository).should().deleteById(orders.getId());
    }

    @DisplayName("주문 삭제 - 기업과 삭제 요청 유저가 일치")
    @Test
    void deleteOrdersByCompanyName() {
        // Given
        Orders orders = Fixture.getOrders(Fixture.getUserAccount(), Fixture.getCompany());
        given(ordersRepository.getReferenceById(orders.getId())).willReturn(orders);
        willDoNothing().given(ordersRepository).deleteById(orders.getId());
        // When
        ordersService.deleteOrders(orders.getId(), orders.getCompany().getCompanyName());
        // Then
        then(ordersRepository).should().getReferenceById(orders.getId());
        then(ordersRepository).should().deleteById(orders.getId());
    }
}
