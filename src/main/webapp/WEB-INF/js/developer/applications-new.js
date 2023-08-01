$(document).ready(function() {
    var url = $('#applicationsaddform').data('complexity-url');
    var errorMsg = $('#applicationsaddform').data('complexity-message');
    var generateClientSecretWidget = new GeneratePasswordWidget('clientSecret', url, errorMsg, 19);
});