'use strict';

$(document).ready( function() {

    console.log('Initializing...')

    $('.make-admin').click(function() {
        console.log('Making admin....');
        var userIdToPromote = $(this).siblings('.userId').first().attr('value');

        console.log('Promoting user with id: ' + userIdToPromote);

        $.post(
            '/make-admin/'+userIdToPromote,
            '',
            function( data ) {
                // refresh the page
                location.reload();
            },
            ''
        );
    });

    $('.grant-role').click(function() {
        console.log('Granting role...');
        var userIdToPromote = $(this).siblings('.userId').first().attr('value');

        $.post(
            '/grant-role-request/'+userIdToPromote,
            '',
            function( data ) {
                location.reload();
            },
            ''
        );

        console.log('Granting role to user with id: ' + userIdToPromote);
    });

});