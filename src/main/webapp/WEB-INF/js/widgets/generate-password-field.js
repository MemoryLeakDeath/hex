class GeneratePasswordWidget {
	constructor(name, complexityUrl, complexityMessage, complexityCheckThreshold) {
		this.fieldName = name;
		this.complexityUrl = complexityUrl;
		this.complexityMessage = complexityMessage;
		this.complexityCheckThreshold = complexityCheckThreshold;
		this.isGeneratePasswordPopoverShown = false;
		this.#init();
	}
	
	#init() {
	    var timeout = null;
	    $('#' + this.fieldName).keyup(function(e) {
	        var newPassword = $('#' + this.fieldName).val();
	        if(newPassword.length > this.complexityCheckThreshold) {
	            if(timeout != null) {
	                clearTimeout(timeout);
	            }
	            timeout = setTimeout(this.#callComplexityService, 1000, this.fieldName, this.complexityUrl, newPassword, this.complexityMessage);
	        }
	        e.stopPropagation();
	    });
	
	    $('#generate' + this.fieldName + 'button').popover( 
	        {
	            html: true, 
	            placement: "top", 
	            sanitize: false, 
	            trigger: "manual",
	            content: ''
	        });
	    $('#generate' + this.fieldName + 'button').on('click', {'fieldName' : this.fieldName}, this.#callPasswordGeneratorService);    
	    $('#generate' + this.fieldName + 'button').on('hidden.bs.popover', function() {
	       this.isGeneratePasswordPopoverShown = false;
	    });
	    $('#generate' + this.fieldName + 'button').on('shown.bs.popover', function() {
	       this.isGeneratePasswordPopoverShown = true;
	    });	
	    
		$('html').on('click', '#copy' + this.fieldName + 'toclipboardbutton', {'fieldName' : this.fieldName}, this.#copyToClipboard);
		$('html').on('click', '#generate' + this.fieldName + 'button', {'fieldName' : this.fieldName}, this.#callPasswordGeneratorService);
		$('html').on('click', '#regenerate' + this.fieldName + 'button', {'fieldName' : this.fieldName}, this.#regenerate);	    	
	}
	
	#callComplexityService(fieldName, url, newPassword, errorMsg) {
	    $.post(url, {password: newPassword}).done(function(data) {
	       if(data.notComplexEnough) {
	           this.#showPasswordComplexityMessage(fieldName, errorMsg);
	       } else {
	           this.#hidePasswordComplexityMessage(fieldName);
	       }
	    });		
	}
	
	#showPasswordComplexityMessage(fieldName, errorMsg) {
		var newPasswordField = $('#' + fieldName);
		if(!$(newPasswordField).hasClass('is-invalid')) {
		    $(newPasswordField).addClass('is-invalid');
		    $('#' + fieldName + '-error').text(errorMsg);
		}
	}
	
	#hidePasswordComplexityMessage(fieldName) {
	    $('#' + fieldName).removeClass('is-invalid');
	    $('#' + fieldName + '-error').text('');		
	}
	
	#callPasswordGeneratorService(event) {
		var fieldName = event.data.fieldName;
	    var url = $('#generate' + fieldName + 'button').data('url');
	    var popover = bootstrap.Popover.getInstance(document.getElementById('generate' + fieldName + 'button'));
	    if(this.isGeneratePasswordPopoverShown) {
	        popover.hide();
	    } else {
	        $.post(url).done(function(html) {
	            popover.setContent({'.popover-body': html});
	            popover.show();
	        });
	    }
	    event.stopPropagation();
	}
	
	#copyToClipboard(event) {
		var fieldName = event.data.fieldName;
	    var password = $('#generated' + fieldName).text();
	    navigator.clipboard.writeText(password).then(
	        () => {
	            $('#copy' + fieldName + 'toclipboardbutton').addClass('btn-success');
	        },
	        () => {
	            $('#copy' + fieldName + 'toclipboardbutton').addClass('btn-danger');
	        }
	    );
	}
	
	#regenerate(event) {
		var fieldName = event.data.fieldName;
	    var url = $(this).data('url');
	    $.post(url).done(function(response) {
	        $('#generated' + fieldName).text(response.data);
	        $('#copy' + fieldName + 'toclipboardbutton').removeClass('btn-success');
	        $('#copy' + fieldName + 'toclipboardbutton').removeClass('btn-danger');
	    });
	}
}
