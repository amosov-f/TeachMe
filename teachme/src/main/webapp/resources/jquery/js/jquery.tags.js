(function($) {
    'use strict';

    var defaults = {
        tags: new Array(),
        newTagsOutput: null
    }

    var tags = new Array();
    var splitter = /,\s*/g;

    var methods = {
        init: function(params) {
            var options = $.extend({}, defaults, params);

            tags = options.tags;

            return this.each(function() {
                $(this).autocomplete({
                    delimiter: splitter,
                    maxHeight: 130,
                    onSelect: function() {
                        $(this).val($(this).val() + ', ');
                    }
                });

                $(this).bind('click change paste keyup keydown textchange', function() {
                    if (options.newTagsOutput != null) {
                        options.newTagsOutput.val(viewConcat($(this).tags('newTags')));
                    }
                    $(this).autocomplete().setOptions({
                        lookup: сomplement(tags, $(this).tags('chosenTags'))
                    });
                });
            });
        },

        chosenTags: function() {
            var str = trim($(this).val());
            if (str === '') {
                return new Array();
            }
            return jQuery.unique(str.split(splitter));
        },

        newTags: function() {
            return сomplement($(this).tags('chosenTags'), tags);
        }
    };

    $.fn.tags = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error( 'Метод "' +  method + '" не найден в плагине jQuery.tags' );
        }
    };

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

})(jQuery);