$(document).ready(function() {
    $('#resendbutton').click(function() {
        $.get($(this).data('url')).done(function() {
            console.log("Email verification re-sent!");
        }).fail(function() {
            console.log("Failed to resend verification");
        });
    });
});