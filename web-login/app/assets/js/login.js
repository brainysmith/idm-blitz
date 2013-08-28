console.log('start login.js');

require(['ie', 'main', 'conf', 'jquery'],
    function (ie, main, conf, $) {
        console.log('running the login module');
        console.log(ie);
    });

console.log('end login.js');