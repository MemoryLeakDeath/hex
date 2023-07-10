$.ajaxPrefilter(function(options, original, xhr) {
    var csrf_token = $('meta[name="_csrf"]').attr('content');
    var csrf_header = $('meta[name="_csrf_header"]').attr('content');
    if (["post","get"].includes(options.type.toLowerCase())) {
        options.data = options.data || "";
        options.data += options.data?"&":"";
        options.data += "_token=" + encodeURIComponent(csrf_token);
        xhr.setRequestHeader(csrf_header, csrf_token);
    }    
});

$(document).ready(function () {
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

