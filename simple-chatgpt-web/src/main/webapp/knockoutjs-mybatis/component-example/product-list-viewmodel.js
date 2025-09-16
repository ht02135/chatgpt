/*
This file, product-list-viewmodel.js, holds the data for our page, 
just like in previous examples. It's the parent ViewModel that will 
use our component.
*/

// Product data model
function Product(name, price, inStock) {
    this.name = ko.observable(name);
    this.price = ko.observable(price);
    this.inStock = ko.observable(inStock);
}

// Main ViewModel for the page
function ProductListViewModel() {
    this.products = ko.observableArray([
        new Product("Laptop", 1200, true),
        new Product("Mouse", 25, false),
        new Product("Keyboard", 75, true)
    ]);
}