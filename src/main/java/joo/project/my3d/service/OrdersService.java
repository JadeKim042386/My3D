package joo.project.my3d.service;

import joo.project.my3d.domain.Orders;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.dto.OrdersDto;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.OrdersException;
import joo.project.my3d.repository.OrdersRepository;
import joo.project.my3d.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final UserAccountRepository userAccountRepository;


    public List<OrdersDto> getOrders(String email) {
        return ordersRepository.findByUserAccount_Email(email)
                .stream().map(OrdersDto::from)
                .toList();
    }

    @Transactional
    public void saveOrders(OrdersDto dto) {
        try{
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().email());
            Orders orders = dto.toEntity(userAccount);
            ordersRepository.save(orders);
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
    public void deleteOrders(Long ordersId, String email) {
        Orders orders = ordersRepository.getReferenceById(ordersId);
        //작성자와 삭제 요청 유저가 같은지 확인
        if (orders.getUserAccount().getEmail().equals(email)) {
            ordersRepository.deleteById(ordersId);
        } else {
            log.error("작성자와 삭제 요청 유저가 다릅니다. 작성자: {} - 삭제 요청: {}", orders.getUserAccount().getEmail(), email);
            throw new OrdersException(ErrorCode.COMMENT_NOT_WRITER);
        }
    }
}
