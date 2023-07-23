$('html').on('click', '#copyToClipboardButton', copyPasswordToClipboard);
$('html').on('click', '#regeneratePasswordButton', regeneratePassword);

var isGeneratePasswordPopoverShown = false;    
$(document).ready(function() {
    var url = $('#updatePasswordForm').data('complexity-url');
    var errorMsg = $('#updatePasswordForm').data('complexity-message');
    var timeout = null;
    $('#newPassword').keyup(function(e) {
        var newPassword = $('#newPassword').val();
        if(newPassword.length > 9) {
            if(timeout != null) {
                clearTimeout(timeout);
            }
            timeout = setTimeout(callComplexityService, 1000, url, newPassword, errorMsg);
        }
        e.stopPropagation();
    });

    $('#generatePasswordButton').popover( 
        {
            html: true, 
            placement: "top", 
            sanitize: false, 
            trigger: "manual",
            content: ''
        });
    $('#generatePasswordButton').on('click', callPasswordGeneratorService);    
    $('#generatePasswordButton').on('hidden.bs.popover', function() {
       isGeneratePasswordPopoverShown = false;
    });
    $('#generatePasswordButton').on('shown.bs.popover', function() {
       isGeneratePasswordPopoverShown = true;
    });
});

function callComplexityService(url, newPassword, errorMsg) {
    $.post(url, {password: newPassword}).done(function(data) {
       if(data.notComplexEnough) {
           showPasswordComplexityMessage(errorMsg);
       } else {
           hidePasswordComplexityMessage();
       }
    });
}

function showPasswordComplexityMessage(errorMsg) {
    var newPasswordField = $('#newPassword');
    if(!$(newPasswordField).hasClass('is-invalid')) {
        $(newPasswordField).addClass('is-invalid');
        $('#newpassword-error').text(errorMsg);
    }
}

function hidePasswordComplexityMessage() {
    $('#newPassword').removeClass('is-invalid');
    $('#newpassword-error').text('');
}

function callPasswordGeneratorService(event) {
    var url = $('#generatePasswordButton').data('url');
    var popover = bootstrap.Popover.getInstance(document.getElementById('generatePasswordButton'));
    if(isGeneratePasswordPopoverShown) {
        popover.hide();
    } else {
        $.post(url).done(function(html) {
            popover.setContent({'.popover-body': html});
            popover.show();
        });
    }
    event.stopPropagation();
}

function copyPasswordToClipboard() {
    var password = $('#generatedpassword').text();
    navigator.clipboard.writeText(password).then(
        () => {
            $('#copyToClipboardButton').addClass('btn-success');
        },
        () => {
            $('#copyToClipboardButton').addClass('btn-danger');
        }
    );
}

function regeneratePassword() {
    var url = $(this).data('url');
    $.post(url).done(function(response) {
        $('#generatedpassword').text(response.data);
        $('#copyToClipboardButton').removeClass('btn-success');
        $('#copyToClipboardButton').removeClass('btn-danger');
    });
}