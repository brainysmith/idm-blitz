console.log('start login.js');

require(['ie', 'main', 'conf', 'jquery', 'kendo'],
    function (ie, main, conf, $, kendo) {
        console.log('running the login module');
        console.log(ie);

        kendo.bind($("#loginFm"), basicModel("#loginFm"));

        function basicModel(formId) {
            var ddErrorTemp = kendo.template("<dd class='error'>#= value #</dd>");
            var m = {
                'lgn': "",
                'pswd': "",
                'login': function() {
                    console.log('running login')
                    var bodyJ = {};
                    bodyJ.lgn = this.get('lgn');
                    bodyJ.pswd = this.get('pswd');
                    routes.javascript.Login.basicLogin().ajax({
                        contentType: 'application/json',
                        data: JSON.stringify(bodyJ),
                        global: true,
                        beforeSend: function(jqXHR, settings) {

                        },
                        success: function(request, textStatus, jqXHR) {
                            //clear error
                            $(formId + " dd.error").remove();

                            if(successFun != null && successFun != undefined){
                                successFun(data);
                            }

                        },
                        error: function(jqXHR, textStatus, errorThrown) {
                            //clear error
                            $(formId + " dd.error").remove();

                            var jsonKeys = Object.keys($.parseJSON(jqXHR.responseText));
                            for (var i = 0; i < jsonKeys.length; i++) {
                                var key = jsonKeys[i];
                                var values = $.parseJSON(jqXHR.responseText)[key];
                                values = values.reverse();

                                for (var j = 0; j < values.length; j++) {
                                    if (key == "") {
                                        $(formId + "_lineError dt").after(ddErrorTemp({value: values[j]}));
                                    } else {

                                        $(formId + " input[name='" + key + "']").parent().after(ddErrorTemp({value: values[j]}));
                                    }
                                }

                                if (key == "") {
                                    $(formId + "_lineError").show();
                                }
                            }

                        }
                    })

                }
            }
            return kendo.observable(m);
        }
    }
);

console.log('end login.js');