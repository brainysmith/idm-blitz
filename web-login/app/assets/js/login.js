console.log('start login.js');

requirejs(['jquery', 'kendo', 'main'],
    function ($, kendo, main) {
        console.log('running the login module');
        console.log(main);
    });

console.log('end login.js');