(function() {
    'use strict';
    angular
        .module('salesOrderApp')
        .factory('Shipment', Shipment);

    Shipment.$inject = ['$resource'];

    function Shipment ($resource) {
        var resourceUrl =  'api/shipments/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
