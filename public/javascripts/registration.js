'use strict';

$(document).ready(function() {

    // Call the function initially to hide the manager/admin ID field if user role is initially selected.
    showHideManagerAdminIdDependingOnRole();

    // Register the function to be called every time the role selection is clicked.
    $('#role').click(showHideManagerAdminIdDependingOnRole);
});

function showHideManagerAdminIdDependingOnRole() {
    console.log( $('#role').val() );
    if ( $('#role').val() === 'MANAGER' || $('#role').val() === 'ADMIN' ) {
        console.log("Showing admin/manager id.")
        $('#mngrID').show();
    } else {
        console.log("Hiding admin/manager id.")
        $('#mngrID').hide();
    }
}