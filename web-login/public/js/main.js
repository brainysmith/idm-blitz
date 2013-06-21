(function page() {
	
	//initializing
	$(document).ready(function() {
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
})()