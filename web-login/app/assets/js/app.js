requirejs.config({
    baseUrl: 'assets/js/lib',
    paths: {
        "app": "../app",
        "jquery" : "jquery-1.9.0.min",
        "jquery-ui" : "jquery-ui-1.10.3.min",
        "kendo" :"kendo.web.min",
        "domReady" : "requirePlugins/domReady"
    },
    shim : {
        "jquery-ui" : [ 'jquery' ],
        "kendo" : [ 'jquery' ]
    },
    waitSeconds: 15
});

requirejs(['jquery', 'kendo', 'domReady'],
    function ($) {
        var errPpJq = $('#errPP');
        if (!errPpJq.data("kendoWindow")) {
            errPpJq.kendoWindow({
                draggable: false,
                modal: true,
                resizable: false,
                visible: false,
                width: "520px",
                title: mainMsg("main.errPP.title")
            });
            errPp = errPpJq.data("kendoWindow");
            errPp.center();
        }
    });