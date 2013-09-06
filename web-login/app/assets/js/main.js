console.log('start main.js');

define(['jquery', 'conf', 'kendo', 'domReady'],
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
        };

        //default ajax error handler
        var ajaxErrorHandler = function (event, jqxhr, settings, exception) {
            if (jqxhr.processed) {
                return;
            }

            showError();
        };
        $(document).ajaxError(ajaxErrorHandler);

        return {
            'errPP': errPP,
            'showError': showError,
            'closePPs': function () {
                $('.k-window-content.k-content').each(function(index) {
                    var iPP = $(this).data('kendoWindow');
                    if (iPP) {
                        iPP.close();
                    }
                });
            },
            "ddErrorTmp": kendo.template("<dd class='error'>#= msg #</dd>"),
            "clearFormErrors": function(formId) {
                $(formId + " dd.error").remove();
            },
            "showFormErrors": function(formId, response) {
                //clear error
                this.clearFormErrors(formId);

                var responseJson = $.parseJSON(response);
                if (responseJson.errors) {
                    var errors = responseJson.errors;
                    var fields = Object.keys(errors);
                    for (var i = 0; i < fields.length; i++) {
                        var field = fields[i];
                        var msgs = (errors[field]).reverse();
                        for (var j = 0; j < msgs.length; j++) {
                            if (field == "") {
                                $(formId + "_lineError dt").after(this.ddErrorTmp({msg: msgs[j]}));
                            } else {
                                $(formId + " input[name='" + field + "']").parent().after(this.ddErrorTmp({msg: msgs[j]}));
                            }
                        }

                        if (field == "") {
                            $(formId + "_lineError").show();
                        }
                    }
                }
            }
        }
    });

console.log('end main.js');