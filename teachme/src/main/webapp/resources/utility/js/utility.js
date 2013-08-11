function contains(obj, el) {
    return (obj.indexOf(el) != -1);
}

function сomplement(a, b) {
    var result = [];
    for (var i = 0; i < a.length; ++i) {
        if (!contains(b, a[i])) {
            result.push(a[i]);
        }
    }
    return result;
}

function trim(str) {
    if (str.trim() === '') {
        return '';
    }
    var l = -1, r;
    for (var i = 0; i < str.length; ++i) {
        if (str[i] != ' ' &&  str[i] != ',') {
            if (l == -1) {
                l = i;
            }
            r = i;
        }
    }
    return str.substr(l, r + 1);
}

function viewConcat(strArray) {
    var result = '';
    for (var i = 0; i < strArray.length; ++i) {
        if (i > 0) {
            result += ', ';
        }
        result += strArray[i];
    }
    return result;
}

function concat(strArray) {
    var result = '';
    for (var i = 0; i < strArray.length; ++i) {
        if (i > 0) {
            result += ',';
        }
        result += strArray[i];
    }
    return encodeURIComponent(result);
}