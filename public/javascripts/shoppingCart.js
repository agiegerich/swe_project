'use strict';

$(document).ready(function() {
    console.log("Setting up event handlers...");

    var totalValueOfItems = 0;

    $('.cart-listing').each(function() {
        var quantity = $(this).children('.cart-item-quantity').first().attr('value');
        var price = $(this).children('.cart-item-price').first().attr('value');
        console.log('Quantity: ' + quantity);
        console.log('Price: ' + price + '\n');

        totalValueOfItems += parseInt(quantity) * parseInt(price);
    });

    console.log("Total value of items: " + totalValueOfItems);
    $('#confirmation-dialog').hide();

    $('#checkout-button').click(function() {
        $('#confirmation-dollars').val('');
        $('#confirmation-cents').val('');
        $('#confirmation-dialog-error').text('');
        $('#confirmation-dialog-txt').text('Enter the total value of the products in the shopping cart to confirm.');
        $('#confirmation-dialog').dialog({
            modal: true,
            'buttons' : [
                {
                    text: 'confirm',
                    click : function() {
                        var status = totalValueInputIsValid();
                        if ( !status.success ) {
                            $('#confirmation-dialog-error').text(status.msg).fadeOut(100).fadeIn(100);
                        } else if (priceEqualsTotalValue(totalValueOfItems)) {
                            $('#cart-form').submit();
                        } else {
                            $('#confirmation-dialog-error').text('That is not the correct value.').fadeOut(100).fadeIn(100);
                        }
                    }
                },
                {
                    text: 'cancel',
                    click : function() {
                        $(this).dialog('close');
                    }
                }
            ]
        });
    });
});

function totalValueInputIsValid() {
    var status = {};
    var dollarsValue = $('#confirmation-dollars').val();
    var centsValue = $('#confirmation-cents').val();

    status.success = dollarsValue.match(/^\d+$/) && centsValue.match(/^\d{1,2}$/);
    if (status.success) {
        return status;
    }

    if ( dollarsValue.indexOf('.') > -1 || centsValue.indexOf('.') > -1  ) {
        status.msg = 'No periods are allowed in either input box.';
    } else if ( centsValue.length === 0 ) {
        status.msg = 'The cents box must contain a number.';
    } else if ( centsValue.length > 2 ) {
        status.msg = 'Only two values allowed in cents box.';
    }

    return status;
}



function priceEqualsTotalValue( totalValue ) {
    var inputValue = parseInt($('#confirmation-dollars').val())*100 + parseInt($('#confirmation-cents').val());
    console.log(inputValue);
    return inputValue === totalValue;
}