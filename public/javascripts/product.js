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

    $('.buy-button').click( function() {
        var productId = $(this).val();
        console.log("Buying product with id: " + productId);
        var productPrice = $(this).parent()
        $('#buy-confirmation-dialog-txt').text("Enter the quantity you'd like to purchase.");
        $('#buy-confirmation-dialog').dialog({
            modal: true,
            'buttons' : [
                {
                    text: 'submit',
                    click : function() {

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