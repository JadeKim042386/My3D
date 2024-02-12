const BAR_LENGTH = 5;

function getPaginationBarNumbers(currentPageNumber, totalPages) {
    var startNumber = Math.max(currentPageNumber - (BAR_LENGTH / 2), 0);
    var endNumber = Math.min(startNumber + BAR_LENGTH, totalPages);
    var numbers = [];
    for (var i = startNumber; i < endNumber; i++) {
        numbers.push(i);
    }
    return numbers;
}
