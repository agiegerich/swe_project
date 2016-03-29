'use strict';

$(document).ready(function() {
    console.log('Setting up event handlers...')
    $('.category-selector').change( function() {
        var category = $(this).val();
        if (category === 'all') {
            $('.product-listing').show();
        } else {
            $('.product-listing').hide();
            $('.category-'+category).show();
        }
    });

    // Register the event handler that searches products.
    $('.search-button').click( function() {
        $('.product-listing').hide();
        // trim whitespace off the search text
        var searchText = $('.product-search-bar').val().toLowerCase().trim();
        var selectedCategory = $('.category-selector').val();
        console.log("Searching for: " + searchText);
        $('.product-listing').each(function() {

            // Decide if the current product listing is in the selected category/
            var categoryOfProductListing = $(this).children('.product-category').first().text();
            var isInCategory = selectedCategory === 'all' || selectedCategory === categoryOfProductListing;

            // Hide the product listing if it's name does not contain the search.
            if ( $(this).children('.product-name').first().text().toLowerCase().indexOf( searchText ) > -1 && isInCategory ) {
                $(this).show();
            }
        });
    });

    // Register the event handler that allows users to buy items.
    $('.buy-button').click( function() {
        var productId = $(this).val();
        var currentBuyButton = this;

        // Changes the state after the quantity of items is selected.
        var quantitySelected = false;
        var quantityAvailable = parseInt( $(this).parent().siblings('.product-quantity').first().attr('value') );
        var quantity = 0;
        var productPrice = '';

        // set the default as 1
        $('#confirmation-quantity').val(1);
        $('#confirmation-quantity').show();

        console.log("Buying product with id: " + productId);
        $('#buy-confirmation-dialog-txt').text("Enter the quantity you'd like to purchase.");
        $('#buy-confirmation-dialog').dialog({
            modal: true,
            'buttons' : [
                {
                    text: 'submit',
                    click : function() {
                        if ( quantitySelected ) {
                            $(this).dialog('close');
                            // Make the post to buy the product.
                            $.post(
                                '/buy-product/'+productId+'/'+quantity,
                                '',
                                function( data ) {
                                    // Refresh the page to display the change.
                                    location.reload();
                                },
                                'json'
                            )
                        } else {
                            quantity = parseInt( $('#confirmation-quantity').val() );

                            // Make sure there are enough items available for purchase.
                            if ( quantity > quantityAvailable ) {
                                $(this).dialog('close');
                                $('#buy-error-dialog-txt').text("There are not that many items available for purchase.");
                                $('#buy-error-dialog').dialog({
                                    modal: true
                                });
                            }

                            console.log("Quantity: " + quantity);
                            productPrice = parseInt($(currentBuyButton).parent().siblings('.product-price').first().attr('value'));
                            console.log("Price: " + productPrice);
                            $('#buy-confirmation-dialog-txt').text("This will cost "+centsToFormattedDollars( productPrice*quantity ) + '. Continue?');
                            quantitySelected = true;
                            $('#confirmation-quantity').hide();
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