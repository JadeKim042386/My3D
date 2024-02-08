const initAlarm = () => {
    eventSource = new EventSource("/api/v1/alarm/subscribe");
    eventSource.addEventListener('alarm', function (event) {
        getAlarms();
    })
}

window.onload = initAlarm();

function getAlarms() {
    $.ajax({
        url: "/api/v1/alarm",
        type: "GET",
        success: function (result) {
            if (isIterable(result)){
                setAlarms(result);
            }
        },
        error: function (result) {
            console.log(result.responseText);
        }
    });
}

function isIterable(obj) {
    if (obj == null) {
        return false;
    }
    return typeof obj[Symbol.iterator] === 'function';
}

function setAlarms(result) {
    $('.alarm-dropdown').find('li').slice(1).remove();
    var notReadAlarmCount = 0;
    for (var alarm of result) {
        var email = alarm['fromUserNickname'];
        var createdAt = alarm['createdAt'];
        var url = '/'+ alarm["targetId"] + '?alarmId=' + alarm["id"];
        var content;
        if (alarm['alarmType'] === 'NEW_COMMENT_ON_POST') {
            content = '님이 댓글을 달았습니다';
        }
        var newAlarm = '<li> <a class="dropdown-item alarm-item'
            + (alarm['isChecked'] ? '' : ' fw-semibold')
            + '" href='
            + url + '> <div class="row"> <div class="col"> <p id="alarm-nickname" class="mb-1">'
            + email + '</p> </div> <div class="col"> <div class="d-flex justify-content-end"> <p id="alarm-created_at" class="mb-1">'
            + createdAt + '</p> </div> </div> </div> <div class="row"> <p class="alarm-content">'
            + content + '</p> </div> </a> </li>';

        if (!alarm['isChecked']) {
            notReadAlarmCount++;
        }

        $('.alarm-dropdown').append(newAlarm);
    }

    if (notReadAlarmCount > 0) {
        $('.existNewAlarm').show();
        $('.notExistNewAlarm').hide();
    }
}
