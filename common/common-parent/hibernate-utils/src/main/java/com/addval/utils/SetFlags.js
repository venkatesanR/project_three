function setDefault(parameter, defaultValue)
{
	if (typeof parameter == "undefined" || parameter == "")
		return defaultValue;
	else
		return parameter;
}
function setDirtyFlag(frmName, eleName, delimiter) {

	var form;
	var flagName;
	delimiter = setDefault(delimiter, "_");		
	frmName = setDefault(frmName, "");
	
	form = document.frmName;

	if (typeof form != "object")	
		form = document.forms[0];

	flagName = "dirty" + eleName.substring(eleName.lastIndexOf(delimiter), eleName.length);


	for (var j = 0; j <= form.elements.length-1; j++) {
		if (form.elements[j].type == "hidden" && form.elements[j].name == flagName) {
//			alert(form.elements[j].name + " : " + form.elements[j].value);
			if (form.elements[j].value != "-1")
				form.elements[j].value = "-1";
//			alert(form.elements[j].name + " : " + form.elements[j].value);
			break;
		}
	}		
}


function getAllValues()
{
	var form = document.forms[0];
	for (var j = 0; j <= form.elements.length-1; j++) {
		alert(form.elements[j].value);		
	}
}

function setPagingAction(frm, actionValue,flag) {
	if (!flag)
        {
	var form;
        
	if (typeof frm != "object")
	{
		frm = setDefault( frm, "" );
		form = document.frmName;

		if (typeof form != "object")	
			form = document.forms[0];
	}
	else
	{
		form = frm;
	}
	
	form.PAGING_ACTION.value = actionValue;	

	form.submit();
        }
}

function setSortingAction(frmName, sortName, sortOrder) {
	
	var form;

	frmName = setDefault(frmName, "");
	
	form = document.frmName;

	if (typeof form != "object")	
		form = document.forms[0];
	
	form.SORT_ORDER.value = sortOrder;
	form.SORT_NAME.value = sortName;

	setPagingAction(form, "FIRST");

}
