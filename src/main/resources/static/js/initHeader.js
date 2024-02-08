const ACCESS_TOKEN = 'accessToken';
const REFRESH_TOKEN = 'refreshToken';
const NICKNAME = 'nickname';
const USER_ROLE = 'userRole';

const initHandler = () => {
    //signin/signup 페이지일 경우 로그인, 로그아웃 버튼 등을 모두 삭제
    if (location.href.includes('signin') || location.href.includes('signup')) {
        initNotLoginUserElements();
        $('#login').remove();
        return;
    }
    //유효한 AccessToken이 없을 경우 로그인 버튼만 남기고 있을 경우 로그인 버튼만 삭제하고 유저 정보를 API 호출로 가져온다
    !!getCookie(ACCESS_TOKEN) ? initLoginUserElements() : initNotLoginUserElements();
}

/**
 * 로그인 버튼만 남김
 */
const initNotLoginUserElements = () => {
    $('#logout').remove();
    $('#userAdminButton').remove();
    $('#headerNickname').remove();
    $('#alarmButton').remove();
}

/**
 * 로그인 버튼만 삭제하고 API 요청으로 토큰으로부터 유저 정보를 얻음
 */
const initLoginUserElements = () => {
    $('#login').remove();
    getInfoAndValidateToken();
}

/**
 * API 요청으로 토큰으로부터 유저 정보(email, nickname, userRole)을 얻음
 */
const getInfoAndValidateToken = () => {
    $.ajax({
        url: '/api/v1/signin/info',
        type: 'GET',
        success: function (result) {
            if (result[USER_ROLE] !== 'COMPANY') {
                $('#adminCompany').remove();
            }
            $('#headerNickname').text(result[NICKNAME]);
        },
        error: function (result) {
            console.log(result.responseJSON['message']);
            deleteCookie(ACCESS_TOKEN);
            deleteCookie(REFRESH_TOKEN);
            window.location.href = "/";
        }
    })
}

window.onload = initHandler();

/**
 * 토큰들을 삭제하고 메인페이지로 redirect
 */
const logout = () => {
    deleteCookie(ACCESS_TOKEN);
    deleteCookie(REFRESH_TOKEN);
    window.location.href = "/";
}

function getCookie(name) {
    let matches = document.cookie.match(new RegExp(
        "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
    ));
    return matches ? decodeURIComponent(matches[1]) : undefined;
}

/**
 * example: setCookie('user', 'Joo', {secure: true, 'max-age': 3600});
 */
function setCookie(name, value, options = {}) {

    options = {
        path: '/'
    };

    if (options.expires instanceof Date) {
        options.expires = options.expires.toUTCString();
    }

    let updatedCookie = encodeURIComponent(name) + "=" + encodeURIComponent(value);

    for (let optionKey in options) {
        updatedCookie += "; " + optionKey;
        let optionValue = options[optionKey];
        if (optionValue !== true) {
            updatedCookie += "=" + optionValue;
        }
    }

    document.cookie = updatedCookie;
}

function deleteCookie(name) {
    setCookie(name, "", {
        'max-age': -1
    })
}
