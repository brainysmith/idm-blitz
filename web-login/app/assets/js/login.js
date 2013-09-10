require(['ie', 'main', 'conf', 'jquery', 'kendo'],
    function (ie, main, conf, $, kendo) {
        /*login form*/
        kendo.bind($("#loginFm"), loginModel("#loginFm"));

        /*change password form*/
        var altPswdPPJq = $('#altPswdPP');
        var altPswdPP = altPswdPPJq.data("kendoWindow");
        if (!altPswdPP) {
            altPswdPPJq.kendoWindow({
                actions: {},
                draggable: true,
                modal: true,
                resizable: false,
                visible: false,
                width: "900px",
                title: "Changing the password" /*todo: change it*/
            });
            altPswdPP = altPswdPPJq.data("kendoWindow");
        }
        altPswdPP.center();

        kendo.bind($("#altPswdFm"), altPswdModel("#altPswdFm"));

        /*models*/
        function loginModel(formId) {
            var m = {
                'login': "",
                'pswd': "",
                'do': function() {
                    var that =this;
                    var bodyJ = {};
                    bodyJ.login = this.get('login');
                    bodyJ.pswd = this.get('pswd');
                    router.controllers.Login.basicLogin()
                        .ajax({
                            contentType: 'application/json',
                            data: JSON.stringify(bodyJ),
                            global: true,
                            beforeSend: function() {
                            },
                            success: function(request, textStatus, jqXHR) {
                                main.clearFormErrors(formId);
                                var res = $.parseJSON(jqXHR.responseText);
                                if (res.obligation) {
                                    doObligation(res.obligation)
                                } else if (res.toUrl) {
                                    //todo: do it
                                    throw "Hasn't realized yet.";
                                } else {
                                    //todo: do it
                                    throw "Hasn't realized yet.";
                                }
                            },
                            error: function(jqXHR, textStatus, errorThrown) {
                                if (jqXHR.status == 400) {
                                    main.showFormErrors(formId, jqXHR.responseText);
                                    jqXHR.processed = true;
                                }
                            },
                            complete: function(jqXHR, textStatus) {
                                that.set('pswd', "");
                            }
                        });
                }
            }
            return kendo.observable(m);
        }

        function altPswdModel(formId) {
            var m = {
                'curPswd': "",
                'newPswd': "",
                'do': function() {
                    var that =this;
                    var bodyJ = {};
                    bodyJ.curPswd = this.get('curPswd');
                    bodyJ.newPswd = this.get('newPswd');
                    router.controllers.Login.altPswd()
                        .ajax({
                            contentType: 'application/json',
                            data: JSON.stringify(bodyJ),
                            global: true,
                            beforeSend: function() {
                            },
                            success: function(request, textStatus, jqXHR) {
                                main.clearFormErrors(formId);
                                //todo: do it
                                throw "Hasn't realized yet.";
                            },
                            error: function(jqXHR, textStatus, errorThrown) {
                                if (jqXHR.status == 400) {
                                    main.showFormErrors(formId, jqXHR.responseText);
                                    jqXHR.processed = true;
                                }
                            },
                            complete: function(jqXHR, textStatus) {
                                that.set('curPswd', "");  //todo: thinking about it
                                that.set('newPswd', "");
                            }
                        });
                }
            }
            return kendo.observable(m);
        }


        //functions
        function doObligation(obligation) {
            switch(obligation) {
                case "change_password":
                    altPswdPP.center();
                    altPswdPP.open();
                    break;
                default:
                    //todo: do it
                    throw "Hasn't realized yet.";
            }
        }

    }
);