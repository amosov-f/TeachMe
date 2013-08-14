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