$(document).ready(function() {
    var url = $('#applicationschangesecretform').data('complexity-url');
    var errorMsg = $('#applicationschangesecretform').data('complexity-message');
    var generateClientSecretWidget = new GeneratePasswordWidget('clientSecret', url, errorMsg, 19);
});