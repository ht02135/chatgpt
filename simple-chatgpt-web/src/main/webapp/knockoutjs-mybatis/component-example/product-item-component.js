// A component definition with an in-line template and a ViewModel.
// This is an example, in a real application the template might be
// a separate file loaded via a build tool.
ko.components.register('product-item', {
    viewModel: function(params) {
        this.name = params.product.name;
        this.price = params.product.price;
        this.inStock = params.product.inStock;
    },
    template:
        '<div>' +
        '<h3 data-bind="text: name"></h3>' +
        '<p>Price: $<span data-bind="text: price"></span></p>' +
        '<p>In Stock: <span data-bind="text: inStock"></span></p>' +
        '</div>'
});
/*
ko.components.register('product-item', { ... }): This is the key 
function that tells Knockout how to create and manage the component. 
We give it a name ('product-item').

viewModel: This is the component's own ViewModel. It receives data 
from its parent (in this case, the product object from the foreach 
loop) via the params object.

template: This is the HTML for the component's UI. For simplicity, 
we define it as an in-line JavaScript string, but in a real app, 
a build tool would convert an external HTML file into this string.
*/