const API_BASE = '/chatgpt/api/mybatis/properties';

// --- Model ---
function Property(data) {
    this.key = ko.observable(data.key);
    this.type = ko.observable(data.type);
    this.value = ko.observable(data.value);
}

// --- List ViewModel ---
function PropertyListViewModel() {
    var self = this;
    self.searchKey = ko.observable("");
    self.searchType = ko.observable("");
    self.properties = ko.observableArray([]);
    self.page = ko.observable(1);
    self.size = ko.observable(10);
    self.total = ko.observable(0);
    self.maxPage = ko.observable(1);
    self.sortField = ko.observable("key");
    self.sortOrder = ko.observable("ASC");

    self.searchProperties = function() {
        var params = {
            key: self.searchKey(),
            type: self.searchType(),
            page: self.page(),
            size: self.size(),
            sort: self.sortField(),
            order: self.sortOrder()
        };
        var url = API_BASE + "/all?" + $.param(params);
        $.getJSON(url, function(resp) {
            if (resp && resp.data) {
                var arr = resp.data.properties || [];
                self.properties(arr.map(function(p) { return new Property(p); }));
                self.total(resp.data.total || arr.length);
                self.maxPage(resp.data.maxPage || 1);
            }
        });
    };

    self.setSort = function(field) {
        if (self.sortField() === field) {
            self.sortOrder(self.sortOrder() === "ASC" ? "DESC" : "ASC");
        } else {
            self.sortField(field);
            self.sortOrder("ASC");
        }
        self.searchProperties();
    };

    self.nextPage = function() {
        if (self.page() < self.maxPage()) {
            self.page(self.page() + 1);
            self.searchProperties();
        }
    };
    self.prevPage = function() {
        if (self.page() > 1) {
            self.page(self.page() - 1);
            self.searchProperties();
        }
    };

    self.goEditProperty = function(key) {
        window.location.href = 'editProperty.jsp?key=' + encodeURIComponent(key);
    };

    self.resetSearch = function() {
        self.searchKey("");
        self.searchType("");
        self.page(1);
        self.searchProperties();
    };

    // Initial load
    self.searchProperties();
}

// --- Edit ViewModel ---
function EditPropertyViewModel(key) {
    var self = this;
    self.key = ko.observable("");
    self.type = ko.observable("");
    self.value = ko.observable("");

    // Fetch property
    $.getJSON(API_BASE + "/" + encodeURIComponent(key), function(resp) {
        if (resp && resp.data) {
            self.key(resp.data.key);
            self.type(resp.data.type);
            self.value(resp.data.value);
        } else {
            alert("Property not found");
            window.location.href = "properties.jsp";
        }
    });

    self.done = function() {
        var payload = {
            key: self.key(),
            value: self.value()
        };
        $.ajax({
            url: API_BASE + "/update",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload),
            success: function() {
                window.location.href = "properties.jsp";
            }
        });
    };

    self.cancel = function() {
        window.location.href = "properties.jsp";
    };
}

// --- Page Initialization ---
$(function() {
    var path = window.location.pathname;

    if (path.endsWith("properties.jsp")) {
        var vm = new PropertyListViewModel();
        ko.applyBindings({ propertyVM: vm });
    }
    else if (path.endsWith("editProperty.jsp")) {
        const urlParams = new URLSearchParams(window.location.search);
        const key = urlParams.get("key");
        if (!key) {
            alert("Missing property key");
            window.location.href = "properties.jsp";
            return;
        }
        var vm = new EditPropertyViewModel(key);
        ko.applyBindings(vm, document.getElementById("editPropertyForm"));
    }
});
