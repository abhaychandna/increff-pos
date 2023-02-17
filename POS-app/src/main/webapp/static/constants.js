const HOST_URL = location.protocol + '//' + location.host;
const BASE_URL = $("meta[name=baseUrl]").attr("content");
const LOGOUT_URL = HOST_URL + BASE_URL + "/session/logout";
const LOGOUT_REDIRECT_URL = HOST_URL + BASE_URL;

const LOGOUT_ALERT_TIME_MILLI = 1500;
const ERROR_ALERT_TIME_MILLI = 20000;

const UPLOAD_MAX_ROW_COUNT = 5000;