const findPassword = () => {
    $.ajax({
        url: 'api/v1/mail/find_pass',
        type: 'POST',
        success: function (result) {
            console.log(result['message']);
        },
        error: function (result) {
            console.log(result.responseJSON);
        }
    })
}