<!DOCTYPE html>
<html>
<head>
    <title>Knockout Components Demo</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/knockout/3.5.1/knockout-latest.js"></script>
</head>
<body>
	
	<!--
	This file, index.html, is where everything comes together. Notice 
	that we don't have a <script type="text/html"> tag. Instead, we 
	include all our JavaScript files and use the component as a 
	custom element.
	-->

    <div id="app">
        <h2>Our Products</h2>
        <ul data-bind="foreach: products">
            <li>
                <product-item params="product: $data"></product-item>
            </li>
        </ul>
    </div>

    <script src="product-item-component.js"></script>
    <script src="product-list-viewmodel.js"></script>
    <script>
        // Once everything is loaded, apply the bindings.
        ko.applyBindings(new ProductListViewModel(), document.getElementById('app'));
    </script>
</body>
</html>