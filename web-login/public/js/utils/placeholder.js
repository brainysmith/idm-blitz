/**
 * Created with IntelliJ IDEA.
 * User: tyashneva
 * Date: 22.05.13
 * Time: 13:15
 * To change this template use File | Settings | File Templates.
 */
$(document).ready(function(){
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

