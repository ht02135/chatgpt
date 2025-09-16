<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Knockout.js Component with Method Call</title>
    <!-- Load Knockout.js from CDN -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/knockout/3.5.1/knockout-latest.js"></script>
    <style>
        body {
            font-family: sans-serif;
            padding: 20px;
            background-color: #f0f4f8;
            color: #333;
            display: flex;
            flex-direction: column;
            align-items: center;
        }
        .container {
            background-color: #fff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            text-align: center;
            max-width: 500px;
            width: 100%;
        }
        h1 {
            color: #2c3e50;
            margin-bottom: 20px;
        }
        p {
            font-size: 1.1em;
            margin-bottom: 20px;
        }
        a {
            color: #3498db;
            text-decoration: none;
            cursor: pointer;
            font-weight: bold;
            transition: color 0.2s ease-in-out;
        }
        a:hover {
            color: #2980b9;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Knockout Component Example</h1>
        <p>Current Status: <span data-bind="text: message"></span></p>

        <!-- The custom component tag. The component's internal logic handles the button click -->
        <!-- We now pass a parameter called `extraMessage` from the parent -->
        <my-component params="text: 'Click here!', message: message, extraMessage: paramToPass"></my-component>
    </div>

    <script>
        // 1. Register the custom component
        // This defines a new HTML element called <my-component>
        ko.components.register('my-component', {
            // The template defines the HTML structure for the component
            // We use an arrow function to pass the parent's `extraMessage` to the `handleClick` method.
            template: '<a href="#" data-bind="text: text, click: () => handleClick(params.extraMessage())"></a>',

            // The view model defines the data and behavior for the component
            // The params object is how data is passed from the parent to the component
            viewModel: function(params) {
                var self = this;
                
                // Expose the 'text' parameter from the parent, which is used for the link's label
                self.text = params.text;
                
                // Define the method that will be called when the link is clicked
                // It now accepts a new parameter from the click binding.
                self.handleClick = function(dynamicParam) {
                    // Update the message observable passed in from the parent,
                    // concatenating a message that uses the dynamically passed-in parameter.
                    params.message('You clicked the link inside the component! The dynamic parameter was: "' + dynamicParam + '"');
                };
            }
        });

        // 2. Define the main view model for the page
        // This is the data context for everything in the body that isn't inside a component
        function AppViewModel() {
            var self = this;
            
            // This is the observable that the component will update
            self.message = ko.observable('Waiting for a click...');
            
            // This is the observable that will be passed as a parameter to the component's method
            self.paramToPass = ko.observable('Hello from the parent!');
        }

        // 3. Apply the bindings to the page
        ko.applyBindings(new AppViewModel());
    </script>
</body>
</html>