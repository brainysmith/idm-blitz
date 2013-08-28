console.log('start ie.js');

define(function () {
    var undef,
        v = 5,
        div = document.createElement('div'),
        all = div.getElementsByTagName('i');

    while(div.innerHTML = '<!--[if gt IE ' + (++v) + ']><i></i><![endif]-->', all[0]){}
    return v > 6 ? v : undef;
});

console.log('end ie.js');