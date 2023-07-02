$(document).ready(function () {
    $('.logout').click(function(e) {    
        $('#logoutform').submit();
        e.preventDefault();
    });    
});

