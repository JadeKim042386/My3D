package joo.project.my3d.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

/**
 * 프론트엔드 작업시 참고하기위해 유지
 */
@Service
public class PaginationService {
    private static final int BAR_LENGTH = 5;
    public List<Integer> getPaginationBarNumbers(int currentPageNumber, int totalPages) {
        int startNumber = Math.max(currentPageNumber - (BAR_LENGTH / 2), 0);
        int endNumber = Math.min(startNumber + BAR_LENGTH, totalPages);

        return IntStream.range(startNumber, endNumber).boxed().toList();
    }
}
