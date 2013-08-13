console.log('start placeholder.js');

define(['jquery', 'main', 'domReady'],
    function ($) {
        $('input[placeholder]').each(function(){

            // init
            var placeholder = $(this).attr('placeholder');
            if ($(this).attr('type') == 'password') {
                $(this).addClass('password-placeholder').attr('type','text');
            }
            $(this).val(placeholder)

                // focus
                .focus(function(){
                    if ($(this).hasClass('password-placeholder')) {
                        $(this).attr('type','password');
                    }
                    if ($(this).val() == placeholder) {
                        $(this).val('');
                    }
                })

                // blur
                .blur(function(){
                    if ($(this).val() == '') {
                        $(this).val(placeholder);
                        $(this).attr('type','text');
                    }
                })
        });
    });

console.log('end placeholder.js');