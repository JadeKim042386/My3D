package joo.project.my3d.service;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.Orders;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.AlarmType;
import joo.project.my3d.dto.OrdersDto;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.OrdersException;
import joo.project.my3d.repository.AlarmRepository;
import joo.project.my3d.repository.CompanyRepository;
import joo.project.my3d.repository.OrdersRepository;
import joo.project.my3d.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final UserAccountRepository userAccountRepository;
    private final CompanyRepository companyRepository;
    private final AlarmRepository alarmRepository;
    private final AlarmService alarmService;


    public List<OrdersDto> getOrdersByEmail(String email) {
        return ordersRepository.findByUserAccount_Email(email)
                .stream().map(OrdersDto::from)
                .toList();
    }

    public List<OrdersDto> getOrdersByCompanyId(Long companyId) {
        return ordersRepository.findByCompanyId(companyId)
                .stream().map(OrdersDto::from)
                .toList();
    }

    @Transactional
    public void saveOrders(OrdersDto dto) {
        try{
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().email());
            Company company = companyRepository.getReferenceById(dto.companyDto().id());
            Orders orders = dto.toEntity(userAccount, company);
            ordersRepository.save(orders);

            UserAccount companyUserAccount = userAccountRepository.findByCompanyId(company.getId())
                    .orElseThrow(() -> new UsernameNotFoundException("기업 유저를 찾을 수 없습니다. id=" + company.getId()));
            Alarm alarm = alarmRepository.save(
                    Alarm.of(
                            AlarmType.NEW_ORDER,
                            userAccount.getEmail(),
                            company.getId(),
                            companyUserAccount
                    )
            );
            alarmService.send(companyUserAccount.getEmail(), alarm.getId());
        } catch (EntityNotFoundException e) {
            log.warn("주문 저장 실패! - {}", new OrdersException(ErrorCode.FAILED_SAVE));
        }
    }

    @Transactional
    public void updateOrders(OrdersDto dto) {
        try {
            Orders orders = ordersRepository.getReferenceById(dto.id());
            if (dto.status() != null) {
                orders.setStatus(dto.status());
            }
        } catch (EntityNotFoundException e) {
            log.warn("주문 수정 실패! - dto: {} {}", dto, new OrdersException(ErrorCode.ORDERS_NOT_FOUND));
        }
    }

    @Transactional
    public void deleteOrders(Long ordersId, String emailOrCompanyName) {
        Orders orders = ordersRepository.getReferenceById(ordersId);
        //주문 요청 유저 또는 기업과 삭제 요청 유저가 같은지 확인
        if (orders.getUserAccount().getEmail().equals(emailOrCompanyName) || orders.getCompany().getCompanyName().equals(emailOrCompanyName)) {
            ordersRepository.deleteById(ordersId);
        } else {
            log.error("작성자와 삭제 요청 유저가 다릅니다. 작성자: {} - 삭제 요청: {}", orders.getUserAccount().getEmail(), emailOrCompanyName);
            throw new OrdersException(ErrorCode.NOT_WRITER);
        }
    }
}
