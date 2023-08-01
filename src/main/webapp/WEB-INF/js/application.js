var csrf_token = $('meta[name="_csrf"]').attr('content');
var csrf_header = $('meta[name="_csrf_header"]').attr('content');

$.ajaxPrefilter(function(options, original, xhr) {
    if (["post","get"].includes(options.type.toLowerCase())) {
        options.data = options.data || "";
        options.data += options.data?"&":"";
        options.data += "_token=" + encodeURIComponent(csrf_token);
        xhr.setRequestHeader(csrf_header, csrf_token);
    }    
});

$(document).ready(function () {
	// The following adds csrf token to htmx requests
	document.body.addEventListener('htmx:configRequest', (event) => {
	  event.detail.headers[csrf_header] = csrf_token;
	});
	
    $('.logout').click(function(e) {    
        $('#logoutform').submit();
        e.preventDefault();
    });
    
    $('.login').click(function(e) {
        var url = $(this).data('url');
        var id = $(this).data('id');
        $.get(url).done(function(html) {
            $(id).html(html);
            $('#loginModal').modal("show");
        });
        e.preventDefault();
    });  
});

function showSuccessMessage(msg) {
	showMessage(msg, 'success');
}

function showErrorMessage(msg) {
	showMessage(msg, 'error');
}

function showInfoMessage(msg) {
	showMessage(msg, 'info');
}

function showWarningMessage(msg) {
	showMessage(msg, 'warn');
}

function showMessage(msg, type) {
	var html = "";
	if(type === 'success') {
		html += `<div class="alert alert-success alert-dismissable" role="alert">
				<span><i class="fa-solid fa-circle-check"></i>&nbsp;${msg}</span>
				<button type="button" class="btn-close float-end" data-bs-dismiss="alert"></button>
				</div>`;
	} else if(type === 'error') {
		html += `<div class="alert alert-danger alert-dismissable" role="alert">
				<span><i class="fa-solid fa-triangle-exclamation"></i>&nbsp;${msg}</span>
				<button type="button" class="btn-close float-end" data-bs-dismiss="alert"></button>
				</div>`;
	} else if(type === 'warn') {
		html += `<div class="alert alert-warning alert-dismissable" role="alert">
				<span><i class="fa-solid fa-circle-exclamation"></i>&nbsp;${msg}</span>
				<button type="button" class="btn-close float-end" data-bs-dismiss="alert"></button>
				</div>`;		
	} else if(type === 'info') {
		html += `<div class="alert alert-info alert-dismissable" role="alert">
				<span><i class="fa-solid fa-circle-info"></i>&nbsp;${msg}</span>
				<button type="button" class="btn-close float-end" data-bs-dismiss="alert"></button>
				</div>`;		
	}
	$('#topMessagesBox').append(html);
}

function copyToClipboard(text, successMsg, errorMsg) {
	navigator.clipboard.writeText(text).then(() => {
		showSuccessMessage(successMsg);
	}, () => {
		showErrorMessage(errorMsg);
	});	
}

function toggleBlurText(textTag, iconTag) {
	$(textTag).toggleClass('blur');
	if($(iconTag).hasClass('fa-eye')) {
		$(iconTag).removeClass('fa-eye');
		$(iconTag).addClass('fa-eye-slash')
	} else {
		$(iconTag).removeClass('fa-eye-slash');
		$(iconTag).addClass('fa-eye');
	}	
}
