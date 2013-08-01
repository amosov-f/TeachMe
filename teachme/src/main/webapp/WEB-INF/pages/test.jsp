<%@ page import="com.kk.teachme.model.Problem" %>
<html>
<head>
    <script type="text/javascript" src="/resources/jquery/jquery-1.9.1.js"></script>
    <script type="text/javascript" src="/resources/jquery/jquery.form.js"></script>

    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap.min.js"></script>

    <link href="/resources/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" media="screen">
    <link href="/resources/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">


</head>
<body>
<div id="cblist">
    <input type="checkbox" value="first checkbox" id="cb1" /> <label for="cb1">first checkbox</label>
</div>

<input type="text" id="txtName" />
<input type="button" value="ok" id="btnSave" />

<script type="text/javascript">
    $(document).ready(function() {
        $('#btnSave').click(function() {
            addCheckbox($('#txtName').val());
        });
    });

    function addCheckbox(name) {
        var container = $('#cblist');
        var inputs = container.find('input');
        var id = inputs.length+1;

        $('<input />', { type: 'checkbox', id: 'cb'+id, value: name }).appendTo(container);
        $('<label />', { 'for': 'cb'+id, text: name }).appendTo(container);
    }
</script>
</body>
</html>
