console.log('start main.js');

define(['jquery', 'kendo', 'domReady'],
    function ($) {
        console.log('defining the app module');

        //error popup
        var errPpJq = $('#errPP');
        var errPP = errPpJq.data("kendoWindow");
        if (!errPP) {
            errPpJq.kendoWindow({
                draggable: false,
                modal: true,
                resizable: false,
                visible: false,
                width: "520px",
                title: mainMsg("main.errPP.title")
            });
            errPP = errPpJq.data("kendoWindow");
            errPP.center();
        }

        //show error
        var showError = function (error) {
            if (error) {
                $('#errMsg').html(error);
            } else {
                $('#errMsg').html(mainMsg("main.errPP.defaultError"));
            }
            errPP.open();
        }

        //default ajax error handler
        var ajaxErrorHandler = function (event, jqxhr, settings, exception) {
            showError();
        }
        $(document).ajaxError(ajaxErrorHandler);

        return {
            model: {
            },
            'errPP': errPP,
            'showError': showError,
            'closePPs': function () {
                $('.k-window-content.k-content').each(function(index) {
                    var iPP = $(this).data('kendoWindow');
                    if (iPP) {
                        iPP.close();
                    }
                });
            }
        }
    });

console.log('end main.js');